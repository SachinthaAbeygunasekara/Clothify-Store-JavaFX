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


    @FXML
    void btnBackOnAction(MouseEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(
                getClass().getResource("/view/login-form.fxml")
        );

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginView);
    }

    @FXML
    void btnResetPassword(ActionEvent event) {
        if (txtOTP.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("OTP cannot be empty");
            alert.show();
            return;
        }

        if (!txtOTP.getText().equals(otp)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid OTP");
            alert.show();
            return;
        }

        if (txtNewPassword.getText().isEmpty() || txtConfirmPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password cannot be empty");
            alert.show();
            return;
        }

        if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password doesn't match");
            alert.show();
            return;
        }

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("ClothifySecureKey");
        String encryptedPassword = textEncryptor.encrypt(txtNewPassword.getText().trim());

        UserDTO passwordUpdatedUser = userService.updatePassword(txtForgotEmail.getText(), encryptedPassword);

        if (passwordUpdatedUser != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Password updated successfully");
            alert.show();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
                Stage stage = (Stage) txtForgotEmail.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Clothify");
                stage.setResizable(false);
                stage.centerOnScreen();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password update failed");
            alert.show();
        }
    }

    @FXML
    void btnSendOTP(ActionEvent event) {
        if (txtForgotEmail.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Email cannot be empty");
            alert.show();
        } else {
            otp = generateOTP();
            sendOtpEmail(txtForgotEmail.getText(), otp);
        }
    }

    private String generateOTP() {
        String otp = "";
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            otp += rand.nextInt(10);
        }
        return otp;
    }

    private void sendOtpEmail(String recipientEmail, String otp) {
        String host = "smtp.gmail.com";
        final String user = "clothify.store.app@gmail.com";
        final String password = "kjdo icma qwuv jlml";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Reset Your Password - Clothify Store");

            String emailContent = "<html><body>"
                    + "<h2>Clothify Store</h2>"
                    + "<p>Hello,</p>"
                    + "<p>You requested to reset your password at Clothify Store. Please use the following OTP to proceed:</p>"
                    + "<h2 style='color: #007BFF;'>" + otp + "</h2>"
                    + "<p>If you did not request a password reset, please ignore this email.</p>"
                    + "<p>Thank you for choosing Clothify Store!</p>"
                    + "<p>Best Regards,<br/>The Clothify Store Team</p>"
                    + "</body></html>";

            message.setContent(emailContent, "text/html");
            Transport.send(message);

            txtOTP.setEditable(true);
            txtNewPassword.setEditable(true);
            txtConfirmPassword.setEditable(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
