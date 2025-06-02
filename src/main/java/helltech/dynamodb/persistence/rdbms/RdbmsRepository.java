package helltech.dynamodb.persistence.rdbms;

import helltech.dynamodb.model.Entity;
import helltech.dynamodb.model.Institution;
import helltech.dynamodb.model.Publication;
import helltech.dynamodb.model.User;
import helltech.dynamodb.persistence.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class RdbmsRepository implements Repository {

    public static final String INSTITUTION_ID_VAR = "institution_id";
    public static final String ID_VAR = "id";
    public static final String USER_ID_VAR = "user_id";
    public static final String USER_INSTITUTION_ID_VAR = "user_institution_id";
    private final RdbmsDataSource dataSource;

    public RdbmsRepository(RdbmsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Entity entity) {
        switch (entity) {
            case Institution institution -> saveInstitution(institution);
            case Publication publication -> savePublication(publication);
            case User user -> saveUser(user);
            default -> throw new IllegalStateException("Unexpected value: " + entity);
        }
    }

    public void flush() {
        dataSource.flush();
    }

    public void close() {
        dataSource.closeDataSource();
    }

    private void saveUser(User user) {
        var sql = "INSERT INTO \"USER\" (id, institution_id) VALUES (?, ?)";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.identifier().toString());
            preparedStatement.setString(2, user.institution().identifier().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void savePublication(Publication publication) {
        var sql = "INSERT INTO \"PUBLICATION\" (id, user_id, institution_id) VALUES (?, ?, ?)";

        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, publication.identifier().toString());
            preparedStatement.setString(2, publication.user().identifier().toString());
            preparedStatement.setString(3, publication.institution().identifier().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void saveInstitution(Institution institution) {
        var sql = "INSERT INTO \"INSTITUTION\" (id) VALUES (?)";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, institution.identifier().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<User> fetchUserByIdentifier(UUID identifier) {
        return queryUser(identifier);
    }

    @Override
    public Optional<Institution> fetchInstitutionByIdentifier(UUID identifier) {
        return queryInstitution(identifier);
    }

    @Override
    public Optional<Publication> fetchPublicationByIdentifier(UUID identifier) {
        return queryPublication(identifier);
    }

    @Override
    public List<User> listAllUsers() {
        var sql = """
            SELECT id, institution_id FROM "USER";
            """;
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<User>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                var institutionId = UUID.fromString(resultSet.getString(INSTITUTION_ID_VAR));
                results.add(new User(id, new Institution(institutionId)));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Institution> listAllInstitutions() {
        var sql = "SELECT id FROM \"INSTITUTION\"";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<Institution>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                results.add(new Institution(id));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Publication> listAllPublications() {
        var sql = """
            SELECT p.id, \
                   u.id AS user_id, \
                   u.institution_id AS user_institution_id,\
                   p.institution_id FROM "PUBLICATION" p \
                   INNER JOIN "USER" u ON p.user_id = u.id""";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<Publication>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                var userId = UUID.fromString(resultSet.getString(USER_ID_VAR));
                var userInstitutionId = UUID.fromString(resultSet.getString(USER_INSTITUTION_ID_VAR));
                var institutionId = UUID.fromString(resultSet.getString(INSTITUTION_ID_VAR));
                results.add(new Publication(id, new User(userId, new Institution(userInstitutionId)),
                                            new Institution(institutionId)));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Publication> listPublicationsByUser(User user) {

        var sql = """
            SELECT p.id, \
                   u.id AS user_id, \
                   u.institution_id AS user_institution_id,\
                   p.institution_id FROM "PUBLICATION" p \
                   INNER JOIN "USER" u ON p.user_id = u.id WHERE user_id = ?""";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.identifier().toString());
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<Publication>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                var userId = UUID.fromString(resultSet.getString(USER_ID_VAR));
                var userInstitutionId = UUID.fromString(resultSet.getString(USER_INSTITUTION_ID_VAR));
                var institutionId = UUID.fromString(resultSet.getString(INSTITUTION_ID_VAR));
                results.add(new Publication(id, new User(userId, new Institution(userInstitutionId)),
                                            new Institution(institutionId)));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Publication> listPublicationsByInstitution(Institution institution) {

        var sql = """
            SELECT p.id, \
                   u.id AS user_id, \
                   u.institution_id AS user_institution_id,\
                   p.institution_id FROM "PUBLICATION" p \
                   INNER JOIN "USER" u ON p.user_id = u.id WHERE p.institution_id = ?""";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, institution.identifier().toString());
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<Publication>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                var userId = UUID.fromString(resultSet.getString(USER_ID_VAR));
                var userInstitutionId = UUID.fromString(resultSet.getString(USER_INSTITUTION_ID_VAR));
                var institutionId = UUID.fromString(resultSet.getString(INSTITUTION_ID_VAR));
                results.add(new Publication(id, new User(userId, new Institution(userInstitutionId)),
                                            new Institution(institutionId)));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<User> listAllUsersByInstitution(Institution institution) {

        var sql = """
            SELECT id, \
                   institution_id AS institution_id \
                   FROM "USER" \
                   WHERE institution_id = ?""";
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, institution.identifier().toString());
            var resultSet = preparedStatement.executeQuery();
            var results = new ArrayList<User>();
            while (resultSet.next()) {
                var id = UUID.fromString(resultSet.getString(ID_VAR));
                var institutionId = UUID.fromString(resultSet.getString(INSTITUTION_ID_VAR));
                results.add(new User(id, new Institution(institutionId)));
            }
            return results.isEmpty() ? Collections.emptyList() : results;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<User> queryUser(UUID identifier) {
        var sql = """
            SELECT u.id, i.id AS institution_id\
                FROM "USER" u\
                INNER JOIN "INSTITUTION" i ON u.institution_id = i.id\
                WHERE u.id = ?
            """;
        return queryByIdentifier(identifier, sql, extractUserByIdentifierResult());
    }

    private static Function<ResultSet, List<User>> extractUserByIdentifierResult() {
        return (ResultSet resultSet) -> {
            var results = new ArrayList<User>();
            try {
                while (resultSet.next()) {
                    var id = resultSet.getString(ID_VAR);
                    var institution = resultSet.getString(INSTITUTION_ID_VAR);
                    results.add(new User(UUID.fromString(id), new Institution(UUID.fromString(institution))));
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            return results;
        };
    }

    private Optional<Institution> queryInstitution(UUID identifier) {
        var sql = "SELECT * FROM \"INSTITUTION\" WHERE id = ?";
        return queryByIdentifier(identifier, sql, extractInstitutionByIdentifierResult());
    }

    private static Function<ResultSet, List<Institution>> extractInstitutionByIdentifierResult() {
        return (ResultSet resultSet) -> {
            var results = new ArrayList<Institution>();
            try {
                while (resultSet.next()) {
                    var id = resultSet.getString(ID_VAR);
                    results.add(new Institution(UUID.fromString(id)));
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            return results;
        };
    }

    private Optional<Publication> queryPublication(UUID identifier) {
        var sql = """
            SELECT p.id, \
                   u.id AS user_id, \
                   u.id AS user_institution_id,\
                   p.institution_id FROM "PUBLICATION" p \
                   INNER JOIN "USER" u ON p.user_id = u.id WHERE p.id = ?""";
        return queryByIdentifier(identifier, sql, extractPublicationByIdentifierResult());
    }

    private Function<ResultSet, List<Publication>> extractPublicationByIdentifierResult() {
        return (ResultSet resultSet) -> {
            var results = new ArrayList<Publication>();
            try {
                while (resultSet.next()) {
                    var id = resultSet.getString(ID_VAR);
                    var institutionId = resultSet.getString(INSTITUTION_ID_VAR);
                    var userId = resultSet.getString(USER_ID_VAR);
                    var institution = new Institution(UUID.fromString(institutionId));
                    var user = new User(UUID.fromString(userId), institution);
                    results.add(new Publication(UUID.fromString(id), user, institution));
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            return results;
        };
    }

    private <T> Optional<T> queryByIdentifier(UUID identifier, String sql, Function<ResultSet, List<T>> function) {
        try (var connection = dataSource.getDataSource().getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, identifier.toString());
            var resultSet = preparedStatement.executeQuery();
            var results = function.apply(resultSet);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
