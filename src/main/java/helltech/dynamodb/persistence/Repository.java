package helltech.dynamodb.persistence;

import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This interface serves as an adapter that provides data access for the application and hides implementation details
 * for the persistence layer.
 */
public interface Repository {

    /**
     * Persist the data in the given DAO.
     * @param entity The data to write.
     */
    void save(Entity entity);

    /**
     * Fetch data for a User by identifier; the data may be empty if the identifier is not
     * associated with any known user.
     * @param identifier A UUID that identifies a User.
     * @return return User data if found, empty if not found.
     */
    Optional<User> fetchUserByIdentifier(UUID identifier);

    /**
     * Fetch data for an InstitutionDao by identifier; the data may be empty if the
     * identifier is not associated with any known InstitutionDao.
     * @param identifier A UUID that identifies an InstitutionDao.
     * @return return InstitutionDao data if found, empty if not found.
     */
    Optional<Institution> fetchInstitutionByIdentifier(UUID identifier);

    /**
     * Fetch data for a Publication by identifier; the data may be empty if the
     * identifier is not associated with any known Publication.
     * @param identifier A UUID that identifies a Publication.
     * @return return Publication data if found, empty if not found.
     */
    Optional<Publication> fetchPublicationByIdentifier(UUID identifier);

    /**
     * List all Users.
     * @return A list of Users.
     */
    List<User> listAllUsers();

    /**
     * List all Institutions.
     * @return A list of all Institutions.
     */
    List<Institution> listAllInstitutions();

    /**
     * List all Publications.
     * @return A list of all Publications.
     */
    List<Publication> listAllPublications();

    /**
     * List Publications associated with a User.
     * @param user A User.
     * @return A list of publications.
     */
    List<Publication> listPublicationsByUser(User user);

    /**
     * List Publications associated with an InstitutionDao.
     * @param institution An InstitutionDao.
     * @return A list of Publications.
     */
    List<Publication> listPublicationsByInstitution(Institution institution);

    /**
     * List all Users associated with an InstitutionDao.
     * @param institution An InstitutionDao.
     * @return A list of Users.
     */
    List<User> listAllUsersByInstitution(Institution institution);
}
