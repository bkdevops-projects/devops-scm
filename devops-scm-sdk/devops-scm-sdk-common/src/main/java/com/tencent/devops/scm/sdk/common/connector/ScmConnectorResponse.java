package com.tencent.devops.scm.sdk.common.connector;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public abstract class ScmConnectorResponse implements Closeable {
    private static final Comparator<String> nullableCaseInsensitiveComparator = Comparator
            .nullsFirst(String.CASE_INSENSITIVE_ORDER);

    private final int statusCode;
    private final ScmConnectorRequest request;
    private final Map<String, List<String>> headers;

    protected ScmConnectorResponse(ScmConnectorRequest request, int statusCode, Map<String, List<String>> headers) {
        this.request = request;
        this.statusCode = statusCode;

        // Response header field names must be case-insensitive.
        TreeMap<String, List<String>> caseInsensitiveMap = new TreeMap<>(nullableCaseInsensitiveComparator);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            caseInsensitiveMap.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        this.headers = Collections.unmodifiableMap(caseInsensitiveMap);
    }

    public String header(String name) {
        String result = null;
        if (headers.containsKey(name)) {
            result = headers.get(name).get(0);
        }
        return result;
    }

    public abstract InputStream bodyStream() throws IOException;

    public ScmConnectorRequest request() {
        return request;
    }

    public int statusCode() {
        return statusCode;
    }

    public Map<String, List<String>> allHeaders() {
        return headers;
    }

    protected InputStream wrapStream(InputStream stream) throws IOException {
        String encoding = header("Content-Encoding");
        if (encoding == null || stream == null) {
            return stream;
        }
        if (encoding.equals("gzip")) {
            return new GZIPInputStream(stream);
        }

        throw new UnsupportedOperationException("Unexpected Content-Encoding: " + encoding);
    }

    public final int parseInt(String name) throws NumberFormatException {
        try {
            String headerValue = header(name);
            return Integer.parseInt(headerValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(name + ": " + e.getMessage());
        }
    }

    /**
     * A ByteArrayResponse class
     */
    public abstract static class ByteArrayResponse extends ScmConnectorResponse {

        private boolean inputStreamRead = false;
        private byte[] inputBytes = null;
        private boolean isClosed = false;

        protected ByteArrayResponse(ScmConnectorRequest request, int statusCode, Map<String, List<String>> headers) {
            super(request, statusCode, headers);
        }

        public InputStream bodyStream() throws IOException {
            if (isClosed) {
                throw new IOException("Response is closed");
            }
            synchronized (this) {
                if (!inputStreamRead) {
                    InputStream rawStream = rawBodyStream();
                    try (InputStream stream = wrapStream(rawStream)) {
                        if (stream != null) {
                            inputBytes = IOUtils.toByteArray(stream);
                        }
                    }
                    inputStreamRead = true;
                }
            }

            if (inputBytes == null) {
                throw new IOException("Response body missing, stream null");
            }

            return new ByteArrayInputStream(inputBytes);
        }

        protected abstract InputStream rawBodyStream() throws IOException;

        @Override
        public void close() throws IOException {
            isClosed = true;
            this.inputBytes = null;
        }
    }
}
