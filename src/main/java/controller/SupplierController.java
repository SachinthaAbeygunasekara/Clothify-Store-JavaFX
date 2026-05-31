package controller;

import dto.SupplierDTO;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.SupplierService;
import util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    SupplierService supplierService;
    ObservableList<SupplierDTO> supplierDTOObservableList;

    private SupplierDTO supplierDTO;

    public void setSupplier(SupplierDTO supplierDTO) {
        this.supplierDTO = supplierDTO;
        if (txtUpdateSupplierName != null) {
            txtUpdateSupplierName.setText(supplierDTO != null ? supplierDTO.getName() : "");
        }
        if (txtUpdateSupplierCompany != null) {
            txtUpdateSupplierCompany.setText(supplierDTO != null ? supplierDTO.getCompany() : "");
        }
        if (txtUpdateSupplierEmail != null) {
            txtUpdateSupplierEmail.setText(supplierDTO != null ? supplierDTO.getEmail() : "");
        }
        if (cmbUpdateSupplyItem != null && supplierDTO != null) {
            cmbUpdateSupplyItem.setValue(supplierDTO.getSupplyItem());
        }
    }

    @FXML
    private TableColumn<SupplierDTO, String> colSupplierCompany;

    @FXML
    private TableColumn<SupplierDTO, Void> colSupplierDeleteAction;

    @FXML
    private TableColumn<SupplierDTO, String> colSupplierEmail;

    @FXML
    private TableColumn<SupplierDTO, String> colSupplierId;

    @FXML
    private TableColumn<SupplierDTO, String> colSupplierName;

    @FXML
    private TableColumn<SupplierDTO, Void> colSupplierUpdateAction;

    @FXML
    private TableColumn<SupplierDTO, String> colSypplyItem;

    @FXML
    private AnchorPane containerPane;

    @FXML
    private TableView<SupplierDTO> tblSupplier;

    @FXML
    private TextField txtSearchSupplier;

    @FXML
    private ComboBox<String> cmbAddSupplyItem;

    @FXML
    private TextField txtAddSupplierCompany;

    @FXML
    private TextField txtAddSupplierEmail;

    @FXML
    private TextField txtAddSupplierName;

    @FXML
    private TextField txtUpdateSupplierCompany;

    @FXML
    private TextField txtUpdateSupplierEmail;

    @FXML
    private TextField txtUpdateSupplierName;

    @FXML
    private ComboBox<String> cmbUpdateSupplyItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);

        // Populate combo boxes only if they exist in the currently loaded view
        if (cmbAddSupplyItem != null) {
            cmbAddSupplyItem.getItems().add("Gents");
            cmbAddSupplyItem.getItems().add("Ladies");
            cmbAddSupplyItem.getItems().add("Kids");
            cmbAddSupplyItem.getItems().add("Accessories");
            cmbAddSupplyItem.getItems().add("Footwear");
        }
        if (cmbUpdateSupplyItem != null) {
            cmbUpdateSupplyItem.getItems().addAll("Gents", "Ladies", "Kids", "Accessories", "Footwear");
        }

        if (tblSupplier != null) {
            // Keep a single observable list bound to the table for stable action cells
            supplierDTOObservableList = FXCollections.observableArrayList();
            tblSupplier.setItems(supplierDTOObservableList);

            if (colSupplierId != null) colSupplierId.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colSupplierName != null) colSupplierName.setCellValueFactory(new PropertyValueFactory<>("name"));
            if (colSupplierCompany != null) colSupplierCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
            if (colSupplierEmail != null) colSupplierEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            if (colSypplyItem != null) colSypplyItem.setCellValueFactory(new PropertyValueFactory<>("supplyItem"));


            if (colSupplierUpdateAction != null) {
                colSupplierUpdateAction.setStyle("-fx-alignment: CENTER;");
                colSupplierUpdateAction.setCellFactory(param -> new TableCell<>() {
                    private final Button updateButton = new Button("Update");
                    private final HBox container = new HBox(updateButton);

                    {
                        updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;");
                        updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #363b3e; -fx-text-fill: white;"));
                        updateButton.setOnMouseExited(e -> updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        updateButton.setOnAction(event -> {
                            SupplierDTO supplier = getTableRow() != null ? getTableRow().getItem() : null;
                            if (supplier != null) {
                                updateSupplier(supplier);
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

            if (colSupplierDeleteAction != null) {
                // Center align the whole column
                colSupplierDeleteAction.setStyle("-fx-alignment: CENTER;");
                colSupplierDeleteAction.setCellFactory(param -> new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");
                    private final HBox container = new HBox(deleteButton);

                    {
                        deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;");
                        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #4c0000; -fx-text-fill: white;"));
                        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;"));
                        container.setAlignment(Pos.CENTER);
                        container.setFillHeight(true);

                        deleteButton.setOnAction(event -> {
                            SupplierDTO supplier = getTableRow() != null ? getTableRow().getItem() : null;
                            if (supplier != null) {
                                deleteSupplier(supplier);
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
    void btnAddSupplierOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/add-supplier.fxml"))));
            stage.setTitle("Add Supplier");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnClearSearchOnAction(ActionEvent event) {
        if (txtSearchSupplier != null) {
            txtSearchSupplier.clear();
        }
        populateTable();
    }

    @FXML
    void btnSearchSupplierOnAction(ActionEvent event) {
        if (txtSearchSupplier == null || tblSupplier == null) {
            return;
        }

        String searchText = txtSearchSupplier.getText();
        if (searchText == null) searchText = "";
        List<SupplierDTO> suppliers = supplierService.getSuppliers();
        ObservableList<SupplierDTO> filteredList = FXCollections.observableArrayList();

        for (SupplierDTO supplier : suppliers) {
            if (supplier != null && supplier.getName() != null && supplier.getCompany() != null && supplier.getEmail() != null &&
                    (supplier.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            supplier.getCompany().contains(searchText.toLowerCase()) || supplier.getEmail().contains(searchText.toLowerCase()))) {
                filteredList.add(supplier);
            }
        }

        if (supplierDTOObservableList != null) {
            supplierDTOObservableList.setAll(filteredList);
            tblSupplier.refresh();
        }
    }

    @FXML
    void btnSaveSupplierOnAction(ActionEvent event) {
        if (txtAddSupplierName == null || txtAddSupplierCompany == null || txtAddSupplierEmail == null || cmbAddSupplyItem == null) {
            return;
        }

        String name = txtAddSupplierName.getText();
        String company = txtAddSupplierCompany.getText();
        String email = txtAddSupplierEmail.getText();
        String supplyItem = cmbAddSupplyItem.getValue();

        if (name == null || name.isBlank() || company == null || company.isBlank() || email == null || email.isBlank() || supplyItem == null || supplyItem.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fill all fields");
            alert.show();
            return;
        }

        SupplierDTO newSupplier = new SupplierDTO(0, name.trim(), company.trim(), email.trim(),supplyItem.trim());
        boolean isSaved = supplierService.addSupplier(newSupplier);

        if (isSaved) {
            Stage stage = (Stage) txtAddSupplierName.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Supplier Not Saved");
            alert.show();
        }
    }

    @FXML
    void btnUpdateSupplierOnAction(ActionEvent event) {
        if (supplierDTO != null) {
            supplierDTO.setName(txtUpdateSupplierName.getText());
            supplierDTO.setCompany(txtUpdateSupplierCompany.getText());
            supplierDTO.setEmail(txtUpdateSupplierEmail.getText());
            supplierDTO.setSupplyItem(cmbUpdateSupplyItem.getValue());

            boolean isSupplierUpdated = supplierService.updateSupplier(supplierDTO);

            if (isSupplierUpdated) {
                Stage stage = (Stage) txtUpdateSupplierName.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Error");
                alert.setHeaderText("Supplier Not Updated");
                alert.show();
            }
        }
    }

    private void updateSupplier(SupplierDTO supplierDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-supplier.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            SupplierController controller = loader.getController();
            if (controller != null) controller.setSupplier(supplierDTO);
            stage.setTitle("Update Supplier");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteSupplier(SupplierDTO supplierDTO) {
        if (supplierDTO == null) return;

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Supplier");
        deleteAlert.setHeaderText("Do you want to delete supplier: " + supplierDTO.getName() + "?");
        deleteAlert.setContentText("Click 'Delete' to confirm, or 'Cancel' to abort.");

        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete the supplier: " + supplierDTO.getName() + "?");
            confirmationAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                boolean isSupplierDeleted = supplierService.deleteSupplier(supplierDTO.getId());

                if (isSupplierDeleted) {
                    Platform.runLater(() -> populateTable());
                }
            }
        }
    }

    private void populateTable() {
        if (tblSupplier == null) return;
        if (supplierDTOObservableList != null) {
            supplierDTOObservableList.setAll(supplierService.getSuppliers());
            tblSupplier.refresh();
        }
    }


}
