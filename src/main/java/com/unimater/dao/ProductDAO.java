package com.unimater.dao;

import com.unimater.model.Product;

import java.sql.Connection;
import java.util.List;

public class ProductDAO extends GenericDAOImpl<Product> {

    private final String TABLE_NAME = "product";
    private final List<String> COLUMNS = List.of("product_type_id", "description", "value");

    public ProductDAO(Connection connection) {
        super(Product::new, connection);
        super.tableName = TABLE_NAME;
        super.columns = COLUMNS;
    }

}
