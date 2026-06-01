package repository.custom.impl;

import db.DBConnection;
import entity.OrderDetail;
import repository.custom.OrderDetailRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    private static OrderDetailRepositoryImpl orderDetailsRepository;

    public static OrderDetailRepositoryImpl getInstance() {
        return orderDetailsRepository == null ? orderDetailsRepository = new OrderDetailRepositoryImpl() : orderDetailsRepository;
    }

    @Override
    public boolean save(List<OrderDetail> orderProducts) {
        String query = "INSERT INTO orderdetail (orderId, productId, quantity) VALUES (?, ?, ?)";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            for (OrderDetail orderDetail : orderProducts) {
                statement.setInt(1, orderDetail.getOrderId());
                statement.setInt(2, orderDetail.getProductId());
                statement.setInt(3, orderDetail.getQuantity());

                if (statement.executeUpdate() <= 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order details", e);
        }
    }

    @Override
    public List<OrderDetail> getAll() {
        String query = "SELECT id, orderId, productId, quantity FROM orderdetail";
        List<OrderDetail> orderProducts = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderProducts.add(new OrderDetail(
                        resultSet.getInt("id"),
                        resultSet.getInt("orderId"),
                        resultSet.getInt("productId"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching order details", e);
        }
        return orderProducts;
    }
}
