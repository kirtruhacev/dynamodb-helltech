package helltech.dynamodb.persistence.jpa;

import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import helltech.dynamodb.persistence.jpa.dao.Dao;
import helltech.dynamodb.persistence.jpa.dao.InstitutionDao;
import helltech.dynamodb.persistence.jpa.dao.PublicationDao;
import helltech.dynamodb.persistence.jpa.dao.UserDao;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaRepository implements Repository {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY
        = Persistence.createEntityManagerFactory("examplePU");

    @Override
    public void save(Entity entity) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(transformToDao(entity));
            entityManager.getTransaction().commit();
        }
    }

    private static Dao<? extends Entity> transformToDao(Entity entity) {
        return switch (entity) {
            case Publication publication -> PublicationDao.fromEntity(publication);
            case Institution institution -> InstitutionDao.fromEntity(institution);
            case User user -> UserDao.fromEntity(user);
        };
    }

    @Override
    public Optional<User> fetchUserByIdentifier(UUID identifier) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var user = entityManager.find(UserDao.class, identifier).toEntity();
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var institution = entityManager.find(InstitutionDao.class, identifier).toEntity();
            return Optional.ofNullable(institution);
        }
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var publication = entityManager.find(PublicationDao.class, identifier).toEntity();
            return Optional.ofNullable(publication);
        }
    }

    @Override
    public List<User> listAllUsers() {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery("SELECT u FROM UserDao u", UserDao.class);
            return toEntityList(query.getResultList());
        }
    }

    @Override
    public List<Institution> listAllInstitutions() {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery("SELECT i FROM InstitutionDao i", InstitutionDao.class);
            return toEntityList(query.getResultList());
        }
    }

    @Override
    public List<Publication> listAllPublications() {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery("SELECT p FROM PublicationDao p", PublicationDao.class);
            return toEntityList(query.getResultList());
        }
    }

    @Override
    public List<Publication> listPublicationsByUser(User user) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery(
                "SELECT p FROM PublicationDao p WHERE p.user = :user", PublicationDao.class);
            query.setParameter("user", UserDao.fromEntity(user));
            return toEntityList(query.getResultList());
        }
    }

    @Override
    public List<Publication> listPublicationsByInstitution(Institution institution) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery(
                "SELECT p FROM PublicationDao p WHERE p.institution = :institution", PublicationDao.class);
            query.setParameter("institution", InstitutionDao.fromEntity(institution));
            return toEntityList(query.getResultList());
        }
    }

    @Override
    public List<User> listAllUsersByInstitution(Institution institution) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            var query = entityManager.createQuery(
                "SELECT u FROM UserDao u WHERE u.institutionDao = :institution", UserDao.class);
            query.setParameter("institution", InstitutionDao.fromEntity(institution));
            return toEntityList(query.getResultList());
        }
    }

    public void flush() {

        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            entityManager.getTransaction().begin();

            // Necessarily ordered sequence of operations due to constraints
            entityManager.createNativeQuery("DELETE FROM \"PUBLICATIONS\"").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM \"USERS\"").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM \"INSTITUTIONS\"").executeUpdate();

            entityManager.getTransaction().commit();
        }
    }

    private <T extends Entity> List<T> toEntityList(List<? extends Dao<T>> resultList) {
        return resultList.stream()
                   .map(Dao::toEntity)
                   .toList();
    }
}
