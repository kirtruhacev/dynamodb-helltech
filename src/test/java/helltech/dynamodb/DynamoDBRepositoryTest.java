package helltech.dynamodb;

import static helltech.dynamodb.DynamoDBLocal.dynamoDBLocal;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.dynamodb.DynamoDbConstants;
import helltech.dynamodb.persistence.dynamodb.DynamoDBRepository;
import helltech.dynamodb.persistence.Repository;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

class DynamoDBRepositoryTest {

    private static final String MY_TABLE = DynamoDbConstants.TABLE_NAME;
    public static final AmazonDynamoDBLocal database = DynamoDBEmbedded.create();
    private DynamoDbClient client;
    private Repository repository;

    @BeforeEach
    void setUp() {
        client = dynamoDBLocal(database, MY_TABLE);
        repository = new DynamoDBRepository(client);
    }

    @AfterEach
    void tearDown() {
        client.deleteTable(DeleteTableRequest.builder().tableName(MY_TABLE).build());
    }

    @Test
    void shouldFetchUserByIdentifier() {
        var user = createUser();
        var persistedUser = repository.fetchUserByIdentifier(user.identifier()).orElseThrow();
        assertEquals(user, persistedUser);
    }

    @Test
    void shouldFetchInstitutionByIdentifier() {
        var institution = createInstitution();
        var persistedInstitution = repository.fetchInstitutionByIdentifier(institution.identifier()).orElseThrow();
        assertEquals(institution, persistedInstitution);
    }

    @Test
    void shouldFetchPublicationByIdentifier() {
        var publication = createPublication();
        var persistedPublication = repository.fetchPublicationByIdentifier(publication.identifier()).orElseThrow();
        assertEquals(publication, persistedPublication);
    }

    @Test
    void shouldListAllUsers() {
        var numberOfUsers = 2;
        daoCreator(numberOfUsers, () -> createUser(createInstitution()));
        var users = repository.listAllUsers();
        assertEquals(numberOfUsers, users.size());
    }

    @Test
    void shouldListAllInstitutions() {
        var numberOfInstitutions = 2;
        daoCreator(numberOfInstitutions, this::createInstitution);
        var institutions = repository.listAllInstitutions();
        assertEquals(numberOfInstitutions, institutions.size());
    }

    @Test
    void shouldListAllPublications() {
        var numberOfPublications = 2;
        daoCreator(numberOfPublications, this::createPublicationWithUniqueUserAtUniqueOrganisation);
        var publications = repository.listAllPublications();
        assertEquals(numberOfPublications, publications.size());
    }

    @Test
    void shouldListAllPublicationsByUserIdentifier() {
        var numberOfPublications = 2;
        var user = createUserWithPublications(numberOfPublications);
        var fetchUser = repository.fetchUserByIdentifier(user.identifier()).orElseThrow();
        var publications = repository.listPublicationsByUser(fetchUser);
        assertEquals(numberOfPublications, publications.size());
    }

    @Test
    void shouldListAllPublicationsByInstitutionIdentifier() {
        var numberOfPublications = 2;
        var institution = createInstitutionWithPublications(numberOfPublications);
        var publications = repository.listPublicationsByInstitution(institution);
        assertEquals(numberOfPublications, publications.size());
    }

    @Test
    void shouldListAllUsersByInstitution() {
        var numberOfUsers = 2;
        var institution = createInstitutionWithUsers(numberOfUsers);
        var users = repository.listAllUsersByInstitution(institution);
        assertEquals(numberOfUsers, users.size());
    }

    private Entity createPublicationWithUniqueUserAtUniqueOrganisation() {
        var institution = createInstitution();
        return createPublication(createUser(institution), institution);
    }

    private Publication createPublication(User user, Institution institution) {
        var publication = new Publication(randomUUID(), user, institution);
        repository.save(publication);
        return publication;
    }

    private Publication createPublication() {
        var institution = createInstitution();
        return createPublication(createUser(institution), institution);
    }

    private Institution createInstitutionWithUsers(int numberOfUsers) {
        var institution = createInstitution();
        daoCreator(numberOfUsers, () -> createUser(institution));
        return institution;
    }

    private User createUserWithPublications(int publications) {
        var user = createUser();
        daoCreator(publications, () -> createPublication(user, randomInstitution()));
        return user;
    }

    private Institution createInstitutionWithPublications(int publications) {
        var institution = createInstitution();
        daoCreator(publications, () -> createPublication(createUser(institution), institution));
        return institution;
    }

    private Institution createInstitution() {
        var institution = new Institution(randomUUID());
        repository.save(institution);
        return institution;
    }

    private User createUser() {
        return createUser(createInstitution());
    }

    private User createUser(Institution institution) {
        var user = randomUser(institution);
        repository.save(user);
        return user;
    }

    private Institution randomInstitution() {
        return new Institution(randomUUID());
    }

    private static User randomUser(Institution institution) {
        return new User(randomUUID(), institution);
    }

    private void daoCreator(int num, Supplier<Entity> supplier) {
        IntStream.range(0, num)
            .forEach(ignored -> supplier.get());
    }
}