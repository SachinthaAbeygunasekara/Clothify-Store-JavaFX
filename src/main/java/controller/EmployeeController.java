package controller;

import dto.EmployeeDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.EmployeeService;
import util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    EmployeeService employeeService;
    ObservableList<EmployeeDTO> employeeDTOObservableList;

    private EmployeeDTO employeeDTO;

    // Called by update-employee.fxml dialog after loading to pre-fill fields
    public void setEmployee(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;

        if (cmbUpdateEmployeeRole != null) {
            cmbUpdateEmployeeRole.getItems().clear();
            cmbUpdateEmployeeRole.getItems().addAll("Sales Assistant", "Cashier");
        }

        if (txtUpdateEmployeeName != null) {
            txtUpdateEmployeeName.setText(employeeDTO != null ? employeeDTO.getName() : "");
        }
        if (txtUpdateEmployeeEmail != null) {
            txtUpdateEmployeeEmail.setText(employeeDTO != null ? employeeDTO.getEmail() : "");
        }
        if (cmbUpdateEmployeeRole != null && employeeDTO != null) {
            cmbUpdateEmployeeRole.setValue(employeeDTO.getRole());
        }
    }

    @FXML
    private AnchorPane containerPane;

    @FXML
    private TableColumn<EmployeeDTO, Void> colEmployeeDeleteAction;

    @FXML
    private TableColumn<EmployeeDTO, String> colEmployeeEmail;

    @FXML
    private TableColumn<EmployeeDTO, Integer> colEmployeeId;

    @FXML
    private TableColumn<EmployeeDTO, String> colEmployeeName;

    @FXML
    private TableColumn<EmployeeDTO, String> colEmployeeRole;

    @FXML
    private TableColumn<EmployeeDTO, Void> colEmployeeUpdateAction;

    @FXML
    private TableView<EmployeeDTO> tblEmployeeDetails;

    @FXML
    private TextField txtSearchEmployee;

    @FXML
    private ComboBox<String> cmbUpdateEmployeeRole;

    @FXML
    private TextField txtUpdateEmployeeEmail;

    @FXML
    private TextField txtUpdateEmployeeName;


    @FXML
    private ComboBox<String> cmbAddEmployeeRole;

    @FXML
    private TextField txtAddEmployeeEmail;

    @FXML
    private TextField txtAddEmployeeName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize services
        employeeService = ServiceFactory.getInstance().getServiceType(ServiceType.EMPLOYEE);
        
        // If table and columns exist (employee main view), wire them
        if (tblEmployeeDetails != null) {
            // Keep a single observable list bound to the table for stable action cells
            employeeDTOObservableList = FXCollections.observableArrayList();
            tblEmployeeDetails.setItems(employeeDTOObservableList);

            if (colEmployeeId != null) colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colEmployeeName != null) colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
            if (colEmployeeEmail != null) colEmployeeEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            if (colEmployeeRole != null) colEmployeeRole.setCellValueFactory(new PropertyValueFactory<>("role"));

            if (colEmployeeUpdateAction != null) {
                // Center align the whole column
                colEmployeeUpdateAction.setStyle("-fx-alignment: CENTER;");
                colEmployeeUpdateAction.setCellFactory(param -> new TableCell<>() {
                    private final Button updateButton = new Button("Update");
                    private final HBox container = new HBox(updateButton);

                    {
                        updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;");
                        updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #363b3e; -fx-text-fill: white;"));
                        updateButton.setOnMouseExited(e -> updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        updateButton.setOnAction(event -> {
                            EmployeeDTO employee = getTableRow() != null ? getTableRow().getItem() : null;
                            if (employee != null) {
                                updateEmployee(employee);
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

            if (colEmployeeDeleteAction != null) {
                // Center align the whole column
                colEmployeeDeleteAction.setStyle("-fx-alignment: CENTER;");
                colEmployeeDeleteAction.setCellFactory(param -> new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");
                    private final HBox container = new HBox(deleteButton);

                    {
                        deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;");
                        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #4c0000; -fx-text-fill: white;"));
                        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        deleteButton.setOnAction(event -> {
                            EmployeeDTO employee = getTableRow() != null ? getTableRow().getItem() : null;
                            if (employee != null) {
                                deleteEmployee(employee);
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

            // Initial load
            populateTable();
        }

        // If we're in the add-employee dialog, set up role combo box
        if (cmbAddEmployeeRole != null) {
            cmbAddEmployeeRole.getItems().clear();
            cmbAddEmployeeRole.getItems().addAll("Sales Assistant", "Cashier");
        }
    }

    @FXML
    void btnAddEmployeeOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-employee.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Employee");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateEmployee(EmployeeDTO employeeDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-employee.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            // Get the controller (EmployeeController for the dialog) and pass the model to it
            EmployeeController controller = loader.getController();
            if (controller != null) controller.setEmployee(employeeDTO);
            stage.setTitle("Update Employee");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) return;

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Employee");
        deleteAlert.setHeaderText("Do you want to delete employee: " + employeeDTO.getName() + "?");
        deleteAlert.setContentText("Click 'Delete' to confirm, or 'Cancel' to abort.");

        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete the employee: " + employeeDTO.getName() + "?");
            confirmationAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                boolean isEmployeeDeleted = employeeService.deleteEmployee(employeeDTO.getId());

                if (isEmployeeDeleted) {
                    Platform.runLater(() -> populateTable());
                }
            }
        }
    }

    @FXML
    void btnSearchEmployeeOnAction(ActionEvent event) {
        if (txtSearchEmployee == null || tblEmployeeDetails == null) {
            return;
        }

        String searchText = txtSearchEmployee.getText();
        if (searchText == null) searchText = "";
        List<EmployeeDTO> employees = employeeService.getEmployees();
        ObservableList<EmployeeDTO> filteredList = FXCollections.observableArrayList();

        for (EmployeeDTO emp : employees) {
            if (emp != null && emp.getName() != null && emp.getEmail() != null &&
                    (emp.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            emp.getEmail().toLowerCase().contains(searchText.toLowerCase()))) {
                filteredList.add(emp);
            }
        }
        // Do not replace table items list; update the bound list instead
        if (employeeDTOObservableList != null) {
            employeeDTOObservableList.setAll(filteredList);
            tblEmployeeDetails.refresh();
        }
    }

    @FXML
    void btnClearSearchOnAction(ActionEvent event) {
        // Clear the search field and restore the full employee list
        if (txtSearchEmployee != null) {
            txtSearchEmployee.clear();
        }
        // Re-populate the table with all employees
        populateTable();
    }

    @FXML
    void btnUpdateEmployeeOnAction (ActionEvent event) {
        if (employeeDTO != null) {
            employeeDTO.setName(txtUpdateEmployeeName.getText());
            employeeDTO.setEmail(txtUpdateEmployeeEmail.getText());
            employeeDTO.setRole(cmbUpdateEmployeeRole.getValue());

            boolean isEmployeeUpdated = employeeService.updateEmployee(employeeDTO);

            if (isEmployeeUpdated) {
                Stage stage = (Stage) txtUpdateEmployeeEmail.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Error");
                alert.setHeaderText("Employee Not Updated");
                alert.show();
            }
        }
    }

    @FXML
    void btnSaveEmployeeOnAction(ActionEvent event) {
        // Handle save action from add-employee dialog
        if (txtAddEmployeeName == null || txtAddEmployeeEmail == null || cmbAddEmployeeRole == null) {
            return; // Not in add dialog
        }

        String name = txtAddEmployeeName.getText();
        String email = txtAddEmployeeEmail.getText();
        String role = cmbAddEmployeeRole.getValue();

        if (name == null || name.isBlank() || email == null || email.isBlank() || role == null || role.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fill all fields");
            alert.show();
            return;
        }

        EmployeeDTO newEmp = new EmployeeDTO(0, name.trim(), email.trim(), role.trim());
        boolean isSaved = employeeService.addEmployee(newEmp);

        if (isSaved) {
            // Close the dialog window
            Stage stage = (Stage) txtAddEmployeeName.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Employee Not Saved");
            alert.show();
        }
    }


    private void populateTable() {
        if (tblEmployeeDetails == null) return;
        if (employeeDTOObservableList != null) {
            employeeDTOObservableList.setAll(employeeService.getEmployees());
            tblEmployeeDetails.refresh();
        }
    }

}
