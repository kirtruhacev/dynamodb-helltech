package helltech.dynamodb.persistence.dynamodb.dao;

import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.GSI1;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.PK1;
import static helltech.dynamodb.persistence.dynamodb.DynamoDbConstants.SK0;
import com.fasterxml.jackson.annotation.JsonProperty;
import helltech.dynamodb.annotations.Generated;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Dao implements Serializable {

    protected static final String KEY_PATTERN = "%s#%s";

    private String type;
    private UUID identifier;

    public Dao() {
    }

    public Dao(UUID identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    public static String partitionKey(String type, UUID identifier) {
        return KEY_PATTERN.formatted(type, identifier);
    }

    public static String sortKey(String type, UUID identifier) {
        return KEY_PATTERN.formatted(type, identifier);
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    @DynamoDbAttribute("PK0")
    @DynamoDbPartitionKey
    @JsonProperty("PK0")
    public String getPartitionKey() {
        return KEY_PATTERN.formatted(type, this.identifier);
    }

    public void setPartitionKey(String partitionKey) {

    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI1})
    @DynamoDbAttribute(PK1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDbAttribute(SK0)
    @DynamoDbSortKey
    @JsonProperty(SK0)
    public String getSortKey() {
        return KEY_PATTERN.formatted(type, this.identifier);
    }

    public void setSortKey(String sortKey) {

    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(getType(), getIdentifier());
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Dao dao)) {
            return false;
        }
        return Objects.equals(getType(), dao.getType()) && Objects.equals(getIdentifier(), dao.getIdentifier());
    }
}
