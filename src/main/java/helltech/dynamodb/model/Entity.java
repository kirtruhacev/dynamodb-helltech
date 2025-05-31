package helltech.dynamodb.model.business;

import java.util.UUID;

public sealed interface Entity permits Institution, Publication, User {

    UUID identifier();
}
