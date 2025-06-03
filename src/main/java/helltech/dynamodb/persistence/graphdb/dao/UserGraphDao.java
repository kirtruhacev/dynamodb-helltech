package helltech.dynamodb.persistence.graphdb.dao;

import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.ONTOLOGY;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.literal;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.property;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.resource;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.graphdb.GraphDbConstants;
import java.net.URI;
import java.util.UUID;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;

public class UserGraphDao implements GraphDao {

    private static final URI TYPE = URI.create(ONTOLOGY + "User");
    private final URI id;
    private final UUID identifier;
    private final InstitutionGraphDao institution;

    public UserGraphDao(URI id, UUID identifier, InstitutionGraphDao institution) {
        this.id = id;
        this.identifier = identifier;
        this.institution = institution;
    }

    public static UserGraphDao fromUser(User user) {
        var id = createId(user.identifier());
        var identifier = user.identifier();
        var institution = InstitutionGraphDao.fromInstitution(user.institution());
        return new UserGraphDao(id, identifier, institution);
    }

    public static GraphDao fromIdentifier(UUID identifier) {
        return new UserGraphDao(createId(identifier), identifier, null);
    }

    public static UserGraphDao fromModel(Model model, URI id) {
        var identifier = model.listStatements(model.createResource(id.toString()),
                                              model.createProperty(GraphDbConstants.ONTOLOGY, "identifier"),
                                              (RDFNode) null).next().getObject().asLiteral().getValue().toString();
        var institution = model.listStatements(model.createResource(id.toString()),
                                               model.createProperty(GraphDbConstants.ONTOLOGY, "institution"),
                                               (RDFNode) null).next().getObject().asResource();
        return new UserGraphDao(id, UUID.fromString(identifier),
                                InstitutionGraphDao.fromModel(model, URI.create(institution.getURI())));
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
        var institutionStatement = model.createStatement(resource, property(INSTITUTION_PROPERTY, model),
                                                         resource(institution.id(), model));
        model.add(typeStatement);
        model.add(identifierStatement);
        model.add(institutionStatement);
        institution.toRdf(model);
        return model;
    }

    public User toUser() {
        return new User(identifier, institution.toInstitution());
    }

    private static URI createId(UUID identifier) {
        return URI.create(GraphDbConstants.BASE_URI + "user#" + identifier);
    }
}
