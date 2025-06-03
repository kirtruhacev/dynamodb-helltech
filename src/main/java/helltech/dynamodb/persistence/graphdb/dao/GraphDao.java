package helltech.dynamodb.persistence.graphdb.dao;

import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.ONTOLOGY;
import java.net.URI;
import java.util.UUID;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public interface GraphDao {
    URI IDENTIFIER_PROPERTY = URI.create(ONTOLOGY + "identifier");
    URI INSTITUTION_PROPERTY = URI.create(ONTOLOGY + "institution");

    URI id();
    Model toRdf(Model model);

    static Literal literal(UUID identifier, Model model) {
        return model.createLiteral(identifier.toString());
    }

    static Resource resource(URI uri, Model model) {
        return model.createResource(uri.toString());
    }

    static Property property(URI uri, Model model) {
        return model.createProperty(uri.toString());
    }
}
