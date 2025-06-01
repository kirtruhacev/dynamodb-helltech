package helltech.dynamodb.persistence.graphdb.dao;

import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.BASE_URI;
import static helltech.dynamodb.persistence.graphdb.GraphDbConstants.ONTOLOGY;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.literal;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.property;
import static helltech.dynamodb.persistence.graphdb.dao.GraphDao.resource;
import helltech.dynamodb.model.Publication;
import java.net.URI;
import java.util.UUID;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;

public class PublicationGraphDao implements GraphDao {

    private static final URI TYPE = URI.create(ONTOLOGY + "Publication");
    private static final URI USER_PROPERTY = URI.create(ONTOLOGY + "user");
    private final URI id;
    private final UUID identifier;
    private final UserGraphDao user;
    private final InstitutionGraphDao institution;

    public PublicationGraphDao(URI id,
                               UUID identifier,
                               UserGraphDao user,
                               InstitutionGraphDao institution) {
        this.id = id;
        this.identifier = identifier;
        this.user = user;
        this.institution = institution;
    }

    public static PublicationGraphDao fromPublication(Publication publication) {
        var id = createId(publication.identifier());
        var user = publication.user();
        var institution = publication.institution();

        return new PublicationGraphDao(id,
                                       publication.identifier(),
                                       UserGraphDao.fromUser(user),
                                       InstitutionGraphDao.fromInstitution(institution));
    }

    public static GraphDao fromIdentifier(UUID identifier) {
        return new PublicationGraphDao(createId(identifier), identifier, null, null);
    }

    public static PublicationGraphDao fromModel(Model model, URI uri) {
        var resource = model.createResource(uri.toString());
        var identifier = model.listStatements(resource,
                                              model.createProperty(ONTOLOGY, "identifier"),
                                              (RDFNode) null).next().getObject().asLiteral().getValue().toString();
        var user = model.listStatements(resource, property(USER_PROPERTY, model), (RDFNode) null).next().getObject().asResource().toString();
        var institution = model.listStatements(resource, property(INSTITUTION_PROPERTY, model), (RDFNode) null).next().getObject().asResource().toString();
        return new PublicationGraphDao(uri, UUID.fromString(identifier), UserGraphDao.fromModel(model, URI.create(user)),
                                       InstitutionGraphDao.fromModel(model, URI.create(institution)));
    }

    private static URI createId(UUID identifier) {
        return URI.create(BASE_URI + "publications/%s".formatted(identifier));
    }

    public Publication toPublication() {
        return new Publication(identifier, user.toUser(), institution.toInstitution());
    }

    @Override
    public URI id() {
        return id;
    }

    @Override
    public Model toRdf(Model model) {
        var resource = resource(id, model);
        var typeStatement = model.createStatement(resource, RDF.type, resource(TYPE, model));
        var userStatement = model.createStatement(resource, property(USER_PROPERTY, model), resource(user.id(), model));
        var institutitonStatement = model.createStatement(resource, property(INSTITUTION_PROPERTY, model),
                                                          resource(institution.id(), model));
        var identifierStatement = model.createStatement(resource, property(IDENTIFIER_PROPERTY, model),
                                                        literal(identifier, model));

        model.add(typeStatement);
        model.add(identifierStatement);
        model.add(userStatement);
        model.add(institutitonStatement);
        user.toRdf(model);
        institution.toRdf(model);
        return model;
    }
}
