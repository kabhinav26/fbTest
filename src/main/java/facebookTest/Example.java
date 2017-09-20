package facebookTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by kumar on 26/07/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "requestId",
        "headers",
        "errorMessage",
        "errorCode",
        "success",
        "content",
        "pageMetaData"
})
public class Example {
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("headers")
    private Object headers;
    @JsonProperty("errorMessage")
    private Object errorMessage;
    @JsonProperty("errorCode")
    private Object errorCode;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("content")
    private List<Content> content = null;
    @JsonProperty("pageMetaData")
    private PageMetaData pageMetaData;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestId")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("requestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("headers")
    public Object getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    @JsonProperty("errorMessage")
    public Object getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty("errorMessage")
    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonProperty("errorCode")
    public Object getErrorCode() {
        return errorCode;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(Object errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("content")
    public List<Content> getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(List<Content> content) {
        this.content = content;
    }

    @JsonProperty("pageMetaData")
    public PageMetaData getPageMetaData() {
        return pageMetaData;
    }

    @JsonProperty("pageMetaData")
    public void setPageMetaData(PageMetaData pageMetaData) {
        this.pageMetaData = pageMetaData;
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
