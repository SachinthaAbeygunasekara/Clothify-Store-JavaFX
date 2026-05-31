package repository.custom.impl;

import db.DBConnection;
import entity.Customer;
import repository.custom.CustomerRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    private static CustomerRepositoryImpl customerRepository;

    public static CustomerRepositoryImpl getInstance() {
        return customerRepository == null ? customerRepository = new CustomerRepositoryImpl() : customerRepository;
    }

    @Override
    public boolean create(Customer customer) throws SQLException {
        // Column name aligned with entity field `mobile`
        String query = "INSERT INTO customer (name, mobile, address) VALUES (?,?,?)";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getMobile());
            statement.setString(3, customer.getAddress());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Customer customer) {
        // Use correct column name `mobile`
        String query = "UPDATE customer SET name = ?, mobile = ?, address = ? WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getMobile());
            statement.setString(3, customer.getAddress());
            statement.setInt(4, customer.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(String id) {
        String query = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(id));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Customer getById(String id) throws SQLException {
        String query = "SELECT id, name, mobile, address FROM customer WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)
                    );
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        String query = "SELECT id, name, mobile, address FROM customer";
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(new Customer(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return customers;
    }
}
