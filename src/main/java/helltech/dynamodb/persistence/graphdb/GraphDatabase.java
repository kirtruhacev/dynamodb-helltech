package helltech.dynamodb.persistence.graphdb;

import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.graphdb.dao.GraphDao;
import helltech.dynamodb.persistence.graphdb.dao.InstitutionGraphDao;
import helltech.dynamodb.persistence.graphdb.dao.PublicationGraphDao;
import helltech.dynamodb.persistence.graphdb.dao.UserGraphDao;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

/**
 * Speed-run of implementation with SPARQL. Probably consider e.g. Graph Store Protocol rather than
 */
public class GraphDatabase {

    private static final String INSTITUTION_IDENTIFIER_VAR = "institutionIdentifier";
    private static final String INSTITUTION_VAR = "institution";
    private static final String INSTITUTION_URI_VAR = "institutionUri";
    private static final String USER_INSTITUTION_URI_VAR = "userInstitutionUri";
    private static final String USER_INSTITUTION_IDENTIFIER_VAR = "userInstitutionIdentifier";
    private static final String USER_URI_VAR = "userUri";
    private static final String USER_IDENTIFIER_VAR = "userIdentifier";
    private static final String PUBLICATION_URI_VAR = "publicationUri";
    private static final String PUBLICATION_IDENTIFIER_VAR = "publicationIdentifier";
    private final Model model;

    public GraphDatabase(Model model) {
        this.model = model;
    }

    /**
     * This is a nasty hack, but the data is concatenated into the database in one step. The right way would be to
     * pass in a new model and write to a remote DB.
     * @param data The data to save.
     */
    public void save(GraphDao data) {
        data.toRdf(model);
    }

    public Optional<UserGraphDao> fetchUserByIdentifier(URI id) {
        var result = describe(id);
        return result.map(resultModel -> UserGraphDao.fromModel(resultModel, id));
    }

    public Optional<InstitutionGraphDao> fetchInstitutionByIdentifier(URI id) {
        var result = describe(id);
        return result.map(resultModel -> InstitutionGraphDao.fromModel(resultModel, id));
    }

    public Optional<PublicationGraphDao> fetchPublicationByIdentifier(URI id) {
        var result = describe(id);
        return result.map(resultModel -> PublicationGraphDao.fromModel(resultModel, id));
    }

    public List<UserGraphDao> listAllUsers() {
        var query = """
            SELECT * WHERE {
                ?userUri a <https://example.org/ontology#User> ;
                     <https://example.org/ontology#identifier> ?userIdentifier ;
                     <https://example.org/ontology#institution> ?userInstitutionUri .
                ?userInstitutionUri a <https://example.org/ontology#Institution> ;
                     <https://example.org/ontology#identifier> ?userInstitutionIdentifier .
            }
            """;
        return execute(query, this::toUser);
    }

    public List<InstitutionGraphDao> listAllInstitutions() {
        var query = """
            SELECT * WHERE {
                ?institution a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?institutionIdentifier .
            }
            """;
        return execute(query, this::toInstitutionList);
    }

    public List<PublicationGraphDao> listAllPublications() {
        var query = """
            SELECT * WHERE {
                ?publicationUri a <https://example.org/ontology#Publication> ;
                    <https://example.org/ontology#identifier> ?publicationIdentifier ;
                    <https://example.org/ontology#user> ?userUri ;
                    <https://example.org/ontology#institution> ?institutionUri .
                ?institutionUri a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?institutionIdentifier .
                ?userUri a <https://example.org/ontology#User> ;
                    <https://example.org/ontology#identifier> ?userIdentifier ;
                    <https://example.org/ontology#institution> ?userInstitutionUri .
                ?userInstitutionUri a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?userInstitutionIdentifier .
            }
            """;
        return listPublications(query);
    }

    public List<PublicationGraphDao> listPublicationsByUser(User user) {
        var query = """
            SELECT * WHERE {
                 BIND ("%s" AS ?userIdentifier)
                 ?publicationUri a <https://example.org/ontology#Publication> ;
                     <https://example.org/ontology#identifier> ?publicationIdentifier ;
                     <https://example.org/ontology#user> ?userUri ;
                     <https://example.org/ontology#institution> ?institutionUri .
                 ?institutionUri a <https://example.org/ontology#Institution> ;
                     <https://example.org/ontology#identifier> ?institutionIdentifier .
                 ?userUri a <https://example.org/ontology#User> ;
                     <https://example.org/ontology#identifier> ?userIdentifier ;
                     <https://example.org/ontology#institution> ?userInstitutionUri .
                 ?userInstitutionUri a <https://example.org/ontology#Institution> ;
                     <https://example.org/ontology#identifier> ?userInstitutionIdentifier .
            }
            """.formatted(user.identifier());
        return listPublications(query);
    }

