package helltech.dynamodb.model;

import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Institution extends Dao {

    protected static final String INSTITUTION = "Institution";

    public Institution() {
    }

    public Institution(UUID identifier) {
        super(identifier, INSTITUTION);
    }

    public static Institution fromDao(Dao dao) {
        return new Institution(dao.getIdentifier());
    }

    public static String type() {
        return INSTITUTION;
    }
}