package controller;

import com.jfoenix.controls.JFXRadioButton;
import dto.ProductDTO;
import dto.SupplierDTO;
import entity.Product;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.modelmapper.ModelMapper;
import service.ServiceFactory;
import service.custom.ProductService;
import service.custom.SupplierService;
import util.ServiceType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    ProductService productService;
    SupplierService supplierService;
    ObservableList<ProductDTO> productDTOObservableList;
    private List<ProductDTO> productDTOList;
    private ModelMapper modelMapper;

    private ProductDTO productDTO;

    public void setProduct(ProductDTO productDTO) {
        this.productDTO = productDTO;
        // Ensure combo items are loaded BEFORE setting selected values
        loadProductCategoryComboBox();
        loadProductSizeComboBox();
        loadProductSupplierComboBox();
        // Now set current values so selection is retained
        loadProductDetails();
    }

    @FXML
    private FlowPane flowPaneProductsManagement;

    @FXML
    private AnchorPane paneProductManagement;

    @FXML
    private TextField txtSearchProduct;

    @FXML
    public TableView<Product> tableProducts;

    @FXML
    public AnchorPane paneListView;

    @FXML
    public ScrollPane paneCardView;

    @FXML
    public JFXRadioButton radioListView;

    @FXML
    public JFXRadioButton radioCardView;

    @FXML
    public TableColumn<Product, String> colId;

    @FXML
    public TableColumn<Product, String> colName;

    @FXML
    public TableColumn<Product, String> colImage;

    @FXML
    public TableColumn<Product, String> colCategory;

    @FXML
    public TableColumn<Product, String> colSize;

    @FXML
    public TableColumn<Product, Double> colPrice;

    @FXML
    public TableColumn<Product, Integer> colQty;

    @FXML
    public TableColumn<Product, String> colSupplierId;

    @FXML
    public TableColumn<Product, Void> colUpdateAction;

    @FXML
    public TableColumn<Product, Void> colDeleteAction;

    @FXML
    private ComboBox cmbUpdateProcutSupplierId;

    @FXML
    private ComboBox cmbUpdateProductCategory;

    @FXML
    private ComboBox cmbUpdateProductSize;

    @FXML
    private TextField txtUpdateProductImagePath;

    @FXML
    private TextField txtUpdateProductName;

    @FXML
    private TextField txtUpdateProductPrice;

    @FXML
    private TextField txtUpdateProductQuantityOnHand;

    @FXML
    private ComboBox<?> cmbAddProcutSupplierId;

    @FXML
    private ComboBox<?> cmbAddProductCategory;

    @FXML
    private ComboBox<?> cmbAddProductSize;

    @FXML
    private TextField txtAddProductImagePath;

    @FXML
    private TextField txtAddProductName;

    @FXML
    private TextField txtAddProductPrice;

    @FXML
    private TextField txtAddProductQuantityOnHand;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize services and mapper first
        productService = ServiceFactory.getInstance().getServiceType(ServiceType.PRODUCT);
        supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);
        modelMapper = new ModelMapper();

        // Load combo box data only if those controls are present in the current view
        loadProductCategoryComboBox();
        loadProductSizeComboBox();
        loadProductSupplierComboBox();

        // Only initialize main Product Management view controls if present
        if (paneProductManagement != null) {
            if (radioListView != null && radioCardView != null) {
                ToggleGroup viewToggleGroup = new ToggleGroup();
                radioListView.setToggleGroup(viewToggleGroup);
                radioCardView.setToggleGroup(viewToggleGroup);
            }

            if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colCategory != null) colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            if (colImage != null) colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
            if (colName != null) colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            if (colPrice != null) colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            if (colQty != null) colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            if (colSize != null) colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
            if (colSupplierId != null) colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierID"));

            if (colImage != null) {
                colImage.setCellFactory(column -> new TableCell<>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(String imageUrl, boolean empty) {
                        super.updateItem(imageUrl, empty);

                        if (empty || imageUrl == null || imageUrl.isEmpty()) {
                            setGraphic(null);
                        } else {
                            try {
                                Image image = imageUrl.startsWith("file:") || imageUrl.startsWith("http")
                                        ? new Image(imageUrl)
                                        : new Image(new File(imageUrl).toURI().toString());
                                imageView.setImage(image);
                                imageView.setFitWidth(80);
                                imageView.setFitHeight(60);
                                setGraphic(imageView);
                            } catch (Exception e) {
                                System.err.println("Failed to load image: " + imageUrl);
                                setGraphic(null);
                            }
                        }
                    }
                });
            }

            if (colUpdateAction != null) {
                colUpdateAction.setCellFactory(column -> new TableCell<>() {
                    private final Button updateButton = new Button("Update");

                    {
                        updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;");
                        updateButton.setOnMouseEntered(e -> updateButton.setStyle("-fx-background-color: #363b3e; -fx-text-fill: white;"));
                        updateButton.setOnMouseExited(e -> updateButton.setStyle("-fx-background-color: #495057; -fx-text-fill: white;"));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(updateButton);
                            updateButton.setOnAction(event -> {
                                Product product = getTableView().getItems().get(getIndex());
                                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                                updateProduct(productDTO);
                            });
                        }
                    }
                });
            }

            if (colDeleteAction != null) {
                colDeleteAction.setCellFactory(column -> new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;");
                        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #4c0000; -fx-text-fill: white;"));
                        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;"));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);

                            deleteButton.setOnAction(event -> {
                                Product product = getTableView().getItems().get(getIndex());
                                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                                deleteProduct(productDTO);
                            });
                        }
                    }
                });
            }

            productDTOList = productService.getProducts();
            if (flowPaneProductsManagement != null) {
                loadProductPanes(productDTOList);
            }
            populateTable(productDTOList);

            if (radioCardView != null) {
                radioCardView.setSelected(true);
            }
        }
    }

    @FXML
    void btnAddProductOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-product.fxml"));
            Parent root = loader.load();

            ProductController controller = loader.getController();
            controller.loadAddProductComboBoxes();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Product");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> populateTable(productDTOList)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSortAccessoriesOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Accessories"));
        populateTable(sortProductsByCategory("Accessories"));
    }

    @FXML
    void btnSortAllProductsOnAction(ActionEvent event) {
        loadProductPanes(productDTOList);
        populateTable(productDTOList);
    }

    @FXML
    void btnSortFootwareOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Footwear"));
        populateTable(sortProductsByCategory("Footwear"));
    }

    @FXML
    void btnSortGentsOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Gents"));
        populateTable(sortProductsByCategory("Gents"));
    }

    @FXML
    void btnSortKidsOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Kids"));
        populateTable(sortProductsByCategory("Kids"));
    }

    @FXML
    void btnSortLadiesOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Ladies"));
        populateTable(sortProductsByCategory("Ladies"));
    }

    @FXML
    void radioCardViewOnAction(ActionEvent event) {
        paneCardView.toFront();
    }

    @FXML
    void radioListViewOnAction(ActionEvent event) {
        tableProducts.toFront();
    }

    @FXML
    void btnAddProductImageOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File initialDirectory = new File("src\\main\\resources\\images\\products");
        fileChooser.setInitialDirectory(initialDirectory);

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Convert to a path relative to the project root (e.g., "src\\main\\resources\\images\\products\\file.png")
            String relativePath = selectedFile.getName();
            try {
                java.nio.file.Path projectPath = new java.io.File("").getAbsoluteFile().toPath();
                java.nio.file.Path filePath = selectedFile.getAbsoluteFile().toPath();
                java.nio.file.Path rel = projectPath.relativize(filePath);
                relativePath = rel.toString();
            } catch (Exception ignored) {
                // Fallback to file name if relativize fails
            }

            // Support both Add and Update forms. Only set the field(s) present in the current view.
            if (txtAddProductImagePath != null) {
                txtAddProductImagePath.setText(relativePath);
            }
            if (txtUpdateProductImagePath != null) {
                txtUpdateProductImagePath.setText(relativePath);
            }
        }
    }


    @FXML
    void btnSaveProductOnAction(ActionEvent event) {
        if (txtAddProductImagePath.getText().trim().isEmpty() ||
                txtAddProductName.getText().trim().isEmpty() ||
                txtAddProductPrice.getText().trim().isEmpty() ||
                txtAddProductQuantityOnHand.getText().trim().isEmpty() ||
                cmbAddProductCategory.getValue() == null ||
                cmbAddProductSize.getValue() == null ||
                cmbAddProcutSupplierId.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Fields Can't be Empty");
            alert.show();
            return;
        }
        boolean isProductAdded = productService.addProduct(new ProductDTO(
                1,
                txtAddProductName.getText(),
                cmbAddProductCategory.getSelectionModel().getSelectedItem().toString(),
                cmbAddProductSize.getSelectionModel().getSelectedItem().toString(),
                Double.parseDouble(txtAddProductPrice.getText()),
                Integer.parseInt(txtAddProductQuantityOnHand.getText()),
                txtAddProductImagePath.getText(),
                Integer.parseInt(cmbAddProcutSupplierId.getSelectionModel().getSelectedItem().toString().split(" - ")[0])
        ));

        if (isProductAdded) {
            Stage stage = (Stage) txtAddProductImagePath.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Product Adding Failed");
            alert.setHeaderText("Product Adding Failed");
            alert.show();
        }
    }


    @FXML
    void btnSearchProductOnAction(ActionEvent event) {
        String searchText = txtSearchProduct.getText().trim().toLowerCase();
        List<ProductDTO> filteredList = new ArrayList<>();

        if (!searchText.isEmpty()) {
            for (ProductDTO product : productDTOList) {
                if (product.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(product);
                }
            }
        } else {
            filteredList = productDTOList;
        }
        loadProductPanes(filteredList);
        populateTable(filteredList);
    }

    private void updateProduct(ProductDTO productDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-product.fxml"));
            Parent root = loader.load();

            ProductController controller = loader.getController();
            controller.setProduct(productDTO);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Product");
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(e -> Platform.runLater(() -> {
                        productDTOList.clear();
                        productDTOList.addAll(productService.getProducts());
                        loadProductPanes(productDTOList);
                        populateTable(productDTOList);
                    })
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateProductOnAction(ActionEvent event) {
        // Ensure update form controls are present
        if (txtUpdateProductName == null ||
                cmbUpdateProductCategory == null ||
                cmbUpdateProductSize == null ||
                txtUpdateProductPrice == null ||
                txtUpdateProductQuantityOnHand == null ||
                txtUpdateProductImagePath == null ||
                cmbUpdateProcutSupplierId == null) {
            return;
        }

        // Basic validation
        String name = txtUpdateProductName.getText();
        Object categoryVal = cmbUpdateProductCategory.getValue();
        Object sizeVal = cmbUpdateProductSize.getValue();
        String priceText = txtUpdateProductPrice.getText();
        String qtyText = txtUpdateProductQuantityOnHand.getText();
        String imagePath = txtUpdateProductImagePath.getText();
        Object supplierVal = cmbUpdateProcutSupplierId.getValue();

        if (name == null || name.isBlank() || categoryVal == null || sizeVal == null ||
                priceText == null || priceText.isBlank() || qtyText == null || qtyText.isBlank() ||
                imagePath == null || imagePath.isBlank() || supplierVal == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please fill all fields");
            alert.show();
            return;
        }

        try {
            double price = Double.parseDouble(priceText.trim());
            int quantity = Integer.parseInt(qtyText.trim());

            // Supplier combo value formatted as: "<id> - <name> - <company>"
            int supplierId;
            String supplierStr = supplierVal.toString();
            if (supplierStr.contains(" - ")) {
                supplierId = Integer.parseInt(supplierStr.split(" - ")[0].trim());
            } else {
                supplierId = Integer.parseInt(supplierStr.trim());
            }

            // Preserve current product id from previously set DTO if available
            int id = (productDTO != null) ? productDTO.getId() : -1;

            ProductDTO updated = new ProductDTO(
                    id,
                    name.trim(),
                    categoryVal.toString(),
                    sizeVal.toString(),
                    price,
                    quantity,
                    imagePath.trim(),
                    supplierId
            );

            boolean isUpdated = productService.updateProduct(updated);
            if (isUpdated) {
                // Close the update dialog window
                Node source = (event != null) ? (Node) event.getSource() : null;
                if (source != null && source.getScene() != null) {
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }

                // Refresh cached list and UI immediately as a fallback
                Platform.runLater(() -> {
                    if (productDTOList != null) {
                        productDTOList.clear();
                        productDTOList.addAll(productService.getProducts());
                        loadProductPanes(productDTOList);
                        populateTable(productDTOList);
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Error");
                alert.setHeaderText("Product Not Updated");
                alert.show();
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Price and Quantity must be numeric");
            alert.show();
        }
    }

    @FXML
    void btnAddSupplierOnAction(ActionEvent event) {
        // Opens the Add Supplier dialog from the update-product view
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/add-supplier.fxml"))));
            stage.setTitle("Add Supplier");
            stage.setResizable(false);
            stage.show();

            // After closing, reload suppliers into the combo box
            stage.setOnHidden(e -> Platform.runLater(this::loadProductSupplierComboBox));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct(ProductDTO productDTO) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Product");
        deleteAlert.setHeaderText("Do you want to delete product: " + productDTO.getName() + "?");
        deleteAlert.setContentText("Click 'Ok' to confirm, or 'Cancel' to abort.");

        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete the product: " + productDTO.getName() + "?");
            confirmationAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {

                boolean isProductDeleted = productService.deleteProduct(productDTO.getId());

                if (isProductDeleted) {
                    Platform.runLater(() -> {
                        productDTOList.clear();
                        productDTOList.addAll(productService.getProducts());
                        loadProductPanes(productDTOList);
                        populateTable(productDTOList);
                    });
                }
            }
        }
    }

    private void loadProductDetails() {
        if (productDTO != null) {
            txtUpdateProductName.setText(productDTO.getName());
            if (cmbUpdateProductCategory != null) {
                cmbUpdateProductCategory.setValue(productDTO.getCategory());
            }
            if (cmbUpdateProductSize != null) {
                cmbUpdateProductSize.setValue(productDTO.getSize());
            }
            txtUpdateProductPrice.setText(productDTO.getPrice().toString());
            txtUpdateProductQuantityOnHand.setText(productDTO.getQuantity().toString());
            txtUpdateProductImagePath.setText(productDTO.getImage());
            // Supplier combo items are formatted as: "<id> - <name> - <company>"
            if (cmbUpdateProcutSupplierId != null && cmbUpdateProcutSupplierId.getItems() != null) {
                String prefix = String.valueOf(productDTO.getSupplierID()) + " - ";
                Object match = null;
                for (Object item : cmbUpdateProcutSupplierId.getItems()) {
                    if (item != null && item.toString().startsWith(prefix)) {
                        match = item;
                        break;
                    }
                }
                // Fallback to raw id if no formatted entry present yet
                cmbUpdateProcutSupplierId.setValue(match != null ? match : productDTO.getSupplierID());
            }
        }
    }

    private void loadProductSupplierComboBox() {
        if (cmbUpdateProcutSupplierId == null || supplierService == null) return;
        List<SupplierDTO> supplierDTOList = supplierService.getSuppliers();
        if (supplierDTOList == null) return;
        ObservableList<String> supplierObservableList = FXCollections.observableArrayList();
        for (SupplierDTO supplier : supplierDTOList) {
            if (supplier != null) {
                supplierObservableList.add(supplier.getId() + " - " + supplier.getName() + " - " + supplier.getCompany());
            }
        }
        cmbUpdateProcutSupplierId.setItems(supplierObservableList);
    }

    private void loadProductCategoryComboBox() {
        if (cmbUpdateProductCategory == null) return;
        List<String> productCategories = List.of("Gents", "Ladies", "Kids", "Accessories", "Footwear", "Other");
        cmbUpdateProductCategory.setItems(FXCollections.observableArrayList(productCategories));
        // If a product is already selected, re-apply its category
        if (productDTO != null && productDTO.getCategory() != null) {
            if (productCategories.contains(productDTO.getCategory())) {
                cmbUpdateProductCategory.setValue(productDTO.getCategory());
            }
        }
    }

    private void loadProductSizeComboBox() {
        if (cmbUpdateProductSize == null) return;
        List<String> productSizes = List.of("XXS", "XS", "Small", "Medium", "Large", "XL", "XXL", "XXXL");
        cmbUpdateProductSize.setItems(FXCollections.observableArrayList(productSizes));
        // If a product is already selected, re-apply its size
        if (productDTO != null && productDTO.getSize() != null) {
            if (productSizes.contains(productDTO.getSize())) {
                cmbUpdateProductSize.setValue(productDTO.getSize());
            }
        }
    }

    private void loadAddProductComboBoxes() {
        // Load categories for Add form
        if (cmbAddProductCategory != null) {
            List<String> productCategories = List.of("Gents", "Ladies", "Kids", "Accessories", "Footwear", "Other");
            ((ComboBox<String>) cmbAddProductCategory).setItems(FXCollections.observableArrayList(productCategories));
        }

        // Load sizes for Add form
        if (cmbAddProductSize != null) {
            List<String> productSizes = List.of("XXS", "XS", "Small", "Medium", "Large", "XL", "XXL", "XXXL");
            ((ComboBox<String>) cmbAddProductSize).setItems(FXCollections.observableArrayList(productSizes));
        }

        // Load suppliers for Add form
        if (cmbAddProcutSupplierId != null && supplierService != null) {
            List<SupplierDTO> supplierDTOList = supplierService.getSuppliers();
            if (supplierDTOList != null) {
                ObservableList<String> supplierObservableList = FXCollections.observableArrayList();
                for (SupplierDTO supplier : supplierDTOList) {
                    if (supplier != null) {
                        supplierObservableList.add(supplier.getId() + " - " + supplier.getName() + " - " + supplier.getCompany());
                    }
                }
                ((ComboBox<String>) cmbAddProcutSupplierId).setItems(supplierObservableList);
            }
        }
    }

    private void loadProductPanes(List<ProductDTO> products) {
        if (products == null) {
            return;
        }
        flowPaneProductsManagement.getChildren().clear();
        flowPaneProductsManagement.setHgap(15);
        flowPaneProductsManagement.setVgap(15);
        flowPaneProductsManagement.setPrefWrapLength(1020);

        for (ProductDTO product : products) {
            VBox productCard = createProductCard(product);
            flowPaneProductsManagement.getChildren().add(productCard);
        }
    }

    private List<ProductDTO> sortProductsByCategory(String category) {
        List<ProductDTO> sortedList = new ArrayList<>();
        for (ProductDTO product : productDTOList) {
            if (product.getCategory().equals(category)) {
                sortedList.add(product);
            }
        }
        return sortedList;
    }

    private VBox createProductCard(ProductDTO productDTO) {
        VBox productCard = new VBox(5);
        productCard.setStyle("-fx-padding: 12; -fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(97,97,97,0.2), 15, 0, 0, 0);");
        productCard.setPrefWidth(215);
        productCard.setPrefHeight(300);
        productCard.setAlignment(Pos.CENTER);

        ImageView productImage = new ImageView();
        File imageFile = new File(productDTO.getImage());
        if (imageFile.exists()) {
            productImage.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.out.println("Image file not found: " + productDTO.getImage());
        }

        productImage.setFitWidth(215);
        productImage.setFitHeight(130);

        Rectangle clip = new Rectangle(215, 130);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        productImage.setClip(clip);

        Label lblProductId = new Label("ID: " + productDTO.getId());
        lblProductId.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");

        Label lblProductName = new Label(productDTO.getName());
        lblProductName.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label lblCategory = new Label("Category: " + productDTO.getCategory());
        Label lblQuantity = new Label("Stock: " + productDTO.getQuantity());

        Label lblPrice = new Label("LKR " + productDTO.getPrice());
        lblPrice.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox textContainer = new VBox(3, lblProductId, lblProductName, lblCategory, lblQuantity);
        textContainer.setAlignment(Pos.CENTER);

        Button btnUpdate = new Button("Update");
        btnUpdate.setStyle("-fx-background-color: #495057; -fx-text-fill: white;");
        btnUpdate.setOnMouseEntered(e -> btnUpdate.setStyle("-fx-background-color: #363b3e; -fx-text-fill: white;"));
        btnUpdate.setOnMouseExited(e -> btnUpdate.setStyle("-fx-background-color: #495057; -fx-text-fill: white;"));
        btnUpdate.setOnAction(e -> updateProduct(productDTO));

        Button btnDelete = new Button("Delete");
        btnDelete.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;");
        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #4c0000; -fx-text-fill: white;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #6e0000; -fx-text-fill: white;"));
        btnDelete.setOnAction(e -> deleteProduct(productDTO));

        HBox buttonBox = new HBox(10, btnUpdate, btnDelete);
        buttonBox.setAlignment(Pos.CENTER);

        productCard.getChildren().addAll(productImage, textContainer, lblPrice, buttonBox);

        return productCard;
    }


    private void populateTable(List<ProductDTO> products) {
        if (tableProducts == null) return;
        tableProducts.getItems().clear();

        if (products == null || products.isEmpty()) return;

        // The TableView is typed to `Product`, so map incoming DTOs to `Product` entities for display
        ObservableList<Product> rows = FXCollections.observableArrayList();
        for (ProductDTO dto : products) {
            if (dto != null) {
                Product product = modelMapper.map(dto, Product.class);
                rows.add(product);
            }
        }
        tableProducts.setItems(rows);
        tableProducts.refresh();
    }
}
