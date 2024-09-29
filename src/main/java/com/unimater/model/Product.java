package com.unimater.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product implements Entity {

    private int id;
    private ProductType productType;
    private String description;
    private double value;

    public Product(int id, ProductType productType, String description, double value) {
        this.id = id;
        this.productType = productType;
        this.description = description;
        this.value = value;
    }

    public Product() {
    }

    @Override
    public int getId() {
        return id;
    }

    public ProductType getProductType() {
        return productType;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    // Adicionando os setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public Entity constructFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        int productTypeId = rs.getInt("product_type_id");
        String description = rs.getString("description");
        double value = rs.getDouble("value");
        this.productType = new ProductType(productTypeId, ""); // Você pode buscar a descrição real se necessário
        this.description = description;
        this.value = value;
        return this;
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, productType.getId());
        preparedStatement.setString(2, description);
        preparedStatement.setDouble(3, value);
        return preparedStatement;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productType=" + productType +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}
