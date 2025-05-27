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
import static java.util.Objects.isNull;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.ArrayList;
import java.util.Optional;
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

/**
 * A local DynamoDB compatible database.
 */
public class DynamoDBLocal {

    private static final String NULL_SORT_KEY = null;

    /**
     * A convenience method to create an instance of DynamoDBEmbedded with
     * a pre-configured table with some defaults for testing purposes and returns a client.
     *
     * @param tableName the name of the table to create.
     * @return A DynamoDBClient instance.
     */
    public static DynamoDbClient dynamoDBLocal(String tableName) {
        var database = DynamoDBEmbedded.create();
        var client = database.dynamoDbClient();
        createTable(tableName, client);
        return client;
    }

    private static void createTable(String tableName, DynamoDbClient client) {
        client.createTable(CreateTableRequest.builder()
                               .tableName(tableName)
                               .provisionedThroughput(createProvisionedThroughput())
                               .attributeDefinitions(createAttribute(PK0), createAttribute(SK0),
                                                     createAttribute(PK1), createAttribute(PK2), createAttribute(SK2),
                                                     createAttribute(PK3), createAttribute(SK3))
                               .globalSecondaryIndexes(createGlobalSecondaryIndex(GSI1, PK1, NULL_SORT_KEY),
                                                       createGlobalSecondaryIndex(GSI2, PK2, SK2),
                                                       createGlobalSecondaryIndex(GSI3, PK3, SK3))
                               .keySchema(createKeySchemaElements(PK0, SK0))
                               .build());
    }

    private static GlobalSecondaryIndex createGlobalSecondaryIndex(String name, String partitionKey, String sortKey) {
        return GlobalSecondaryIndex.builder()
                   .indexName(name)
                   .provisionedThroughput(createProvisionedThroughput())
                   .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                   .keySchema(createKeySchemaElements(partitionKey, sortKey))
                   .build();
    }

    private static ArrayList<KeySchemaElement> createKeySchemaElements(String partitionKey, String sortKey) {
        var keySchemas = new ArrayList<KeySchemaElement>();
        createSchemaElement(KeyType.HASH, partitionKey).ifPresentOrElse(keySchemas::add, IllegalArgumentException::new);
        createSchemaElement(KeyType.RANGE, sortKey).ifPresent(keySchemas::add);
        return keySchemas;
    }

    private static ProvisionedThroughput createProvisionedThroughput() {
        return ProvisionedThroughput.builder()
                   .readCapacityUnits(1000L)
                   .writeCapacityUnits(1000L)
                   .build();
    }

    private static Optional<KeySchemaElement> createSchemaElement(KeyType keyType, String attributeName) {
        return isNullSortKey(keyType, attributeName)
            ? Optional.empty()
            : Optional.of(KeySchemaElement.builder()
                   .keyType(keyType)
                   .attributeName(attributeName)
                   .build());
    }

    private static boolean isNullSortKey(KeyType keyType, String attributeName) {
        return KeyType.RANGE == keyType && isNull(attributeName);
    }

    private static AttributeDefinition createAttribute(String attributeName) {
        return AttributeDefinition.builder()
                   .attributeName(attributeName)
                   .attributeType(ScalarAttributeType.S)
                   .build();
    }
}
