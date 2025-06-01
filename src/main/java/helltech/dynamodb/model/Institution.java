package helltech.dynamodb.model;

import java.util.UUID;

public record Institution(UUID identifier) implements Entity {
}
