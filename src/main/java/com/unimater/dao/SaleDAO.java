package com.unimater.dao;

import com.unimater.model.Sale;

import java.sql.Connection;
import java.util.List;

public class SaleDAO extends GenericDAOImpl<Sale> {

    private final String TABLE_NAME = "sale";
    private final List<String> COLUMNS = List.of("insert_at");

    public SaleDAO(Connection connection) {
        super(Sale::new, connection);
        super.tableName = TABLE_NAME;
        super.columns = COLUMNS;
    }

}
