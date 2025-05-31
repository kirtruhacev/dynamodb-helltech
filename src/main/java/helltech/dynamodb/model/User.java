package helltech.dynamodb.model;

import java.util.UUID;

public record User(UUID identifier, Institution institution) implements Entity {
}
