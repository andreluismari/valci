package com.unimater;

import com.sun.net.httpserver.HttpServer;
import com.unimater.controller.HelloWorldHandler;
import com.unimater.dao.ProductTypeDAO;
import com.unimater.model.ProductType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main( String[] args ){
        try {
            HttpServer servidor = HttpServer.create(
                    new InetSocketAddress(8080),0
            );

            servidor.createContext("/helloWorld",
                    new HelloWorldHandler());

            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/your_db", "root", "admin1git "
            );

            ProductTypeDAO productTypeDAO = new ProductTypeDAO(connection);

            productTypeDAO.getAll().forEach(System.out::println);
            productTypeDAO.upsert(new ProductType(0, "Teste"));
            productTypeDAO.getAll().forEach(System.out::println);
            productTypeDAO.upsert(new ProductType(4, "Teste 2"));
            productTypeDAO.getAll().forEach(System.out::println);
            System.out.println(productTypeDAO.getById(1));
            productTypeDAO.delete(5);
            productTypeDAO.getAll().forEach(System.out::println);

            servidor.setExecutor(null);
            servidor.start();
            System.out.println("Servidor rodando na porta 8080");
        } catch (IOException e) {
            System.out.println(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
