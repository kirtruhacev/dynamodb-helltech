package helltech.dynamodb.model;

import static helltech.dynamodb.DatabaseConstants.GSI2;
import static helltech.dynamodb.DatabaseConstants.GSI3;
import static helltech.dynamodb.DatabaseConstants.PK2;
import static helltech.dynamodb.DatabaseConstants.PK3;
import static helltech.dynamodb.DatabaseConstants.SK2;
import static helltech.dynamodb.DatabaseConstants.SK3;
import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class Publication extends Dao {

    protected static final String PUBLICATION = "Publication";
    private UUID institutionIdentifier;
    private UUID userIdentifier;

    public Publication() {

    }

    public Publication(UUID identifier, UUID userIdentifier, UUID institutionIdentifier) {
        super(identifier, PUBLICATION);
        this.userIdentifier = userIdentifier;
        this.institutionIdentifier = institutionIdentifier;
    }

    public static String type() {
        return PUBLICATION;
    }

    public static TableSchema<Publication> tableSchema() {
        return fromBean(Publication.class);
    }

    public UUID getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(UUID userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI2})
    @DynamoDbAttribute(PK2)
    public String getPk2() {
        return KEY_PATTERN.formatted(User.type(), this.userIdentifier);
    }

    public void setPk2(String pk2) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI2})
    @DynamoDbAttribute(SK2)
    public String getSk2() {
        return KEY_PATTERN.formatted(Publication.type(), getIdentifier());
    }

    public void setSk2(String sk2) {

    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI3})
    @DynamoDbAttribute(PK3)
    public String getPk3() {
        return KEY_PATTERN.formatted(Institution.type(), this.institutionIdentifier);
    }

    public void setPk3(String pk3) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI3})
    @DynamoDbAttribute(SK3)
    public String getSk3() {
        return KEY_PATTERN.formatted(Publication.type(), getIdentifier());
    }

    public void setSk3(String sk3) {

    }

    public UUID getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    public void setInstitutionIdentifier(UUID institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInstitutionIdentifier(), getUserIdentifier());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Publication that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(getInstitutionIdentifier(), that.getInstitutionIdentifier()) &&
               Objects.equals(getUserIdentifier(), that.getUserIdentifier());
    }
}
