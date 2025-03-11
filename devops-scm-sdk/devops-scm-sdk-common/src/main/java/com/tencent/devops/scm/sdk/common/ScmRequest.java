package com.tencent.devops.scm.sdk.common;

import static com.tencent.devops.scm.sdk.common.constants.ScmSdkConstants.SCM_REPO_ID_HEADER;
import static com.tencent.devops.scm.sdk.common.constants.ScmSdkConstants.URI_PATTERN_HEADER;

import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.exception.ScmSdkException;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 客户端构造http请求实体
 */
@SuppressWarnings("unchecked")
public class ScmRequest implements ScmConnectorRequest {

    private static final Comparator<String> nullableCaseInsensitiveComparator = Comparator
            .nullsFirst(String.CASE_INSENSITIVE_ORDER);

    private static final List<String> METHODS_WITHOUT_BODY = Collections.singletonList("GET");
    private final List<Entry> args;
    private final Map<String, List<String>> headers;
    private final String apiUrl;
    private final String urlPath;
    private final String method;
    private final byte[] body;
    private final boolean forceBody;

    private final URL url;

    private ScmRequest(List<Entry> args,
            Map<String, List<String>> headers,
            String apiUrl,
            String urlPath,
            String method,
            byte[] body,
            boolean forceBody
    ) {
        this.args = Collections.unmodifiableList(new ArrayList<>(args));
        TreeMap<String, List<String>> caseInsensitiveMap = new TreeMap<>(nullableCaseInsensitiveComparator);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            caseInsensitiveMap.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        this.headers = Collections.unmodifiableMap(caseInsensitiveMap);
        this.apiUrl = apiUrl;
        this.urlPath = urlPath;
        this.method = method;
        this.body = body;
        this.forceBody = forceBody;
        String tailApiUrl = buildTailApiUrl();
        url = getApiURL(apiUrl, tailApiUrl);
    }

    public static Builder<?> newBuilder() {
        return new Builder<>();
    }

    static URL getApiURL(String apiUrl, String tailApiUrl) {
        try {
            return new URL(apiUrl + tailApiUrl);
        } catch (Exception e) {
            throw new ScmSdkException("Unable to build Scm API URL", e);
        }
    }

    @Override
    public String method() {
        return method;
    }

    public List<Entry> args() {
        return args;
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        return headers;
    }

    @Override
    public String header(String name) {
        List<String> values = headers.get(name);
        if (values != null) {
            return values.get(0);
        }
        return null;
    }


    public String apiUrl() {
        return apiUrl;
    }

    public String urlPath() {
        return urlPath;
    }

    @Override
    public String contentType() {
        return header("Content-type");
    }

