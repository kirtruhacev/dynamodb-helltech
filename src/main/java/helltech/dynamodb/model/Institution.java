package helltech.dynamodb.model;

import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Institution extends Dao {

    protected static final String TYPE = "Institution";

    public Institution() {
    }

    public Institution(UUID identifier) {
        super(identifier, TYPE);
    }

    public static String type() {
        return TYPE;
    }

    public static TableSchema<Institution> tableSchema() {
        return fromBean(Institution.class);
    }
}