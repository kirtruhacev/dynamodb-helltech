package helltech.dynamodb;

import static helltech.dynamodb.DatabaseConstants.GSI1;
import static helltech.dynamodb.DatabaseConstants.GSI2;
import static helltech.dynamodb.DatabaseConstants.GSI3;
import static helltech.dynamodb.DatabaseConstants.TABLE_NAME;
import helltech.dynamodb.model.Dao;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
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

    private final DynamoDbTable<Publication> publicationTable;
    private final DynamoDbTable<User> userTable;
    private final DynamoDbTable<Institution> institutionTable;

    public DynamoDBRepository(DynamoDbClient client) {
        var enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        this.publicationTable = enhancedClient.table(TABLE_NAME, Publication.tableSchema());
        this.userTable = enhancedClient.table(TABLE_NAME, User.tableSchema());
        this.institutionTable = enhancedClient.table(TABLE_NAME, Institution.tableSchema());
    }

    @Override
    public void save(Dao dao) {
        switch (dao) {
            case Publication publication -> publicationTable.putItem(publication);
            case Institution institution -> institutionTable.putItem(institution);
            case User user -> userTable.putItem(user);
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public Optional<User> fetchUserByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(User.type(), identifier);
        var sortValue = Dao.sortKey(User.type(), identifier);
        return Optional.ofNullable(userTable.getItem(key(partitionValue, sortValue)));
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(Institution.type(), identifier);
        var sortValue = Dao.sortKey(Institution.type(), identifier);
        return Optional.ofNullable(institutionTable.getItem(key(partitionValue, sortValue)));
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        var partitionValue = Dao.partitionKey(Publication.type(), identifier);
        var sortValue = Dao.sortKey(Publication.type(), identifier);
        return Optional.ofNullable(publicationTable.getItem(key(partitionValue, sortValue)));
    }

    @Override
    public List<User> listAllUsers() {
        return userTable.index(GSI1).query(query(User.type())).stream().map(Page::items).flatMap(List::stream).toList();
    }

    @Override
    public List<Institution> listAllInstitutions() {
        return institutionTable.index(GSI1)
                   .query(query(Institution.type()))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .toList();
    }

    @Override
    public List<Publication> listAllPublications() {
        return publicationTable.index(GSI1)
                   .query(query(Publication.type()))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByUserIdentifier(UUID identifier) {
        return publicationTable.index(GSI2)
                   .query(query(Dao.partitionKey(User.type(), identifier)))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByInstitutionIdentifier(UUID identifier) {
        return publicationTable.index(GSI3)
                   .query(query(Dao.partitionKey(Institution.type(), identifier)))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
                   .toList();
    }

    @Override
    public List<User> listAllUsersByInstitutionIdentifier(UUID identifier) {
        return userTable.index(GSI2)
                   .query(query(Dao.partitionKey(Institution.type(), identifier)))
                   .stream()
                   .map(Page::items)
                   .flatMap(List::stream)
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
