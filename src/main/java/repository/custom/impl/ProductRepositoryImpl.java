package repository.custom.impl;

import db.DBConnection;
import entity.OrderDetail;
import entity.Product;
import repository.custom.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    private static ProductRepositoryImpl productRepository;

    public static ProductRepositoryImpl getInstance() {
        return productRepository == null ? productRepository = new ProductRepositoryImpl() : productRepository;
    }
    @Override
    public boolean create(Product product) throws SQLException {
        String query = "INSERT INTO product (name, category, size, price, quantity, image, supplierID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setString(3, product.getSize());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setString(6, product.getImage());
            statement.setInt(7, product.getSupplierID());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        // Use column labels exactly as defined in the schema
        String query = "UPDATE product SET name = ?, category = ?, size = ?, price = ?, quantity = ?, image = ?, supplierID = ? WHERE id = ?";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setString(3, product.getSize());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setString(6, product.getImage());
            statement.setInt(7, product.getSupplierID());
            statement.setInt(8, product.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String query = "DELETE FROM product WHERE id = ?";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Product getById(Integer id) throws SQLException {
        // Select explicit columns and read by label to avoid column-order bugs
        String query = "SELECT id, name, category, size, price, quantity, image, supplierID FROM product WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("category"),
                            resultSet.getString("size"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("quantity"),
                            resultSet.getString("image"),
                            resultSet.getInt("supplierID")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        // Avoid SELECT * and map by labels to prevent misalignment when DB column order changes
        String query = "SELECT id, name, category, size, price, quantity, image, supplierID FROM product";
        List<Product> products = new ArrayList<>();
        try (ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery(query)) {
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("category"),
                        resultSet.getString("size"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("image"),
                        resultSet.getInt("supplierID")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public boolean updateQuantity(List<OrderDetail> orderDetailList) {
        for (OrderDetail orderDetail : orderDetailList) {
            boolean isUpdate = minusQuantity(orderDetail);
            if (!isUpdate) {
                return false;
            }
        }
        return true;
    }

    public boolean minusQuantity(OrderDetail orderDetail) {
        String query = "UPDATE product SET quantity = quantity-? WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderDetail.getQuantity());
            statement.setInt(2, orderDetail.getProductId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
