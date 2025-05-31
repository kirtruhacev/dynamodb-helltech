package helltech.dynamodb.persistence.dynamodb.dao;

import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.PK2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.SK2;
import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import helltech.dynamodb.annotations.Generated;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.User;
import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class UserDao extends Dao {

    protected static final String TYPE = "User";
    private UUID institutionIdentifier;

    public UserDao() {
    }

    public UserDao(UUID identifier, UUID institutionIdentifier) {
        super(identifier, TYPE);
        this.institutionIdentifier = institutionIdentifier;
    }

    public static String type() {
        return TYPE;
    }

    public static TableSchema<UserDao> tableSchema() {
        return fromBean(UserDao.class);
    }

    public static UserDao fromUser(User user) {
        return new UserDao(user.identifier(), user.institution().identifier());
    }

    public UUID getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    public void setInstitutionIdentifier(UUID institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI2})
    @DynamoDbAttribute(PK2)
    public String getPk2() {
        return KEY_PATTERN.formatted(InstitutionDao.type(), this.institutionIdentifier);
    }

    public void setPk2(String pk2) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI2})
    @DynamoDbAttribute(SK2)
    public String getSk2() {
        return KEY_PATTERN.formatted(UserDao.type(), getIdentifier());
    }

    public void setSk2(String sk2) {

    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInstitutionIdentifier());
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDao user)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(getInstitutionIdentifier(), user.getInstitutionIdentifier());
    }

    public User toUser() {
        return new User(this.getIdentifier(), new Institution(this.getInstitutionIdentifier()));
    }
}