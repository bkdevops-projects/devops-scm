package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class TestConnector implements ScmConnector {
    private final Deque<Reply> replies = new ArrayDeque<>();
    private final List<ScmConnectorRequest> requests = new ArrayList<>();

    TestConnector reply(int status, String body) {
        return reply(status, body, Collections.emptyMap());
    }

    TestConnector reply(int status, String body, Map<String, List<String>> headers) {
        replies.add(new Reply(status, body, headers));
        return this;
    }

    ScmConnectorRequest lastRequest() {
        return requests.get(requests.size() - 1);
    }

    List<ScmConnectorRequest> requests() {
        return requests;
    }

    @Override
    public ScmConnectorResponse send(ScmConnectorRequest request) {
        requests.add(request);
        Reply reply = replies.removeFirst();
        return new ScmConnectorResponse.ByteArrayResponse(request, reply.status, reply.headers) {
            @Override
            protected InputStream rawBodyStream() {
                return new ByteArrayInputStream(reply.body.getBytes(StandardCharsets.UTF_8));
            }
        };
    }

    static Map<String, List<String>> headers(String... values) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            headers.put(values[i], Collections.singletonList(values[i + 1]));
        }
        return headers;
    }

    private static final class Reply {
        private final int status;
        private final String body;
        private final Map<String, List<String>> headers;

        private Reply(int status, String body, Map<String, List<String>> headers) {
            this.status = status;
            this.body = body;
            this.headers = headers;
        }
    }
}
