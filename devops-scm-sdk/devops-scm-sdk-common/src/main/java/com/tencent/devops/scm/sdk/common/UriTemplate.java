package com.tencent.devops.scm.sdk.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI模板工具
 */
public class UriTemplate {
    private static final Pattern PARAM_PATTERN = Pattern.compile(":(\\w+)");
    private final String pattern;
    private final Map<String, String> variables = new LinkedHashMap<>();

    private UriTemplate(String pattern) {
        this.pattern = pattern;
    }

    public static UriTemplate from(String pattern) {
        return new UriTemplate(pattern);
    }

    public UriTemplate with(String name, Object value) {
        variables.put(name, value.toString());
        return this;
    }

    public UriTemplate with(String name, String value, boolean encodeValue) {
        variables.put(name, encodeValue ? encodeValue(value) : value);
        return this;
    }

    public UriTemplate with(Map<String, String> params) {
        variables.putAll(params);
        return this;
    }

    public String expand() {
        Matcher matcher = PARAM_PATTERN.matcher(pattern);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            String replacement = variables.get(paramName);
            if (StringUtils.isBlank(replacement)) {
                throwMissingParamException(paramName);
            }
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private String encodeValue(String value) {
        try {
            return value != null ? URLEncoder.encode(value, "UTF-8") : "";
        } catch (UnsupportedEncodingException e) {
            logger.warn("encode failed", e);
            return value;
        }
    }

    private String throwMissingParamException(String paramName) {
        throw new IllegalArgumentException(
            "Missing parameter '" + paramName + "' for URI pattern: " + pattern);
    }

    private static final Logger logger = LoggerFactory.getLogger(UriTemplate.class);
}

