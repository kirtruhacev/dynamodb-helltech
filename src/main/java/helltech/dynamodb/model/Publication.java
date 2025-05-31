package helltech.dynamodb.model.business;

import java.util.UUID;

public record Publication(UUID identifier, User user, Institution institution) implements Entity {
}
