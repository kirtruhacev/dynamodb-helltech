package helltech.dynamodb;

import static helltech.dynamodb.DynamoDBLocal.dynamoDBLocal;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import helltech.dynamodb.persistence.dynamodb.DynamoDBRepository;
import helltech.dynamodb.persistence.dynamodb.DynamoDbConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

class DynamoDBRepositoryTest extends AbstractTestBase {

    private static final String MY_TABLE = DynamoDbConstants.TABLE_NAME;
    public static final AmazonDynamoDBLocal database = DynamoDBEmbedded.create();
    private static DynamoDbClient client;

    @BeforeEach
    void setUp() {
        client = dynamoDBLocal(database, MY_TABLE);
        setRepository(new DynamoDBRepository(client));
    }

    @AfterEach
    void tearDown() {
        client.deleteTable(DeleteTableRequest.builder().tableName(MY_TABLE).build());
    }
}