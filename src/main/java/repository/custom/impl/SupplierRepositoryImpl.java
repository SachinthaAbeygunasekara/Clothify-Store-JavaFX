package repository.custom.impl;

import db.DBConnection;
import entity.Supplier;
import repository.custom.SupplierRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepositoryImpl implements SupplierRepository {

    private static SupplierRepositoryImpl supplierRepository;

    public static SupplierRepositoryImpl getInstance() {
        return supplierRepository == null? supplierRepository = new SupplierRepositoryImpl(): supplierRepository;
    }

    @Override
    public boolean create(Supplier supplier) throws SQLException {
        String query = "INSERT INTO supplier (name, company, email, supplyItem) VALUES (?,?,?,?)";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getCompany());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getSupplyItem());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Supplier supplier) {
        String query = "Update supplier SET name = ?, company = ?, email = ?, supplyItem = ? WHERE id = ?";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getCompany());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getSupplyItem());
            statement.setInt(5, supplier.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String query = "DELETE FROM supplier WHERE id = ?";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Supplier getById(Integer id) throws SQLException {
        // Select columns explicitly and read by column labels to avoid dependency on DB column order
        String query = "SELECT id, name, company, email, supplyItem FROM supplier WHERE id = ?";
        try (PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Supplier(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("company"),
                            resultSet.getString("email"),
                            resultSet.getString("supplyItem")
                    );
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Supplier> getAll() throws SQLException {
        // Select columns explicitly and read by column labels to ensure correct field mapping regardless of table column order
        String query = "SELECT id, name, company, email, supplyItem FROM supplier";
        List<Supplier> suppliers = new ArrayList<>();
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery(query);
            while (resultSet.next()) {
                Supplier supplier = new Supplier(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("company"),
                        resultSet.getString("email"),
                        resultSet.getString("supplyItem")
                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            return null;
        }
        return suppliers;
    }
}
