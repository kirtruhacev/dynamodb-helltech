package helltech.dynamodb.persistence.jpa.dao;

import helltech.dynamodb.model.Entity;
import java.util.UUID;

public interface Dao<E extends Entity> {

    E toEntity();

    UUID getId();

    void setId(UUID id);
}
