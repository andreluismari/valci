package com.unimater.controller;

import com.unimater.dao.ProductTypeDAO;
import com.unimater.model.ProductType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductTypeHandler implements HttpHandler {

    private ProductTypeDAO productTypeDAO;

    public ProductTypeHandler(Connection connection) {
        this.productTypeDAO = new ProductTypeDAO(connection);
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
        List<ProductType> productTypes = productTypeDAO.getAll();
        String response = productTypes.stream()
                .map(ProductType::toString)
                .collect(Collectors.joining("\n"));

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        ProductType productType = new ProductType();
        productType.setDescription(body);

        productTypeDAO.upsert(productType);

        String response = "ProductType criado com sucesso.";
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

        ProductType productType = new ProductType(id, body);
        productTypeDAO.upsert(productType);

        String response = "ProductType atualizado com sucesso.";
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
        productTypeDAO.delete(id);

        String response = "ProductType excluído com sucesso.";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        if (query == null) return Map.of();
        return Map.ofEntries(
                query.split("&")
                        .stream()
                        .map(s -> s.split("="))
                        .filter(arr -> arr.length == 2)
                        .map(arr -> Map.entry(arr[0], arr[1]))
                        .toArray(Map.Entry[]::new)
        );
    }
}
