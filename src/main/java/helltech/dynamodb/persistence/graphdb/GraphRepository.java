package helltech.dynamodb.persistence.graphdb;

import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import helltech.dynamodb.persistence.graphdb.dao.InstitutionGraphDao;
import helltech.dynamodb.persistence.graphdb.dao.PublicationGraphDao;
import helltech.dynamodb.persistence.graphdb.dao.UserGraphDao;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GraphRepository implements Repository {

    private final GraphDatabase database;

    public GraphRepository(GraphDatabase database) {
        this.database = database;
    }

    @Override
    public void save(Entity entity) {
        var data = switch (entity) {
            case Publication publication -> PublicationGraphDao.fromPublication(publication);
            case Institution institution -> InstitutionGraphDao.fromInstitution(institution);
            case User user -> UserGraphDao.fromUser(user);
        };
        database.save(data);
    }

    @Override
    public Optional<User> fetchUserByIdentifier(UUID identifier) {

        return database.fetchUserByIdentifier(UserGraphDao.fromIdentifier(identifier).id())
                   .map(UserGraphDao::toUser);
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        return database.fetchInstitutionByIdentifier(InstitutionGraphDao.fromIdentifier(identifier).id())
                   .map(InstitutionGraphDao::toInstitution);
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        return database.fetchPublicationByIdentifier(PublicationGraphDao.fromIdentifier(identifier).id())
                   .map(PublicationGraphDao::toPublication);
    }

    @Override
    public List<User> listAllUsers() {
        return database.listAllUsers().stream()
                   .map(UserGraphDao::toUser)
                   .toList();
    }

    @Override
    public List<Institution> listAllInstitutions() {
        return database.listAllInstitutions().stream()
                   .map(InstitutionGraphDao::toInstitution)
                   .toList();
    }

    @Override
    public List<Publication> listAllPublications() {
        return database.listAllPublications().stream()
                   .map(PublicationGraphDao::toPublication)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByUser(User user) {
        return database.listPublicationsByUser(user).stream()
                   .map(PublicationGraphDao::toPublication)
                   .toList();
    }

    @Override
    public List<Publication> listPublicationsByInstitution(Institution institution) {
        return database.listPublicationsByInstitution(institution).stream()
                   .map(PublicationGraphDao::toPublication)
                   .toList();
    }

    @Override
    public List<User> listAllUsersByInstitution(Institution institution) {
        return database.listAllUsersByInstitution(institution).stream()
                   .map(UserGraphDao::toUser)
                   .toList();
    }
}
