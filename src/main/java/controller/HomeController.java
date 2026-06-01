package controller;

import dto.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML private Button btnHome;
    @FXML private Button btnProduct;
    @FXML private Button btnCustomer;
    @FXML private Button btnOrder;
    @FXML private Button btnEmployee;
    @FXML private Button btnSupplier;
    @FXML private Button btnReports;
    @FXML private Button btnCreateUser;
    @FXML private Button btnProfile;
    @FXML private Button btnLogout;
    @FXML
    private AnchorPane containerPane;
    
    private UserDTO currentUser;

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
        setUserButton();

        if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().equalsIgnoreCase("admin")) {
            btnCreateUser.setVisible(true);
        }
    }

    private void setUserButton() {
        if (currentUser != null && currentUser.getName() != null) {
            btnProfile.setText("   " + currentUser.getName());
        }
    }
    
    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            AnchorPane pane = loader.load();
            DashboardController dashboardController = loader.getController();
            if (dashboardController != null) {
                dashboardController.setCurrentUser(currentUser);
            }
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        btnCreateUser.setVisible(false);
    }
    
    @FXML
    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            AnchorPane pane = loader.load();
            DashboardController dashboardController = loader.getController();
            if (dashboardController != null) {
                dashboardController.setCurrentUser(currentUser);
            }
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateNavigationButtonStyle(btnHome);
    }
    
    @FXML
    private void handleProductManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/product.fxml"));
            AnchorPane pane = loader.load();
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load Product Management view");
        }
        updateNavigationButtonStyle(btnProduct);
    }
    
    @FXML
    private void handleCustomerManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer.fxml"));
            AnchorPane pane = loader.load();
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load Customer Management view");
        }
        updateNavigationButtonStyle(btnCustomer);
    }
    
    @FXML
    private void handleOrderHistory() {
        updateNavigationButtonStyle(btnOrder);
        showNotification("Order History", "Feature coming soon!");
    }
    
    @FXML
    private void handleEmployeeManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/employee.fxml"));
            AnchorPane pane = loader.load();
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load Employee Management view");
        }
        updateNavigationButtonStyle(btnEmployee);
    }
    
    @FXML
    private void handleSupplierManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/supplier.fxml"));
            AnchorPane pane = loader.load();
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load Customer Management view");
        }
        updateNavigationButtonStyle(btnSupplier);
    }
    
    @FXML
    private void handleReports() {
        updateNavigationButtonStyle(btnReports);
        showNotification("Reports", "Feature coming soon!");
    }
    
    @FXML
    private void handleCreateUserAccount() {
        try {
            AnchorPane pane = new FXMLLoader().load(getClass().getResource("/view/create-user-account.fxml"));
            containerPane.getChildren().clear();
            containerPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load Create User Account view");
        }
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to logout from Clothify Store?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-form.fxml"));
                AnchorPane root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Clothify Store - Login");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error", "Failed to load Login view");
            }
        }
    }
    
    private void updateNavigationButtonStyle(Button clickedButton) {
        // Remove active style from all buttons
        btnHome.getStyleClass().remove("nav-button-active");
        btnProduct.getStyleClass().remove("nav-button-active");
        btnCustomer.getStyleClass().remove("nav-button-active");
        btnOrder.getStyleClass().remove("nav-button-active");
        btnEmployee.getStyleClass().remove("nav-button-active");
        btnSupplier.getStyleClass().remove("nav-button-active");
        btnReports.getStyleClass().remove("nav-button-active");
        
        // Add active style to clicked button
        if (!clickedButton.getStyleClass().contains("nav-button-active")) {
            clickedButton.getStyleClass().add("nav-button-active");
        }
    }
    
    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
