package helltech.dynamodb;

import static helltech.dynamodb.DynamoDBLocal.dynamoDBLocal;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

class DynamoDBRepositoryTest {

    private Repository repository;
    private DynamoDbClient client;

    @BeforeEach
    void setUp() {
        client = dynamoDBLocal();
        this.repository = new DynamoDBRepository(client);
    }

    @AfterEach
    void tearDown() {
        client.deleteTable(DeleteTableRequest.builder().tableName("table").build());
    }

    @Test
    void shouldFetchUserByIdentifier() {
        var user = randomUser(randomUUID());
        repository.save(user);

        var persistedUser = repository.fetchUserByIdentifier(user.getIdentifier()).orElseThrow();

        assertEquals(user, persistedUser);
    }

    @Test
    void shouldFetchInstitutionByIdentifier() {
        var institution = new Institution(randomUUID());
        repository.save(institution);

        var persistedInstitution = repository.fetchInstitutionByIdentifier(institution.getIdentifier()).orElseThrow();

        assertEquals(institution, persistedInstitution);
    }

    @Test
    void shouldFetchPublicationByIdentifier() {
        var publication = new Publication(randomUUID(), randomUUID(), randomUUID());
        repository.save(publication);

        var persistedPublication = repository.fetchPublicationByIdentifier(publication.getIdentifier()).orElseThrow();

        assertEquals(publication, persistedPublication);
    }

    @Test
    void shouldListAllUsers() {
        repository.save(randomUser(randomUUID()));
        repository.save(randomUser(randomUUID()));

        var users = repository.listAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void shouldListAllInstitutions() {
        repository.save(randomInstitution());
        repository.save(randomInstitution());

        var institutions = repository.listAllInstitutions();

        assertEquals(2, institutions.size());
    }

    @Test
    void shouldListAllPublications() {
        repository.save(randomPublication(randomUUID(), randomUUID()));
        repository.save(randomPublication(randomUUID(), randomUUID()));

        var publications = repository.listAllPublications();

        assertEquals(2, publications.size());
    }

    @Test
    void shouldListAllPublicationsByUserIdentifier() {
        var user = randomUser(randomUUID());
        repository.save(user);
        repository.save(randomPublication(user.getIdentifier(), randomUUID()));
        repository.save(randomPublication(user.getIdentifier(), randomUUID()));

        var publications = repository.listPublicationsByUserIdentifier(user.getIdentifier());

        assertEquals(2, publications.size());
    }

    @Test
    void shouldListAllPublicationsByInstitutionIdentifier() {
        var institution = randomInstitution();
        repository.save(institution);
        repository.save(randomPublication(randomUUID(), institution.getIdentifier()));
        repository.save(randomPublication(randomUUID(), institution.getIdentifier()));

        var publications = repository.listPublicationsByInstitutionIdentifier(institution.getIdentifier());

        assertEquals(2, publications.size());
    }

    @Test
    void shouldListAllUsersByInstitutionIdentifier() {
        var institution = randomInstitution();

        repository.save(randomUser(institution.getIdentifier()));
        repository.save(randomUser(institution.getIdentifier()));

        var publications = repository.listAllUsersByInstitutionIdentifier(institution.getIdentifier());

        assertEquals(2, publications.size());
    }

    private Publication randomPublication(UUID userIdentifier, UUID institutionIdentifier) {
        return new Publication(randomUUID(), userIdentifier, institutionIdentifier);
    }

    private Institution randomInstitution() {
        return new Institution(randomUUID());
    }

    private static User randomUser(UUID institutionIdentifier) {
        return new User(randomUUID(), institutionIdentifier);
    }
}