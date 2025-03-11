package com.tencent.devops.scm.sdk.common.connector.okhttp3;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *  {@link ScmConnector} for {@link OkHttpClient}.
 */
public class OkHttpScmConnector implements ScmConnector {

    private final OkHttpClient client;

    public OkHttpScmConnector(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public ScmConnectorResponse send(ScmConnectorRequest request) throws IOException {
        Request.Builder builder = new Request.Builder().url(request.url());
        for (Map.Entry<String, List<String>> e : request.allHeaders().entrySet()) {
            List<String> v = e.getValue();
            if (v != null) {
                builder.addHeader(e.getKey(), String.join(", ", v));
            }
        }
        RequestBody body = null;
        if (request.hasBody()) {
            body = RequestBody.create(IOUtils.toByteArray(request.body()));
        }
        builder.method(request.method(), body);
        Request okhttpRequest = builder.build();
        Response okhttpResponse = client.newCall(okhttpRequest).execute();

        return new OkHttpScmConnectorResponse(request, okhttpResponse);
    }

    private static class OkHttpScmConnectorResponse extends ScmConnectorResponse.ByteArrayResponse {

        private final Response response;

        OkHttpScmConnectorResponse(ScmConnectorRequest request, Response response) {
            super(request, response.code(), response.headers().toMultimap());
            this.response = response;
        }

        @Override
        protected InputStream rawBodyStream() throws IOException {
            ResponseBody body = response.body();
            if (body != null) {
                return body.byteStream();
            } else {
                return null;
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            response.close();
        }
    }
}
