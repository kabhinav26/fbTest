package facebookTest;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "storeId",
        "createdDate",
        "createdBy",
        "updatedDate",
        "updatedBy",
        "version",
        "key",
        "value",
        "feedType",
        "positiveFilter"
})
/**
 * Created by kumar on 26/07/17.
 */
public class Content {

    @JsonProperty("id")
    private String id;
    @JsonProperty("storeId")
    private String storeId;
    @JsonProperty("createdDate")
    private Long createdDate;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("updatedDate")
    private Long updatedDate;
    @JsonProperty("updatedBy")
    private String updatedBy;
    @JsonProperty("version")
    private Object version;
    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private String value;
    @JsonProperty("feedType")
    private String feedType;
    @JsonProperty("positiveFilter")
    private Boolean positiveFilter;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("storeId")
    public String getStoreId() {
        return storeId;
    }

    @JsonProperty("storeId")
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @JsonProperty("createdDate")
    public Long getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("createdDate")
    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("updatedDate")
    public Long getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty("updatedDate")
    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

    @JsonProperty("updatedBy")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updatedBy")
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty("version")
    public Object getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Object version) {
        this.version = version;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("feedType")
    public String getFeedType() {
        return feedType;
    }

    @JsonProperty("feedType")
    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @JsonProperty("positiveFilter")
    public Boolean getPositiveFilter() {
        return positiveFilter;
    }

    @JsonProperty("positiveFilter")
    public void setPositiveFilter(Boolean positiveFilter) {
        this.positiveFilter = positiveFilter;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
