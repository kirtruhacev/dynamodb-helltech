package helltech.dynamodb.persistence;

/**
 * Constants used in the creation of the database and its keys. Note that SK1 is not used.
 */
public final class DynamoDbConstants {

    private DynamoDbConstants() {
        // NO-OP. Should not be initialized.
    }

    public static final String TABLE_NAME = "table";
    public static final String GSI1 = "GSI1";
    public static final String GSI2 = "GSI2";
    public static final String GSI3 = "GSI3";
    public static final String PK0 = "PK0";
    public static final String PK1 = "PK1";
    public static final String PK2 = "PK2";
    public static final String PK3 = "PK3";
    public static final String SK0 = "SK0";
    public static final String SK2 = "SK2";
    public static final String SK3 = "SK3";
}
