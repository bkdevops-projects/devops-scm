package com.tencent.devops.scm.sdk.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 客户端解析服务端返回
 * @param <T>
 */
public class ScmResponse<T> {

    private static final Logger logger = LoggerFactory.getLogger(ScmResponse.class);

    private final int statusCode;

    private final Map<String, List<String>> headers;

    private final T body;

    public ScmResponse(ScmResponse<T> response, T body) {
        this.statusCode = response.statusCode;
        this.headers = response.headers;
        this.body = body;
    }

    public ScmResponse(ScmConnectorResponse connectorResponse, T body) {
        this.statusCode = connectorResponse.statusCode();
        this.headers = connectorResponse.allHeaders();
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }

    public List<String> headers(String field) {
        return headers.get(field);
    }

    public String header(String name) {
        String result = null;
        List<String> rawResult = headers.get(name);
        if (rawResult != null) {
            result = rawResult.get(0);
        }
        return result;
    }

    public T body() {
        return body;
    }

    public static <T> T parseBody(ScmConnectorResponse connectorResponse, Class<T> clazz, ScmSdkJsonFactory jsonFactory)
            throws IOException {
        String data = getBodyAsString(connectorResponse);
        try {
            return jsonFactory.fromJson(data, clazz);
        } catch (JsonMappingException | JsonParseException e) {
            logger.error("Failed to deserialize: {}", data);
            throw e;
        }
    }

    public static <T> T parseBody(String data, Class<T> clazz, ScmSdkJsonFactory jsonFactory)
            throws IOException {
        try {
            return jsonFactory.fromJson(data, clazz);
        } catch (JsonMappingException | JsonParseException e) {
            logger.error("Failed to deserialize: {}", data);
            throw e;
        }
    }

    public static <T> T parseBody(
            ScmConnectorResponse connectorResponse,
            TypeReference<T> typeReference,
            ScmSdkJsonFactory jsonFactory
    ) throws IOException {
        String data = getBodyAsString(connectorResponse);
        try {
            return jsonFactory.fromJson(data, typeReference);
        } catch (JsonMappingException | JsonParseException e) {
            logger.error("Failed to deserialize: {}", data);
            throw e;
        }
    }

    public static <T> T parseBody(
            String data,
            TypeReference<T> typeReference,
            ScmSdkJsonFactory jsonFactory
    ) throws IOException {
        try {
            return jsonFactory.fromJson(data, typeReference);
        } catch (JsonMappingException | JsonParseException e) {
            logger.error("Failed to deserialize: {}", data);
            throw e;
        }
    }

    public static String getBodyAsString(ScmConnectorResponse connectorResponse) throws IOException {
        InputStream inputStream = connectorResponse.bodyStream();
        try (InputStreamReader r = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            String body = IOUtils.toString(r);
            if (logger.isDebugEnabled()) {
                logger.debug("Scm API response body {}", body);
            }
            return body;
        }
    }
}
