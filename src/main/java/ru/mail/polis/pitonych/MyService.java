package ru.mail.polis.pitonych;

import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class MyService implements KVService {
    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;
    private final static String PREFIX = "id=";


    public MyService(int port, @NotNull final MyDAO dao) throws IOException {

        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.dao = dao;

        this.server.createContext("/v0/status", http -> {
                    final String response = "ONLINE";
                    http.sendResponseHeaders(200, response.length());
                    http.getResponseBody().write(response.getBytes());
                    http.close();
                });

        this.server.createContext("/v0/entity", http -> {
            final String id;
            try {
                id = extractId(http.getRequestURI().getQuery());
            } catch (Exception e) {
                http.sendResponseHeaders(400, 0);
                http.close();
                return;
            }

                    switch (http.getRequestMethod()) {

                        case "GET":
                            try {
                                final byte[] getValue = dao.get(id);
                                http.sendResponseHeaders(200, getValue.length);
                                http.getResponseBody().write(getValue);
                            } catch (IOException e) {
                                http.sendResponseHeaders(404, 0);
                                http.close();
                            }
                            break;

                        case "DELETE":
                            dao.delete(id);
                            http.sendResponseHeaders(202, 0);
                            break;

                        case "PUT":
                            try {
                                final int contentLength = Integer.valueOf(http.getRequestHeaders().getFirst("Content-length"));
                                final byte[] putValue = new byte[contentLength];
                                if (contentLength != 0 && http.getRequestBody().read(putValue) != putValue.length) {
                                    throw new IOException("can't read");
                                }
                                dao.upsert(id, putValue);
                                http.sendResponseHeaders(201, contentLength);
                                http.getResponseBody().write(putValue);
                                break;
                            } catch (IllegalArgumentException e) {
                                http.sendResponseHeaders(400, 0);
                            } catch (NoSuchElementException e) {
                                http.sendResponseHeaders(404, 0);
                            }
                        default:
                            http.sendResponseHeaders(503, 0);
                    }

                    http.close();

                });
    }

    @NotNull
    private static String extractId(@NotNull final String query){
        if (!query.startsWith(PREFIX)){
            throw new IllegalArgumentException("support error");
        }
        String key = query.substring(PREFIX.length());
        if (key.isEmpty()) {
            throw new IllegalArgumentException("empty id!");
        }

        return query.substring(PREFIX.length());
    }

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop(0);
    }
}