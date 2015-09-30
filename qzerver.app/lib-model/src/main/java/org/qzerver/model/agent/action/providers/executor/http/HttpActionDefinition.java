package org.qzerver.model.agent.action.providers.executor.http;

import com.gainmatrix.lib.serialization.SerialVersionUID;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;

public class HttpActionDefinition implements ActionDefinition, Serializable {

    private static final long serialVersionUID = SerialVersionUID.UNCONTROLLED;

    private static final int HTTP_SUCCESS_CODE = 200;

    @NotNull
    @NotBlank
    private String url;

    private String plainAuthUsername;

    private String plainAuthPassword;

    private Map<String, String> headers;

    private Map<String, String> postParams;

    private boolean skipOutput;

    @NotNull
    private HttpActionMethod method;

    @Min(0)
    private int connectionTimeoutMs;

    @Min(0)
    private int timeoutMs;

    private int expectedStatusCode = HTTP_SUCCESS_CODE;

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPlainAuthPassword() {
        return plainAuthPassword;
    }

    public void setPlainAuthPassword(String plainAuthPassword) {
        this.plainAuthPassword = plainAuthPassword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlainAuthUsername() {
        return plainAuthUsername;
    }

    public void setPlainAuthUsername(String plainAuthUsername) {
        this.plainAuthUsername = plainAuthUsername;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public HttpActionMethod getMethod() {
        return method;
    }

    public void setMethod(HttpActionMethod method) {
        this.method = method;
    }

    public Map<String, String> getPostParams() {
        return postParams;
    }

    public void setPostParams(Map<String, String> postParams) {
        this.postParams = postParams;
    }

    public boolean isSkipOutput() {
        return skipOutput;
    }

    public void setSkipOutput(boolean skipOutput) {
        this.skipOutput = skipOutput;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.HTTP;
    }
}
