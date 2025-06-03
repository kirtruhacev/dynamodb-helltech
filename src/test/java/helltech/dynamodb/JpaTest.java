package helltech.dynamodb;

import helltech.dynamodb.persistence.jpa.JpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class JpaTest extends AbstractTestBase {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    static void init() {
        // Initialize EntityManagerFactory once for all tests
        entityManagerFactory = Persistence.createEntityManagerFactory("examplePU");
    }

    @BeforeEach
    void setUp() {
        // Initialize EntityManager and Repository before each test
        entityManager = entityManagerFactory.createEntityManager();
        var repository = new JpaRepository();
        setRepository(repository);
        // Flush the database to ensure a clean state
        repository.flush();
    }

    @AfterEach
    void tearDown() {
        // Close EntityManager after each test
        entityManager.close();
    }
}
