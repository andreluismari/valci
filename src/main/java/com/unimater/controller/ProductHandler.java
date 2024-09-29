package com.unimater.controller;

import com.unimater.dao.ProductDAO;
import com.unimater.model.Product;
import com.unimater.model.ProductType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class ProductHandler implements HttpHandler {

    private ProductDAO productDAO;

    public ProductHandler(Connection connection) {
        this.productDAO = new ProductDAO(connection);
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
        List<Product> products = productDAO.getAll();
        String response = products.stream()
                .map(Product::toString)
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

        // Formato esperado: productTypeId|description|value
        String[] parts = body.split("\\|");
        if (parts.length != 3) {
            exchange.sendResponseHeaders(400, -1); // Requisição inválida
            return;
        }

        int productTypeId = Integer.parseInt(parts[0]);
        String description = parts[1];
        double value = Double.parseDouble(parts[2]);

        ProductType productType = new ProductType(productTypeId, "");
        Product product = new Product(0, productType, description, value);

        productDAO.upsert(product);

        String response = "Produto criado com sucesso.";
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

        String[] parts = body.split("\\|");
        if (parts.length != 3) {
            exchange.sendResponseHeaders(400, -1); // Requisição inválida
            return;
        }

        int productTypeId = Integer.parseInt(parts[0]);
        String description = parts[1];
        double value = Double.parseDouble(parts[2]);

        ProductType productType = new ProductType(productTypeId, "");
        Product product = new Product(id, productType, description, value);

        productDAO.upsert(product);

        String response = "Produto atualizado com sucesso.";
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
        productDAO.delete(id);

        String response = "Produto excluído com sucesso.";
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
