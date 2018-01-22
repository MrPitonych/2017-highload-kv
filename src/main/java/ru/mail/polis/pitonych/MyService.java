package ru.mail.polis.pitonych;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.polis.KVService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class MyService implements KVService {

    private static final String PREFIX = "id=";
    @NotNull
    private final HttpServer server;
    @NotNull
    private final MyDAO dao;

    public MyService(int port, @NotNull MyDAO dao) throws IOException {
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

                switch (http.getRequestMethod()) {
                    case "GET":
                        try {
                            final byte[] getValue = dao.get(id);
                            http.sendResponseHeaders(200, getValue.length);
                            http.getResponseBody().write(getValue);
                        } catch (NoSuchElementException e) {
                            http.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "PUT":
                        ByteArrayOutputStream putValue = new ByteArrayOutputStream();
                        InputStream getValue = http.getRequestBody();
                        byte[] buffer = new byte[8192];
                        int lenB;
                        while ((lenB = getValue.read(buffer)) != -1) {
                            putValue.write(buffer, 0, lenB);
                        }
                        dao.upsert(id, putValue.toByteArray());
                        http.sendResponseHeaders(201, 0);
                        break;
                    case "DELETE":
                        dao.delete(id);
                        http.sendResponseHeaders(202, 0);
                        break;
                    default:
                        http.sendResponseHeaders(405, 0);
                        break;
                }
                http.close();

            } catch (IllegalArgumentException e) {
                http.sendResponseHeaders(400, 0);
                http.close();
            }
        });
    }

    private String extractId(@NotNull String query) {
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
        server.start();
    }

    @Override
    public void stop() {
        server.stop(0);
    }
}
