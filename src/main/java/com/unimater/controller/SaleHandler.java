package com.unimater.controller;

import com.unimater.dao.SaleDAO;
import com.unimater.model.Sale;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class SaleHandler implements HttpHandler {

    private SaleDAO saleDAO;

    public SaleHandler(Connection connection) {
        this.saleDAO = new SaleDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            handleGet(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handlePost(exchange);
        } else if ("PUT".equalsIgnoreCase(method)) {
            handlePut(exchange);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            handleDelete(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Método não permitido
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Sale> sales = saleDAO.getAll();
        String response = sales.stream()
                .map(Sale::toString)
                .collect(Collectors.joining("\n"));

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Sale sale = new Sale();
        sale.setInsertAt(new Timestamp(System.currentTimeMillis()));

        saleDAO.upsert(sale);

        String response = "Venda criada com sucesso.";
        exchange.sendResponseHeaders(201, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        String idStr = params.get("id");

        if (idStr == null) {
            exchange.sendResponseHeaders(400, -1); // Requisição inválida
            return;
        }

        int id = Integer.parseInt(idStr);

        InputStream is = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        Timestamp timestamp;
        if (body.isEmpty()) {
            timestamp = new Timestamp(System.currentTimeMillis());
        } else {
            timestamp = Timestamp.valueOf(body);
        }

        Sale sale = new Sale();
        sale.setId(id);
        sale.setInsertAt(timestamp);

        saleDAO.upsert(sale);

        String response = "Venda atualizada com sucesso.";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        String idStr = params.get("id");

        if (idStr == null) {
            exchange.sendResponseHeaders(400, -1); // Requisição inválida
            return;
        }

        int id = Integer.parseInt(idStr);
        saleDAO.delete(id);

        String response = "Venda excluída com sucesso.";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(query.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }
}
