package helltech.dynamodb.persistence.jpa.dao;

import helltech.dynamodb.model.Institution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "INSTITUTIONS")
public class InstitutionDao implements Dao<Institution> {

    @Id
    @Column(name = "id")
    private UUID id;

    public InstitutionDao() {

    }

    public InstitutionDao(UUID identifier) {
        this.id = identifier;
    }

    public static InstitutionDao fromEntity(Institution institution) {
        return new InstitutionDao(institution.identifier());
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Institution toEntity() {
        return new Institution(id);
    }
}
