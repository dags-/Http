package me.dags.http.server;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class Context implements NanoHTTPD.IHTTPSession {

    private final Map<String, String> routeParams;
    private final NanoHTTPD.IHTTPSession session;

    Context(NanoHTTPD.IHTTPSession session, Map<String, String> routeParams) {
        this.routeParams = routeParams;
        this.session = session;
    }

    public String getRouteParam(String name) {
        return routeParams.get(name);
    }

    public String getRouteParam(String name, String defaultVal) {
        String value = getRouteParam(name);
        return value == null ? defaultVal : value;
    }

    public String getQueryParam(String name) {
        List<String> values = session.getParameters().getOrDefault(name, Collections.emptyList());
        if (values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public String getQueryParam(String name, String defaultVal) {
        String value = getQueryParam(name);
        return value == null ? defaultVal : value;
    }

    @Override
    public void execute() throws IOException {
        session.execute();
    }

    @Override
    public NanoHTTPD.CookieHandler getCookies() {
        return session.getCookies();
    }

    @Override
    public Map<String, String> getHeaders() {
        return session.getHeaders();
    }

    @Override
    public InputStream getInputStream() {
        return session.getInputStream();
    }

    @Override
    public NanoHTTPD.Method getMethod() {
        return session.getMethod();
    }

    @Override
    @Deprecated
    public Map<String, String> getParms() {
        return session.getParms();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return session.getParameters();
    }

    @Override
    public String getQueryParameterString() {
        return session.getQueryParameterString();
    }

    @Override
    public String getUri() {
        return session.getUri();
    }

    @Override
    public void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException {
        session.parseBody(files);
    }

    @Override
    public String getRemoteIpAddress() {
        return session.getRemoteIpAddress();
    }

    @Override
    public String getRemoteHostName() {
        return session.getRemoteHostName();
    }
}
