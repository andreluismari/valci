package com.unimater.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductType implements Entity {

    private int id;
    private String description;

    public ProductType(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.description = rs.getString("description");
    }

    public ProductType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public ProductType() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    // Adicionando o setter para 'description'
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Entity constructFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.description = rs.getString("description");
        return this;
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, getDescription());
        return preparedStatement;
    }

    @Override
    public String toString() {
        return "ProductType{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
