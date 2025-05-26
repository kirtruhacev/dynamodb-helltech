package helltech.dynamodb;

import static helltech.dynamodb.DatabaseConstants.GSI1;
import static helltech.dynamodb.DatabaseConstants.GSI2;
import static helltech.dynamodb.DatabaseConstants.GSI3;
import static helltech.dynamodb.DatabaseConstants.PK0;
import static helltech.dynamodb.DatabaseConstants.PK1;
import static helltech.dynamodb.DatabaseConstants.PK2;
import static helltech.dynamodb.DatabaseConstants.PK3;
import static helltech.dynamodb.DatabaseConstants.SK0;
import static helltech.dynamodb.DatabaseConstants.SK2;
import static helltech.dynamodb.DatabaseConstants.SK3;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class DynamoDBLocal {

    public static DynamoDbClient dynamoDBLocal() {
        var client = DynamoDBEmbedded.create().dynamoDbClient();
        client.createTable(CreateTableRequest.builder()
                               .tableName("table")
                               .provisionedThroughput(getProvisionedThroughput())
                               .attributeDefinitions(attribute(PK0), attribute(SK0),
                                                     attribute(PK1), attribute(PK2), attribute(SK2),
                                                     attribute(PK3), attribute(SK3))
                               .globalSecondaryIndexes(globalSecondaryIndex(GSI1, PK1),
                                                       globalSecondaryIndex(GSI2, PK2, SK2),
                                                       globalSecondaryIndex(GSI3, PK3, SK3))
                               .keySchema(schemaElement(KeyType.HASH, PK0), schemaElement(KeyType.RANGE, SK0))
                               .build());
        return client;
    }

    private static GlobalSecondaryIndex globalSecondaryIndex(String name, String partitionKey) {
        return GlobalSecondaryIndex.builder()
                   .indexName(name)
                   .provisionedThroughput(getProvisionedThroughput())
                   .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                   .keySchema(schemaElement(KeyType.HASH, partitionKey))
                   .build();
    }

    private static GlobalSecondaryIndex globalSecondaryIndex(String name, String partitionKey, String sortKey) {
        return GlobalSecondaryIndex.builder()
                   .indexName(name)
                   .provisionedThroughput(getProvisionedThroughput())
                   .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                   .keySchema(schemaElement(KeyType.HASH, partitionKey),
                              schemaElement(KeyType.RANGE, sortKey))
                   .build();
    }

    private static ProvisionedThroughput getProvisionedThroughput() {
        return ProvisionedThroughput.builder()
                   .readCapacityUnits(1000L)
                   .writeCapacityUnits(1000L)
                   .build();
    }

    private static KeySchemaElement schemaElement(KeyType keyType, String attributeName) {
        return KeySchemaElement.builder()
                   .keyType(keyType)
                   .attributeName(attributeName)
                   .build();
    }

    private static AttributeDefinition attribute(String attributeName) {
        return AttributeDefinition.builder()
                   .attributeName(attributeName)
                   .attributeType(ScalarAttributeType.S)
                   .build();
    }
}
