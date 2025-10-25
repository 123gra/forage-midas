package com.jpmc.midascore;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleBalanceServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(33400), 0);
        server.createContext("/api/balance", new BalanceHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port 33400");
    }

    static class BalanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String userId = "unknown";

                if (query != null && query.contains("userId=")) {
                    userId = query.split("userId=")[1].split("&")[0];
                }

                String response = "{\"userId\":\"" + userId + "\",\"balance\":0.0}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}