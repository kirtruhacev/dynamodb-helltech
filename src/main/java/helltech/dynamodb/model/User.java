package helltech.dynamodb.model;

import static helltech.dynamodb.DatabaseConstants.GSI2;
import static helltech.dynamodb.DatabaseConstants.PK2;
import static helltech.dynamodb.DatabaseConstants.SK2;
import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import helltech.dynamodb.Generated;
import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class User extends Dao {

    protected static final String TYPE = "User";
    private UUID institutionIdentifier;

    public User() {
    }

    public User(UUID identifier, UUID institutionIdentifier) {
        super(identifier, TYPE);
        this.institutionIdentifier = institutionIdentifier;
    }

    public static String type() {
        return TYPE;
    }

    public static TableSchema<User> tableSchema() {
        return fromBean(User.class);
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
        return KEY_PATTERN.formatted(Institution.type(), this.institutionIdentifier);
    }

    public void setPk2(String pk2) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI2})
    @DynamoDbAttribute(SK2)
    public String getSk2() {
        return KEY_PATTERN.formatted(User.type(), getIdentifier());
    }

    public void setSk2(String sk2) {

    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(getInstitutionIdentifier(), user.getInstitutionIdentifier());
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInstitutionIdentifier());
    }
}