package helltech.dynamodb;

import helltech.dynamodb.model.Dao;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository {

    void save(Dao dao);

    Optional<User> fetchUserByIdentifier(UUID identifier);

    Optional<Institution> fetchInstitutionByIdentifier(UUID identifier);

    Optional<Publication> fetchPublicationByIdentifier(UUID identifier);

    List<User> listAllUsers();

    List<Institution> listAllInstitutions();

    List<Publication> listAllPublications();

    List<Publication> listPublicationsByUserIdentifier(UUID identifier);

    List<Publication> listPublicationsByInstitutionIdentifier(UUID identifier);

    List<User> listAllUsersByInstitutionIdentifier(UUID identifier);
}
