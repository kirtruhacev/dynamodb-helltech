package helltech.dynamodb;

import helltech.dynamodb.persistence.graphdb.GraphDatabase;
import helltech.dynamodb.persistence.graphdb.GraphRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class GraphRepositoryTest extends AbstractTestBase {

    private static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();

    @BeforeEach
    void setup() {
        setRepository(new GraphRepository(new GraphDatabase(DEFAULT_MODEL)));
    }

    @AfterEach
    void cleanup() {
        DEFAULT_MODEL.removeAll();
    }

}
