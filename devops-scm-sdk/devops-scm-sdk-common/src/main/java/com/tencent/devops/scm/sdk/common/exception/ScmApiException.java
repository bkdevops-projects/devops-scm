package com.tencent.devops.scm.sdk.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.tencent.devops.scm.sdk.common.ScmResponse;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScmApiException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(ScmApiException.class);
    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_JSON = "application/json";
    @Getter
    private int statusCode;
    private String message;

    public ScmApiException(String message) {
        super(message);
        this.message = message;
    }

    public ScmApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public ScmApiException(Exception e) {
        super(e);
        this.message = e.getMessage();
    }

    public ScmApiException(String message, Exception e) {
        super(e);
        this.message = message;
    }

    public ScmApiException(Exception e, ScmConnectorResponse connectorResponse) {
        this.message = e.getMessage();
        if (connectorResponse != null) {
            readResponseMessage(connectorResponse);
        }
    }

    public ScmApiException(ScmConnectorResponse connectorResponse) {
        readResponseMessage(connectorResponse);
    }

    private void readResponseMessage(ScmConnectorResponse connectorResponse) {
        this.statusCode = connectorResponse.statusCode();
        try {
            String message = ScmResponse.getBodyAsString(connectorResponse);
            String contentTypeHeader = connectorResponse.header(CONTENT_TYPE);
            if (contentTypeHeader != null && contentTypeHeader.contains(APPLICATION_JSON)) {
                JsonNode json = ScmJsonUtil.getJsonFactory().toJsonNode(message);
                JsonNode jsonMessage = json.get("message");
                if (jsonMessage != null) {
                    if (jsonMessage.isTextual()) {
                        this.message = jsonMessage.asText();
                    } else {
                        this.message = jsonMessage.toString();
                    }
                }
            }
        } catch (Exception exception) {
            logger.warn("Failed to build tgit api exception", exception);
        }
    }

    @Override
    public String getMessage() {
        return message;
    }
}
