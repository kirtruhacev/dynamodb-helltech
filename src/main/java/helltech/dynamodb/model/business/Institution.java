package helltech.dynamodb.model.business;

import java.util.UUID;

public record Institution(UUID identifier) implements Entity {
}
