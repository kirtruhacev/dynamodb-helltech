package helltech.dynamodb.persistence.jpa.dao;

import helltech.dynamodb.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "USERS")
public class UserDao implements Dao<User> {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionDao institutionDao;

    public UserDao() {
    }

    public UserDao(UUID identifier, InstitutionDao institutionDao) {
        this.id = identifier;
        this.institutionDao = institutionDao;
    }

    public static UserDao fromEntity(User user) {
        return new UserDao(user.identifier(), InstitutionDao.fromEntity(user.institution()));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public InstitutionDao getInstitutionDao() {
        return institutionDao;
    }

    public void setInstitutionDao(InstitutionDao institutionDao) {
        this.institutionDao = institutionDao;
    }

    @Override
    public User toEntity() {
        return new User(id, institutionDao.toEntity());
    }
}
