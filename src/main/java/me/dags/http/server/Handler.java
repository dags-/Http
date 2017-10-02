package me.dags.http.server;

/**
 * @author dags <dags@dags.me>
 */
public interface Handler {

    void handle(ResponseWriter wr, Context c) throws Exception;
}
