package helltech.dynamodb.persistence.graphdb.dao;

import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.BASE_URI;
import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.ONTOLOGY;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.literal;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.property;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.resource;
import helltech.dynamodb.model.Institution;
import java.net.URI;
import java.util.UUID;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class InstitutionGraphDao implements GraphDao {
    private static final URI TYPE = URI.create(ONTOLOGY + "Institution");
    private final URI id;
    private final UUID identifier;

    public InstitutionGraphDao(URI id, UUID identifier) {
        this.id = id;
        this.identifier = identifier;
    }

    public static InstitutionGraphDao fromInstitution(Institution institution) {
        return new InstitutionGraphDao(createId(institution.identifier()), institution.identifier());
    }

    private static URI createId(UUID identifier) {
        return URI.create(BASE_URI + "institution/%s".formatted(identifier));
    }

    public static InstitutionGraphDao fromModel(Model model, URI uri) {
        var identifier = model.listStatements(model.createResource(uri.toString()),
                                              model.createProperty(ONTOLOGY, "identifier"),
                                              (RDFNode) null).next().getObject().asLiteral().getValue().toString();
        return new InstitutionGraphDao(uri, UUID.fromString(identifier));
    }

    public static InstitutionGraphDao fromIdentifier(UUID identifier) {
        return new InstitutionGraphDao(createId(identifier), identifier);
    }

    @Override
    public URI id() {
        return id;
    }

    @Override
    public Model toRdf(Model model) {
        var resource = resource(id, model);
        var typeStatement = model.createStatement(resource, RDF.type, resource(TYPE, model));

        var identifierStatement = model.createStatement(resource, property(IDENTIFIER_PROPERTY, model),
                                                        literal(identifier, model));
        model.add(typeStatement);
        model.add(identifierStatement);
        return model;
    }

    public Institution toInstitution() {
        return new Institution(identifier);
    }
}
