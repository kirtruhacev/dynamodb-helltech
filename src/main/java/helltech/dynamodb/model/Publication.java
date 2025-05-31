package helltech.dynamodb.model;

import java.util.UUID;

public record Publication(UUID identifier, User user, Institution institution) implements Entity {
}
