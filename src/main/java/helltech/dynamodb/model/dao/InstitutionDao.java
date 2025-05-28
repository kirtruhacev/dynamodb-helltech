package helltech.dynamodb.model.dao;

import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import helltech.dynamodb.model.business.Institution;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class InstitutionDao extends Dao {

    protected static final String TYPE = "Institution";

    public InstitutionDao() {
    }

    public InstitutionDao(Institution institution) {
        super(institution.identifier(), TYPE);
    }

    public static String type() {
        return TYPE;
    }

    public static TableSchema<InstitutionDao> tableSchema() {
        return fromBean(InstitutionDao.class);
    }

    public static InstitutionDao fromInstitution(Institution institution) {
        return new InstitutionDao(institution);
    }

    public Institution toInstitution() {
        return new Institution(getIdentifier());
    }
}