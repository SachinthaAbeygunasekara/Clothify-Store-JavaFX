package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LoginController {

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnSignUp;

    @FXML
    private AnchorPane containerPane;

    @FXML
    private Label lblForgotPassword;

    @FXML
    private TextField txtLoginEmail;

    @FXML
    private PasswordField txtLoginPassword;

    @FXML
    void btnLoginOnAction(ActionEvent event) {

    }

    @FXML
    void btnSignUpOnAction(ActionEvent event) {
        try {
            AnchorPane signUpView = FXMLLoader.load(
                    getClass().getResource("/view/create-user-account-view.fxml")
            );
            containerPane.getChildren().clear();
            containerPane.getChildren().add(signUpView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void lblForgotPasswordMouseEntered(MouseEvent event) {

    }

    @FXML
    void lblForgotPasswordMouseExited(MouseEvent event) {

    }

    @FXML
    void lblForgotPasswordOnAction(MouseEvent event) {

    }
}
