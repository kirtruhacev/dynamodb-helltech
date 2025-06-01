package helltech.dynamodb.persistence.dynamodb.dao;

import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI3;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.PK2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.PK3;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.SK2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.SK3;
import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;
import helltech.dynamodb.annotations.Generated;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class PublicationDao extends Dao {

    protected static final String TYPE = "Publication";
    private UUID institutionIdentifier;
    private UUID userIdentifier;

    public PublicationDao() {

    }

    public PublicationDao(UUID identifier, User user, Institution institution) {
        super(identifier, TYPE);
        this.userIdentifier = user.identifier();
        this.institutionIdentifier = institution.identifier();
    }

    public static String type() {
        return TYPE;
    }

    public static TableSchema<PublicationDao> tableSchema() {
        return fromBean(PublicationDao.class);
    }

    public static PublicationDao fromPublication(Publication publication) {
        return new PublicationDao(publication.identifier(), publication.user(), publication.institution());
    }

    public Publication toPublication() {
        return new Publication(getIdentifier(),
                               new User(getUserIdentifier(), new Institution(getInstitutionIdentifier())),
                               new Institution(getInstitutionIdentifier()));
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
        return KEY_PATTERN.formatted(UserDao.type(), this.userIdentifier);
    }

    public void setPk2(String pk2) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI2})
    @DynamoDbAttribute(SK2)
    public String getSk2() {
        return KEY_PATTERN.formatted(PublicationDao.type(), getIdentifier());
    }

    public void setSk2(String sk2) {

    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI3})
    @DynamoDbAttribute(PK3)
    public String getPk3() {
        return KEY_PATTERN.formatted(InstitutionDao.type(), this.institutionIdentifier);
    }

    public void setPk3(String pk3) {

    }

    @DynamoDbSecondarySortKey(indexNames = {GSI3})
    @DynamoDbAttribute(SK3)
    public String getSk3() {
        return KEY_PATTERN.formatted(PublicationDao.type(), getIdentifier());
    }

    public void setSk3(String sk3) {

    }

    public UUID getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    public void setInstitutionIdentifier(UUID institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInstitutionIdentifier(), getUserIdentifier());
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PublicationDao that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(getInstitutionIdentifier(), that.getInstitutionIdentifier()) &&
               Objects.equals(getUserIdentifier(), that.getUserIdentifier());
    }
}
