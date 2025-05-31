package helltech.dynamodb.model.business;

import java.util.UUID;

public record User(UUID identifier, Institution institution) implements Entity {
}
