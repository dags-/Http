package me.dags.http.server;

import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class Server extends NanoHTTPD {

    private static final Response BAD = newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, null);
    private final Map<Route, Handler> handlers;

    private Server(Builder builder) {
        super(builder.address, builder.port);
        handlers = Collections.unmodifiableMap(new HashMap<>(builder.handlers));
    }

    @Override
    public Response serve(IHTTPSession session) {
        for (Map.Entry<Route, Handler> entry : handlers.entrySet()) {
            Route route = entry.getKey();
            Map<String, String> routeParams = route.parse(session.getUri());
            if (routeParams != null) {
                try {
                    ResponseWriter writer = new ResponseWriter(this, newFixedLengthResponse(null));
                    Context request = new Context(session, routeParams);
                    entry.getValue().handle(writer, request);
                    return writer.getResponse();
                } catch (Throwable t) {
                    t.printStackTrace();
                    return newFixedLengthResponse(t.getMessage());
                }
            }
        }
        return BAD;
    }

    void setBody(Response response, String content) {
        byte[] data;
        if (content == null) {
            data = new byte[0];
        } else {
            try {
                ContentType contentType = new ContentType(response.getMimeType());
                CharsetEncoder newEncoder = Charset.forName(contentType.getEncoding()).newEncoder();
                if (!newEncoder.canEncode(content)) {
                    contentType = contentType.tryUTF8();
                }
                data = content.getBytes(contentType.getEncoding());
                response.setData(new ByteArrayInputStream(data));
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                data = new byte[0];
            }
        }
        response.setData(new ByteArrayInputStream(data));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int port = 8080;
        private String address = "127.0.0.1";
        private Map<Route, Handler> handlers = new HashMap<>();

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder route(String route, Handler httpHandler) {
            Route r = Route.parseRoute(route);
            handlers.put(r, httpHandler);
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}
