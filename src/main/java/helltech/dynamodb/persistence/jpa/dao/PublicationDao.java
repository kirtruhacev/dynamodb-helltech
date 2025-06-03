package helltech.dynamodb.persistence.jpa.dao;

import helltech.dynamodb.model.Publication;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "PUBLICATIONS")
public class PublicationDao implements Dao<Publication> {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDao user;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionDao institution;

    public PublicationDao() {
    }

    public PublicationDao(UUID identifier, UserDao userDao, InstitutionDao institutionDao) {
        this.id = identifier;
        this.user = userDao;
        this.institution = institutionDao;
    }

    public static PublicationDao fromEntity(Publication publication) {
        return new PublicationDao(publication.identifier(), UserDao.fromEntity(publication.user()),
                                  InstitutionDao.fromEntity(publication.institution()));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public UserDao getUser() {
        return user;
    }

    public void setUser(UserDao user) {
        this.user = user;
    }

    public InstitutionDao getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDao institution) {
        this.institution = institution;
    }

    @Override
    public Publication toEntity() {
        return new Publication(id, user.toEntity(), institution.toEntity());
    }
}