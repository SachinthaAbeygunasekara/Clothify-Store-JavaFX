package controller;

import dto.CustomerDTO;
import dto.EmployeeDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.CustomerService;
import util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    CustomerService customerService;
    ObservableList<CustomerDTO> customerDTOObservableList;

    private CustomerDTO customerDTO;

    public void setCustomer(CustomerDTO customerDTO) {
        this.customerDTO = customerDTO;
        if (txtUpdateCustomerName != null) {
            txtUpdateCustomerName.setText(customerDTO != null ? customerDTO.getName() : "");
        }
        if (txtUpdateCustomerAddress != null) {
            txtUpdateCustomerAddress.setText(customerDTO != null ? customerDTO.getAddress() : "");
        }
        if (txtUpdateCustomerMobileNumber != null) {
            txtUpdateCustomerMobileNumber.setText(customerDTO != null ? customerDTO.getMobile() : "");
        }
    }

    @FXML
    private TableColumn<CustomerDTO, String> colCustomerAddress;

    @FXML
    private TableColumn<CustomerDTO, Void> colCustomerDeleteAction;

    @FXML
    private TableColumn<CustomerDTO, Integer> colCustomerId;

    @FXML
    private TableColumn<CustomerDTO, String> colCustomerName;

    @FXML
    private TableColumn<CustomerDTO, Void> colCustomerUpdateAction;

    @FXML
    private TableColumn<CustomerDTO, String> colMobileNumber;

    @FXML
    private TableView<CustomerDTO> tblCustomerDetails;

    @FXML
    private TextField txtSearchCustomer;

    @FXML
    private TextField txtUpdateCustomerAddress;

    @FXML
    private TextField txtUpdateCustomerMobileNumber;

    @FXML
    private TextField txtUpdateCustomerName;

    @FXML
    private TextField txtAddCustomerAddress;

    @FXML
    private TextField txtAddCustomerMobileNumber;

    @FXML
    private TextField txtAddCustomerName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerService = ServiceFactory.getInstance().getServiceType(ServiceType.CUSTOMERS);

        if (tblCustomerDetails != null) {
            // Keep a single observable list bound to the table for stable action cells
            customerDTOObservableList = FXCollections.observableArrayList();
            tblCustomerDetails.setItems(customerDTOObservableList);

            if (colCustomerId != null) colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colCustomerName != null) colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
            if (colMobileNumber != null) colMobileNumber.setCellValueFactory(new PropertyValueFactory<>("mobile"));
            if (colCustomerAddress != null) colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));


            if (colCustomerUpdateAction != null) {
                // Center align the whole column
                colCustomerUpdateAction.setStyle("-fx-alignment: CENTER;");
                colCustomerUpdateAction.setCellFactory(param -> new TableCell<>() {
                    private final Button updateButton = new Button("Update");
                    private final HBox container = new HBox(updateButton);

                    {
                        updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;");
                        updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #363b3e; -fx-text-fill: white;"));
                        updateButton.setOnMouseExited(e -> updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        updateButton.setOnAction(event -> {
                            CustomerDTO customer = getTableRow() != null ? getTableRow().getItem() : null;
                            if (customer != null) {
                                updateCustomer(customer);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : container);
                        setText(null);
                        setAlignment(Pos.CENTER);
                    }
                });
            }

            if (colCustomerDeleteAction != null) {
                // Center align the whole column
                colCustomerDeleteAction.setStyle("-fx-alignment: CENTER;");
                colCustomerDeleteAction.setCellFactory(param -> new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");
                    private final HBox container = new HBox(deleteButton);

                    {
                        deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;");
                        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #4c0000; -fx-text-fill: white;"));
                        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        deleteButton.setOnAction(event -> {
                            CustomerDTO customer = getTableRow() != null ? getTableRow().getItem() : null;
                            if (customer != null) {
                                deleteCustomer(customer);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : container);
                        setText(null);
                        setAlignment(Pos.CENTER);
                    }
                });
            }
            populateTable();
        }
    }

    @FXML
    void btnAddCustomerOnAction(javafx.event.ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/add-customer.fxml"))));
            stage.setTitle("Add Customer");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSearchCustomerOnAction(ActionEvent event) {
        if (txtSearchCustomer == null || tblCustomerDetails == null) {
            return;
        }

        String searchText = txtSearchCustomer.getText();
        if (searchText == null) searchText = "";
        List<CustomerDTO> customers = customerService.getCustomers();
        ObservableList<CustomerDTO> filteredList = FXCollections.observableArrayList();

        for (CustomerDTO cus : customers) {
            if (cus != null && cus.getName() != null && cus.getMobile() != null &&
                    (cus.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            cus.getMobile().contains(searchText.toLowerCase()))) {
                filteredList.add(cus);
            }
        }
        // Do not replace table items list; update the bound list instead
        if (customerDTOObservableList != null) {
            customerDTOObservableList.setAll(filteredList);
            tblCustomerDetails.refresh();
        }
    }

    @FXML
    void btnClearSearchOnAction(ActionEvent event) {
        if (txtSearchCustomer != null) {
            txtSearchCustomer.clear();
        }
        populateTable();
    }

    @FXML
    void btnUpdateCustomerOnAction(ActionEvent event) {
        if (customerDTO != null) {
            customerDTO.setName(txtUpdateCustomerName.getText());
            customerDTO.setMobile(txtUpdateCustomerMobileNumber.getText());
            customerDTO.setAddress(txtUpdateCustomerAddress.getText());

            boolean isCustomerUpdated = customerService.updateCustomer(customerDTO);

            if (isCustomerUpdated) {
                Stage stage = (Stage) txtUpdateCustomerAddress.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Error");
                alert.setHeaderText("Customer Not Updated");
                alert.show();
            }
        }
    }

    @FXML
    void btnSaveCustomerOnAction(ActionEvent event) {
        if (txtAddCustomerName == null || txtAddCustomerMobileNumber == null || txtAddCustomerAddress == null) {
            return;
        }

        String name = txtAddCustomerName.getText();
        String mobile = txtAddCustomerMobileNumber.getText();
        String address = txtAddCustomerAddress.getText();

        if (name == null || name.isBlank() || mobile == null || mobile.isBlank() || address == null || address.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fill all fields");
            alert.show();
            return;
        }

        CustomerDTO newCus = new CustomerDTO(0, name.trim(), mobile.trim(), address.trim());
        boolean isSaved = customerService.addCustomer(newCus);

        if (isSaved) {
            Stage stage = (Stage) txtAddCustomerName.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Customer Not Saved");
            alert.show();
        }
    }

    private void updateCustomer(CustomerDTO customerDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-customer.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            
            CustomerController controller = loader.getController();
            if (controller != null) controller.setCustomer(customerDTO);
            stage.setTitle("Update Customer");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCustomer(CustomerDTO customerDTO) {
        if (customerDTO == null) return;

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Customer");
        deleteAlert.setHeaderText("Do you want to delete customer: " + customerDTO.getName() + "?");
        deleteAlert.setContentText("Click 'Delete' to confirm, or 'Cancel' to abort.");

        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete the customer: " + customerDTO.getName() + "?");
            confirmationAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                boolean isCustomerDeleted = customerService.deleteCustomer(customerDTO.getId());

                if (isCustomerDeleted) {
                    Platform.runLater(() -> populateTable());
                }
            }
        }
    }

    private void populateTable() {
        if (tblCustomerDetails == null) return;
        if (customerDTOObservableList != null) {
            customerDTOObservableList.setAll(customerService.getCustomers());
            tblCustomerDetails.refresh();
        }
    }

}
