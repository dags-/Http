package me.dags.http.server;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author dags <dags@dags.me>
 */
public class ResponseWriter {

    private static final NanoHTTPD.Response.IStatus OK = NanoHTTPD.Response.Status.OK;
    private static final NanoHTTPD.Response.IStatus DENY = NanoHTTPD.Response.Status.FORBIDDEN;
    private static final NanoHTTPD.Response.IStatus REDIRECT = NanoHTTPD.Response.Status.REDIRECT;

    private final Server server;
    private final NanoHTTPD.Response response;

    public ResponseWriter(Server server, NanoHTTPD.Response response) {
        this.server = server;
        this.response = response;
    }

    NanoHTTPD.Response getResponse() {
        return response;
    }

    public ResponseWriter close() throws IOException {
        response.close();
        return this;
    }

    public ResponseWriter addHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    public ResponseWriter closeConnection(boolean close) {
        response.closeConnection(close);
        return this;
    }

    public ResponseWriter setGzipEncoding(boolean encodeAsGzip) {
        response.setGzipEncoding(encodeAsGzip);
        return this;
    }

    public ResponseWriter setKeepAlive(boolean useKeepAlive) {
        response.setKeepAlive(useKeepAlive);
        return this;
    }

    public ResponseWriter setChunkedTransfer(boolean chunkedTransfer) {
        response.setChunkedTransfer(chunkedTransfer);
        return this;
    }

    public ResponseWriter setBody(String body) {
        server.setBody(response, body);
        return this;
    }

    public ResponseWriter setBody(InputStream data) {
        response.setData(data);
        return this;
    }

    public ResponseWriter accept() {
        response.setStatus(OK);
        return this;
    }

    public ResponseWriter deny() {
        response.setStatus(DENY);
        clear();
        return this;
    }

    public ResponseWriter redirect() {
        response.setStatus(REDIRECT);
        return this;
    }

    public ResponseWriter clear() {
        setBody((InputStream) null);
        return this;
    }

    public ResponseWriter html() {
        response.setMimeType(NanoHTTPD.MIME_HTML);
        return this;
    }

    public ResponseWriter text() {
        response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
        return this;
    }

    public ResponseWriter html(String html) {
        return accept().html().setBody(html);
    }

    public ResponseWriter text(String text) {
        return accept().text().setBody(text);
    }

    public ResponseWriter redirect(String url) {
        return redirect().text().clear().addHeader("Location", url);
    }
}
