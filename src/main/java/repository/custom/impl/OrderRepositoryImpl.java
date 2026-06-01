package repository.custom.impl;

import db.DBConnection;
import entity.Order;
import entity.OrderDetail;
import repository.custom.OrderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    private static OrderRepositoryImpl orderRepository;

    public static OrderRepositoryImpl getInstance() {
        return orderRepository == null ? orderRepository = new OrderRepositoryImpl() : orderRepository;
    }

    @Override
    public int getLastOrderId() {
        String query = "SELECT id FROM orders ORDER BY id DESC LIMIT 1";
        try (
                Statement stmt = DBConnection.getInstance().getConnection().createStatement();
                ResultSet resultSet = stmt.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching last OrderID", e);
        }
        return -1;
    }

    @Override
    public boolean create(Order order) throws SQLException {
        String query = "INSERT INTO orders (orderDate, totalPrice, paymentMethod, userId, customerId) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        try{
            PreparedStatement statement = null;

            try {
                connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setDate(1, order.getOrderDate());
                statement.setDouble(2, order.getTotalPrice());
                statement.setString(3, order.getPaymentMethod());
                statement.setInt(4, order.getUserId());
                statement.setObject(5, order.getCustomerId() != 0 ? order.getCustomerId() : null);
                int rowsInserted = statement.executeUpdate();
                
                if (rowsInserted > 0) {
                    // Get the generated order ID
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    int generatedOrderId = 0;
                    if (generatedKeys.next()) {
                        generatedOrderId = generatedKeys.getInt(1);
                    }
                    
                    // Set the generated ID in all order details
                    for (OrderDetail detail : order.getOrderDetailList()) {
                        detail.setOrderId(generatedOrderId);
                    }
                    
                    boolean isSavedToOrderDetails = OrderDetailRepositoryImpl.getInstance().save(order.getOrderDetailList());

                    if (isSavedToOrderDetails) {
                        boolean isProductTableUpdated = ProductRepositoryImpl.getInstance().updateQuantity(order.getOrderDetailList());

                        if (isProductTableUpdated) {
                            connection.commit();
                            return true;
                        }
                    }
                }
                connection.rollback();
                return false;
            } catch (SQLException e) {
                System.err.println("Error creating order: " + e.getMessage());
                e.printStackTrace();
                connection.rollback();
                return false;
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean update(Order order) {
        String query = "UPDATE orders SET orderDate = ?, totalPrice = ?, paymentMethod = ?, userId = ?, customerId = ? WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setDate(1, order.getOrderDate());
            statement.setDouble(2, order.getTotalPrice());
            statement.setString(3, order.getPaymentMethod());
            statement.setInt(4, order.getUserId());
            statement.setInt(5, order.getCustomerId());
            statement.setInt(6, order.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String query = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(id));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    @Override
    public Order getById(String id) throws SQLException {
        String query = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToOrderEntity(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching order", e);
        }
        return null;
    }

    @Override
    public List<Order> getAll() throws SQLException {
        String query = "SELECT id, orderDate, totalPrice, paymentMethod, userId, customerId FROM orders";
        List<Order> orders = new ArrayList<>();

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                orders.add(mapResultSetToOrderEntity(resultSet));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }

        return orders;
    }

    private Order mapResultSetToOrderEntity(ResultSet resultSet) throws SQLException {
        return new Order(
                resultSet.getInt("id"),
                resultSet.getDate("orderDate"),
                resultSet.getDouble("totalPrice"),
                resultSet.getString("paymentMethod"),
                resultSet.getInt("userId"),
                resultSet.getInt("customerId"),
                List.of()
        );
    }
}
