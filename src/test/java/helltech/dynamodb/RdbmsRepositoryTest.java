package helltech.dynamodb;

import helltech.dynamodb.persistence.rdbms.RdbmsDataSource;
import helltech.dynamodb.persistence.rdbms.RdbmsRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class RdbmsRepositoryTest extends AbstractTestBase {

    private static RdbmsRepository repository;

    @BeforeAll
    static void setUp() {
        repository = new RdbmsRepository(new RdbmsDataSource());
    }

    @BeforeEach
    void beforeEach() {
        setRepository(repository);
    }

    @AfterEach
    void tearDown() {
        repository.flush();
    }

    @AfterAll
    static void cleanUp() {
        repository.close();
    }
}