    public List<PublicationGraphDao> listPublicationsByInstitution(Institution institution) {
        var query = """
            SELECT * WHERE {
                BIND ("%s" AS ?institutionIdentifier)
                ?publicationUri a <https://example.org/ontology#Publication> ;
                    <https://example.org/ontology#identifier> ?publicationIdentifier ;
                    <https://example.org/ontology#user> ?userUri ;
                    <https://example.org/ontology#institution> ?institutionUri .
                ?institutionUri a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?institutionIdentifier .
                ?userUri a <https://example.org/ontology#User> ;
                    <https://example.org/ontology#identifier> ?userIdentifier ;
                    <https://example.org/ontology#institution> ?userInstitutionUri .
                ?userInstitutionUri a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?userInstitutionIdentifier .
            }
            """.formatted(institution.identifier());
        return listPublications(query);
    }

    public List<UserGraphDao> listAllUsersByInstitution(Institution institution) {
        var query = """
            SELECT * WHERE {
                BIND ("%s" AS ?userInstitutionIdentifier)
                ?userUri a <https://example.org/ontology#User> ;
                    <https://example.org/ontology#identifier> ?userIdentifier ;
                    <https://example.org/ontology#institution> ?userInstitutionUri .
                ?userInstitutionUri a <https://example.org/ontology#Institution> ;
                    <https://example.org/ontology#identifier> ?userInstitutionIdentifier .
            }
            """.formatted(institution.identifier());
        return execute(query, this::toUser);
    }

    private Optional<Model> describe(URI id) {
        var query = """
            CONSTRUCT {
                ?s ?p ?o .
                ?o ?p1 ?o1 .
                ?o1 ?p2 ?o2 .
            } WHERE {
                <%s> ?p ?o .
            }
            """.formatted(id);
        return execute(query, this::constructModel);
    }

    private <T> T execute(String query, Function<QueryExecution, T> mapper) {
        try (var queryExecution = QueryExecutionFactory.create(query, model)) {
            return mapper.apply(queryExecution);
        }
    }

    private List<UserGraphDao> toUser(QueryExecution queryExecution) {
        return streamOf(queryExecution.execSelect())
                   .map(this::createUser)
                   .toList();
    }

    private List<InstitutionGraphDao> toInstitutionList(QueryExecution queryExecution) {
        return streamOf(queryExecution.execSelect())
                   .map(row -> toInstitution(row, INSTITUTION_VAR, INSTITUTION_IDENTIFIER_VAR))
                   .toList();
    }


    private static Stream<QuerySolution> streamOf(ResultSet resultSet) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(resultSet, Spliterator.ORDERED), false);
    }

    private UserGraphDao createUser(QuerySolution current) {
        var userInstitution =
            toInstitution(current, USER_INSTITUTION_URI_VAR, USER_INSTITUTION_IDENTIFIER_VAR);
        return toUser(current, userInstitution);
    }

    private Optional<Model> constructModel(QueryExecution queryExecution) {
        var result = queryExecution.execConstruct(model);
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private static String literalAsString(QuerySolution current, String variableName) {
        return current.getLiteral(variableName).getString();
    }

    private static URI resourceAsUri(QuerySolution current, String variableName) {
        return URI.create(current.getResource(variableName).getURI());
    }

    private List<PublicationGraphDao> listPublications(String query) {
        return execute(query, this::getPublicationList);
    }

    private List<PublicationGraphDao> getPublicationList(QueryExecution queryExecution) {
        return streamOf(queryExecution.execSelect())
                   .map(this::createPublication)
                   .toList();
    }

    private PublicationGraphDao createPublication(QuerySolution current) {
        var institution = toInstitution(current, INSTITUTION_URI_VAR, INSTITUTION_IDENTIFIER_VAR);
        return toPublication(current, createUser(current),
                             institution);
    }

    private PublicationGraphDao toPublication(QuerySolution solution,
                                              UserGraphDao user,
                                              InstitutionGraphDao institution) {
        return new PublicationGraphDao(resourceAsUri(solution, PUBLICATION_URI_VAR), UUID.fromString(
            literalAsString(solution, PUBLICATION_IDENTIFIER_VAR)), user, institution);
    }

    private UserGraphDao toUser(QuerySolution solution,
                                InstitutionGraphDao userInstitution) {
        return new UserGraphDao(resourceAsUri(solution, USER_URI_VAR), UUID.fromString(
            literalAsString(solution, USER_IDENTIFIER_VAR)), userInstitution);
    }

    private InstitutionGraphDao toInstitution(QuerySolution solution, String uriVar, String identifierVar) {
        return new InstitutionGraphDao(resourceAsUri(solution, uriVar),
                                       UUID.fromString(literalAsString(solution, identifierVar)));
    }
}
