package controller;

import com.jfoenix.controls.JFXButton;
import dto.UserDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    private static UserDTO currentUser;

    @FXML
    private JFXButton btnPayBill;

    @FXML
    private ComboBox<?> cmbSelectCustomer;

    @FXML
    private FlowPane flowPaneCart;

    @FXML
    private FlowPane flowPaneProducts;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblTime;

    @FXML
    private AnchorPane paneDashboard;

    @FXML
    private AnchorPane panePlaceOrder;

    @FXML
    private TextField txtSearchProductText;

    @FXML
    private Label txtTotalAmount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAdnTime();
    }

    @FXML
    void btnAccessoriesOnAction(ActionEvent event) {

    }

    @FXML
    void btnAddNewCustomerOnAction(ActionEvent event) {

    }

    @FXML
    void btnAllProductsOnAction(ActionEvent event) {

    }

    @FXML
    void btnFootwearOnAction(ActionEvent event) {

    }

    @FXML
    void btnGentsOnAction(ActionEvent event) {

    }

    @FXML
    void btnKidsOnAction(ActionEvent event) {

    }

    @FXML
    void btnLadiesOnAction(ActionEvent event) {

    }

    @FXML
    void btnPayBillOnAction(ActionEvent event) {

    }

    @FXML
    void btnSearchProductOnAction(ActionEvent event) {

    }

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    private void loadDateAdnTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        lblDate.setText(formatter.format(date));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalTime now = LocalTime.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                    lblTime.setText(now.format(dateFormatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

}
