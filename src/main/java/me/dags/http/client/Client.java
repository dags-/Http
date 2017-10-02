package me.dags.http.client;

import me.dags.http.json.JsonNode;
import me.dags.http.json.JsonParser;
import okhttp3.*;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
public final class Client {

    private static final OkHttpClient client = new OkHttpClient();

    private Client() {

    }

    public static HttpUrl.Builder url(String url) {
        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed == null) {
            return new HttpUrl.Builder();
        }
        return parsed.newBuilder();
    }

    public static Response execute(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public static Response get(String url) throws IOException {
        return execute(request(url).get().build());
    }

    public static Response get(HttpUrl url) throws IOException {
        return execute(request(url).get().build());
    }

    public static Response post(HttpUrl url, RequestBody body) throws IOException {
        return execute(request(url).post(body).build());
    }

    public static Request.Builder request(String url) {
        return request(url(url).build());
    }

    public static Request.Builder request(HttpUrl url) {
        return new Request.Builder().url(url);
    }

    public static MultipartBody.Builder multipart() {
        return new MultipartBody.Builder();
    }

    public static FormBody.Builder form() {
        return new FormBody.Builder();
    }

    public static JsonNode json(Request request) throws IOException {
        Response response = execute(request);
        return parseJson(response);
    }

    public static JsonNode parseJson(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return JsonNode.NULL;
        }
        return new JsonParser(body.charStream()).parse();
    }
}
