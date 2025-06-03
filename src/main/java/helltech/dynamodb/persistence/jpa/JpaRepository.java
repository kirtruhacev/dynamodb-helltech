package helltech.dynamodb.persistence.jpa;

import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import helltech.dynamodb.persistence.jpa.dao.InstitutionDao;
import helltech.dynamodb.persistence.jpa.dao.PublicationDao;
import helltech.dynamodb.persistence.jpa.dao.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaRepository implements Repository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("examplePU");

    @Override
    public void save(Entity entity) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        var dao = switch (entity) {
            case Publication publication -> PublicationDao.fromEntity(publication);
            case Institution institution -> InstitutionDao.fromEntity(institution);
            case User user -> UserDao.fromEntity(user);
        };
        em.persist(dao);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<User> fetchUserByIdentifier(UUID identifier) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(UserDao.class, identifier).toUser();
        em.close();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        EntityManager em = emf.createEntityManager();
        Institution institution = em.find(InstitutionDao.class, identifier).toInstitution();
        em.close();
        return Optional.ofNullable(institution);
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        EntityManager em = emf.createEntityManager();
        Publication publication = em.find(PublicationDao.class, identifier).toPublication();
        em.close();
        return Optional.ofNullable(publication);
    }

    @Override
    public List<User> listAllUsers() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<UserDao> query = em.createQuery("SELECT u FROM UserDao u", UserDao.class);
        List<User> users = query.getResultList().stream()
                               .map(UserDao::toUser)
                               .toList();
        em.close();
        return users;
    }

    @Override
    public List<Institution> listAllInstitutions() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<InstitutionDao> query = em.createQuery("SELECT i FROM InstitutionDao i", InstitutionDao.class);
        List<Institution> institutions = query.getResultList().stream()
                                                .map(InstitutionDao::toInstitution)
                                             .toList();
        em.close();
        return institutions;
    }

    @Override
    public List<Publication> listAllPublications() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<PublicationDao> query = em.createQuery("SELECT p FROM PublicationDao p", PublicationDao.class);
        List<Publication> publications = query.getResultList().stream()
                                             .map(PublicationDao::toPublication)
                                             .toList();
        em.close();
        return publications;
    }

    @Override
    public List<Publication> listPublicationsByUser(User user) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<PublicationDao> query = em.createQuery(
            "SELECT p FROM PublicationDao p WHERE p.user = :user", PublicationDao.class);
        query.setParameter("user", UserDao.fromEntity(user));
        List<Publication> publications = query.getResultList().stream()
                                             .map(PublicationDao::toPublication)
                                             .toList();
        em.close();
        return publications;
    }

    @Override
    public List<Publication> listPublicationsByInstitution(Institution institution) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<PublicationDao> query = em.createQuery(
            "SELECT p FROM PublicationDao p WHERE p.institution = :institution", PublicationDao.class);
        query.setParameter("institution", InstitutionDao.fromEntity(institution));
        List<Publication> publications = query.getResultList().stream()
                                             .map(PublicationDao::toPublication)
                                             .toList();
        em.close();
        return publications;
    }

    @Override
    public List<User> listAllUsersByInstitution(Institution institution) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<UserDao> query = em.createQuery(
            "SELECT u FROM UserDao u WHERE u.institutionDao = :institution", UserDao.class);
        query.setParameter("institution", InstitutionDao.fromEntity(institution));
        List<User> users = query.getResultList().stream()
                               .map(UserDao::toUser)
                               .toList();
        em.close();
        return users;
    }

    public void flush() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Necessarily ordered sequence of operations due to constraints
        em.createNativeQuery("DELETE FROM \"PUBLICATIONS\"").executeUpdate();
        em.createNativeQuery("DELETE FROM \"USERS\"").executeUpdate();
        em.createNativeQuery("DELETE FROM \"INSTITUTIONS\"").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
