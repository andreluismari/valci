package com.unimater.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleItem implements Entity {
    private int id;
    private Product product;
    private int quantity;
    private double percentualDiscount;

    public SaleItem(int id, Product product, int quantity, double percentualDiscount) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.percentualDiscount = percentualDiscount;
    }

    public SaleItem() {}

    @Override
    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPercentualDiscount() {
        return percentualDiscount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPercentualDiscount(double percentualDiscount) {
        this.percentualDiscount = percentualDiscount;
    }

    @Override
    public Entity constructFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.product = new Product(rs.getInt("product_id"), null, "", 0.0); // Placeholder for actual lookup
        this.quantity = rs.getInt("quantity");
        this.percentualDiscount = rs.getDouble("percentual_discount");
        return this;
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, product.getId());
        preparedStatement.setInt(2, quantity);
        preparedStatement.setDouble(3, percentualDiscount);
        return preparedStatement;
    }
}
