package helltech.dynamodb.persistence.dynamodb;

import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI1;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI2;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI3;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.TABLE_NAME;
import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import helltech.dynamodb.persistence.dynamodb.dao.Dao;
import helltech.dynamodb.persistence.dynamodb.dao.InstitutionDao;
import helltech.dynamodb.persistence.dynamodb.dao.PublicationDao;
import helltech.dynamodb.persistence.dynamodb.dao.UserDao;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBRepository implements Repository {

    private final DynamoDbTable<PublicationDao> publicationTable;
    private final DynamoDbTable<UserDao> userTable;
    private final DynamoDbTable<InstitutionDao> institutionTable;

    public DynamoDBRepository(DynamoDbClient client) {
        var enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        this.publicationTable = enhancedClient.table(TABLE_NAME, PublicationDao.tableSchema());
        this.userTable = enhancedClient.table(TABLE_NAME, UserDao.tableSchema());
        this.institutionTable = enhancedClient.table(TABLE_NAME, InstitutionDao.tableSchema());
    }

    @Override
    public void save(Entity entity) {
        switch (entity) {
            case Publication publication -> publicationTable.putItem(PublicationDao.fromPublication(publication));
            case Institution institution -> institutionTable.putItem(InstitutionDao.fromInstitution(institution));
            case User user -> userTable.putItem(UserDao.fromUser(user));
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public Optional<User> fetchUserByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(UserDao.type(), identifier);
        var sortValue = Dao.sortKey(UserDao.type(), identifier);
        return Optional.ofNullable(userTable.getItem(key(partitionValue, sortValue)))
                   .map(UserDao::toUser);
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(InstitutionDao.type(), identifier);
        var sortValue = Dao.sortKey(InstitutionDao.type(), identifier);
        return Optional.ofNullable(institutionTable.getItem(key(partitionValue, sortValue)))
                   .map(InstitutionDao::toInstitution);
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(PublicationDao.type(), identifier);
        var sortValue = Dao.sortKey(PublicationDao.type(), identifier);
        return Optional.ofNullable(publicationTable.getItem(key(partitionValue, sortValue)))
                   .map(PublicationDao::toPublication);
    }

    @Override
    public List<User> listAllUsers() {
        return userTable.index(GSI1).query(query(UserDao.type())).stream().map(Page::items).flatMap(List::stream).map(UserDao::toUser)
                   .toList();
    }

    @Override
    public List<Institution> listAllInstitutions() {
        return institutionTable.index(GSI1)
                   .query(query(InstitutionDao.type()))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .map(InstitutionDao::toInstitution)
                   .toList();
    }

    @Override
    public List<Publication> listAllPublications() {
        return publicationTable.index(GSI1)
                   .query(query(PublicationDao.type()))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .map(PublicationDao::toPublication)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByUser(User user) {
        return publicationTable.index(GSI2)
                   .query(query(Dao.partitionKey(UserDao.type(), user.identifier())))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .map(PublicationDao::toPublication)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByInstitution(Institution institution) {
        return publicationTable.index(GSI3)
                   .query(query(Dao.partitionKey(InstitutionDao.type(), institution.identifier())))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .map(PublicationDao::toPublication)
                   .toList();
    }

    @Override
    public List<User> listAllUsersByInstitution(Institution institution) {
        return userTable.index(GSI2)
                   .query(query(Dao.partitionKey(InstitutionDao.type(), institution.identifier())))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .map(UserDao::toUser)
                   .toList();
    }

    private static QueryConditional query(String partitionValue) {
        return QueryConditional.keyEqualTo(key(partitionValue));
    }

    private static Key key(String partitionValue) {
        return Key.builder().partitionValue(partitionValue).build();
    }

    private static Key key(String partitionValue, String sortValue) {
        return Key.builder().partitionValue(partitionValue).sortValue(sortValue).build();
    }
}
