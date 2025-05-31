package helltech.dynamodb.model;

import java.util.UUID;

public sealed interface Entity permits Institution, Publication, User {

    UUID identifier();
}