    @Override
    public InputStream body() {
        return body != null ? new ByteArrayInputStream(body) : null;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public boolean hasBody() {
        return forceBody || !METHODS_WITHOUT_BODY.contains(method);
    }

    public Builder<?> toBuilder() {
        return new Builder<>(args,
                headers,
                apiUrl,
                urlPath,
                method,
                body,
                forceBody);
    }

    private String buildTailApiUrl() {
        String tailApiUrl = urlPath;
        if (!hasBody() && !args.isEmpty() && tailApiUrl.startsWith("/")) {
            try {
                StringBuilder argString = new StringBuilder();
                boolean questionMarkFound = tailApiUrl.indexOf('?') != -1;
                argString.append(questionMarkFound ? '&' : '?');

                for (Iterator<Entry> it = args.listIterator(); it.hasNext();) {
                    Entry arg = it.next();
                    argString.append(URLEncoder.encode(arg.key, StandardCharsets.UTF_8.name()));
                    argString.append('=');
                    argString.append(URLEncoder.encode(arg.value.toString(), StandardCharsets.UTF_8.name()));
                    if (it.hasNext()) {
                        argString.append('&');
                    }
                }
                tailApiUrl += argString;
            } catch (UnsupportedEncodingException e) {
                throw new ScmSdkException("UTF-8 encoding required", e);
            }
        }
        return tailApiUrl;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<B>> {

        private final List<Entry> args;

        private final Map<String, List<String>> headers;

        private String apiUrl;

        private String urlPath;

        private String method;

        private byte[] body;
        private boolean forceBody;

        protected Builder() {
            this(new ArrayList<>(),
                    new TreeMap<>(nullableCaseInsensitiveComparator),
                    "",
                    "",
                    "GET",
                    null,
                    false);
        }

        private Builder(List<Entry> args,
                        Map<String, List<String>> headers,
                        String apiUrl,
                        String urlPath,
                        String method,
                        byte[] body,
                        boolean forceBody) {
            this.args = new ArrayList<>(args);
            TreeMap<String, List<String>> caseInsensitiveMap = new TreeMap<>(nullableCaseInsensitiveComparator);
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                caseInsensitiveMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            this.headers = caseInsensitiveMap;
            this.apiUrl = apiUrl;
            this.urlPath = urlPath;
            this.method = method;
            this.body = body;
            this.forceBody = forceBody;
        }


        public ScmRequest build() {
            return new ScmRequest(args,
                    headers,
                    apiUrl,
                    urlPath,
                    method,
                    body,
                    forceBody);
        }
        
        public B withApiUrl(String url) {
            this.apiUrl = url;
            return (B) this;
        }
        
        public B removeHeader(String name) {
            headers.remove(name);
            return (B) this;
        }
        
        public B setHeader(String name, String value) {
            List<String> field = new ArrayList<>();
            field.add(value);
            headers.put(name, field);
            return (B) this;
        }
        
        public B withHeader(String name, String value) {
            List<String> field = headers.get(name);
            if (field == null) {
                setHeader(name, value);
            } else {
                field.add(value);
            }
            return (B) this;
        }
        
        public B with(Map<String, Object> map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                with(entry.getKey(), entry.getValue());
            }

            return (B) this;
        }
        
        public B with(String key, int value) {
            return with(key, (Object) value);
        }
        
        public B with(String key, long value) {
            return with(key, (Object) value);
        }
        
        public B with(String key, boolean value) {
            return with(key, (Object) value);
        }
        
        public B with(String key, String value) {
            return with(key, (Object) value);
        }
        
        public B with(String key, Collection<?> value) {
            return with(key, (Object) value);
        }
        
        public B with(String key, Map<?, ?> value) {
            return with(key, (Object) value);
        }

       
        public B with(InputStream body) throws IOException {
            this.body = IOUtils.toByteArray(body);
            IOUtils.closeQuietly(body);
            return (B) this;
        }

        public B with(String body) {
            this.body = body.getBytes(StandardCharsets.UTF_8);
            return (B) this;
        }

        public B with(String key, Object value) {
            if (value != null) {
                args.add(new Entry(key, value));
            }
            return (B) this;
        }
        
        public B withNullable(String key, Object value) {
            args.add(new Entry(key, value));
            return (B) this;
        }
        
        public B set(String key, Object value) {
            remove(key);
            return with(key, value);

        }
        
        public B remove(String key) {
            for (int index = 0; index < args.size();) {
                if (args.get(index).key.equals(key)) {
                    args.remove(index);
                } else {
                    index++;
                }
            }
            return (B) this;
        }


        public B method(String method) {
            this.method = method;
            return (B) this;
        }
        
        public B contentType(String contentType) {
            this.setHeader("Content-type", contentType);
            return (B) this;
        }

        public B withUrlPath(String urlPath, String... urlPathItems) {
            String tailUrlPath = urlPath;
            if (urlPathItems.length != 0) {
                tailUrlPath += "/" + String.join("/", urlPathItems);
            }

            if (!tailUrlPath.startsWith("/")) {
                tailUrlPath = "/" + tailUrlPath;
            }

            this.urlPath = tailUrlPath;
            return (B) this;
        }

        public B withUrlPath(String uriPattern) {
            this.urlPath = "/" + uriPattern;
            withUriPattern(uriPattern);
            return (B) this;
        }

        public B withUrlPath(String uriPattern, Map<String, String> params) {
            this.urlPath = "/" + UriTemplate.from(uriPattern).with(params).expand();
            withUriPattern(uriPattern);
            return (B) this;
        }

        public B withUriPattern(String uriPattern) {
            this.setHeader(URI_PATTERN_HEADER, uriPattern);
            return (B) this;
        }

        public B withRepoId(String repoId) {
            this.setHeader(SCM_REPO_ID_HEADER, repoId);
            return (B) this;
        }

        public B inBody() {
            forceBody = true;
            return (B) this;
        }
    }

    /**
     * The Class Entry.
     */
    @Getter
    public static class Entry {

        /** The key. */
        private final String key;

        /** The value. */
        private final Object value;

        /**
         * Instantiates a new entry.
         *
         * @param key
         *            the key
         * @param value
         *            the value
         */
        protected Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private static String urlPathEncode(String value) {
        try {
            return new URI(null, null, value, null, null).toASCIIString();
        } catch (URISyntaxException ex) {
            throw new AssertionError(ex);
        }
    }
}
