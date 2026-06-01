package repository.custom.impl;

import db.DBConnection;
import entity.User;
import org.jasypt.util.text.BasicTextEncryptor;
import repository.custom.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {
    @Override
    public User login(String email, String password) {
        String sql = "SELECT password FROM user WHERE email = ?";
        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword("ClothifySecureKey");

                String passwordFromDB = resultSet.getString("password");
                String decryptedPassword = textEncryptor.decrypt(passwordFromDB);

                if (password.trim().equals(decryptedPassword)) {
                    String query = "SELECT * FROM user WHERE email = ?";
                    PreparedStatement statement2 = DBConnection.getInstance().getConnection().prepareStatement(query);
                    statement2.setString(1, email);

                    ResultSet resultSet2 = statement2.executeQuery();
                    if (resultSet2.next()) {
                        return new User(
                                resultSet2.getInt("id"),
                                resultSet2.getString("name"),
                                resultSet2.getString("email"),
                                "xxxxx",
                                resultSet2.getString("role"),
                                resultSet2.getString("regDate")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User updatePassword(String email, String password) {
        try {
            String query = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, email);
            boolean isPasswordUpdated = preparedStatement.executeUpdate() > 0;

            if (isPasswordUpdated) {
                String sql = "SELECT * FROM user WHERE email = ?";
                PreparedStatement statement2 = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement2.setString(1, email);

                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    return new User(
                            resultSet2.getInt("id"),
                            resultSet2.getString("name"),
                            resultSet2.getString("email"),
                            "xxxxx",
                            resultSet2.getString("role"),
                            resultSet2.getString("regDate")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";

        try {
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("regDate")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean saveUser(User user) {
        if (user != null) {
            String sql = "INSERT INTO user (name, email, password, role, regDate) VALUES (?, ?, ?, ?, ?)";
            try {
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.setString(4, user.getRole());
                statement.setString(5, user.getRegDate());
                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
