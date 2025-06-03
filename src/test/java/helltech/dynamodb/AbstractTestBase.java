package helltech.dynamodb;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

abstract class AbstractTestBase {

    private Repository repository;

    void setRepository(Repository repository) {
        this.repository = repository;
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
        var publication = createPublicationWithUniqueUserAtUniqueOrganisation();
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

    private Institution createInstitutionWithUsers(int numberOfUsers) {
        var institution = createInstitution();
        daoCreator(numberOfUsers, () -> createUser(institution));
        return institution;
    }

    private User createUserWithPublications(int publications) {
        var user = createUser();
        daoCreator(publications, () -> createPublication(user, createInstitution()));
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

    private static User randomUser(Institution institution) {
        return new User(randomUUID(), institution);
    }

    private void daoCreator(int num, Supplier<Entity> supplier) {
        IntStream.range(0, num)
            .forEach(ignored -> supplier.get());
    }
}
