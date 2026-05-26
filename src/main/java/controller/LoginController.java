package controller;

import com.jfoenix.controls.JFXButton;
import dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jasypt.util.text.BasicTextEncryptor;
import service.ServiceFactory;
import service.custom.UserService;
import util.ServiceType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class LoginController {

    static String otp;

    UserService userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);

    @FXML
    private JFXButton btnLogin;

    @FXML
    private AnchorPane containerPane;

    @FXML
    private Label lblForgotPassword;

    @FXML
    private TextField txtLoginEmail;

    @FXML
    private PasswordField txtLoginPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtForgotEmail;

    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private TextField txtOTP;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        if (txtLoginEmail.getText().isEmpty() || txtLoginPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Fields are empty");
            alert.show();
        } else {
            UserDTO loginUser = userService.login(txtLoginEmail.getText(), txtLoginPassword.getText());
            if (loginUser == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Username or Password is incorrect");
                alert.show();
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
                    Stage stage = (Stage) txtLoginEmail.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Clothify");
                    stage.setResizable(false);
                    stage.centerOnScreen();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Unable to load the home view.");
                    alert.show();
                }
            }
        }
    }

    @FXML
    void lblForgotPasswordMouseEntered(MouseEvent event) {
        lblForgotPassword.setStyle("-fx-text-fill: #a30000;");
    }

    @FXML
    void lblForgotPasswordMouseExited(MouseEvent event) {
        lblForgotPassword.setStyle("-fx-text-fill: #000000;");
    }

    @FXML
    void lblForgotPasswordOnAction(MouseEvent event) {
        try {
            AnchorPane signUpView = FXMLLoader.load(
                    getClass().getResource("/view/forgot-password-form.fxml")
            );
            containerPane.getChildren().clear();
            containerPane.getChildren().add(signUpView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
