package controller;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.jfoenix.controls.JFXButton;
import com.itextpdf.layout.element.Cell;
import dto.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.ServiceFactory;
import service.custom.CustomerService;
import service.custom.OrderService;
import service.custom.ProductService;
import util.ServiceType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardController implements Initializable {

    CustomerService customerService = ServiceFactory.getInstance().getServiceType(ServiceType.CUSTOMERS);
    ProductService productService = ServiceFactory.getInstance().getServiceType(ServiceType.PRODUCT);
    OrderService orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDERS);

    private List<ProductDTO> productList = new ArrayList<>();
    private List<ProductDTO> cartList = new ArrayList<>();

    private static UserDTO currentUser;

    @FXML
    private JFXButton btnPayBill;

    @FXML
    private ComboBox<String> cmbSelectCustomer;

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
        loadCustomersComboBox();
        productList = productService.getProducts();
        loadProductPanes(productList);
        loadOrderId();
    }

    @FXML
    void btnAccessoriesOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Accessories"));
    }

    @FXML
    void btnAddNewCustomerOnAction(ActionEvent event) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/view/add-customer.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Add Customer");

            stage.setOnHidden(e -> loadCustomersComboBox());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnAllProductsOnAction(ActionEvent event) {
        loadProductPanes(productList);
    }

    @FXML
    void btnFootwearOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Footwear"));
    }

    @FXML
    void btnGentsOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Gents"));
    }

    @FXML
    void btnKidsOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Kids"));
    }

    @FXML
    void btnLadiesOnAction(ActionEvent event) {
        loadProductPanes(sortProductsByCategory("Ladies"));
    }

    @FXML
    void btnPayBillOnAction(ActionEvent event) {
        if (cartList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order Error");
            alert.setHeaderText("Please select a product first");
            alert.show();
            return;
        }

        List<OrderDetailDTO> orderDetailsDTOList = new ArrayList<>();
        for (ProductDTO productDTO : cartList) {
            OrderDetailDTO orderDetails = new OrderDetailDTO(
                    1,
                    Integer.parseInt(lblOrderId.getText()),
                    productDTO.getId(),
                    productDTO.getQuantity()
            );
            orderDetailsDTOList.add(orderDetails);
        }

        OrderDTO order = new OrderDTO(
                Integer.parseInt(lblOrderId.getText()),
                convertDateFormat(lblDate.getText()),
                Double.parseDouble(txtTotalAmount.getText()),
                "Cash",
                currentUser.getId(),
                cmbSelectCustomer.getSelectionModel().getSelectedItem() != null
                        ? getCustomerIdByComboBox(cmbSelectCustomer.getSelectionModel().getSelectedItem().toString())
                        : 0,
                orderDetailsDTOList
        );

        boolean isOrderPlace = orderService.addOrder(order);

        if (isOrderPlace) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Placed Successfully");
            alert.setHeaderText("Order #" + lblOrderId.getText() + " has been placed successfully");
            alert.show();
            
            loadProductPanes(productService.getProducts());
            loadOrderId();
            generateBillPdf();
            cmbSelectCustomer.getSelectionModel().clearSelection();
            cartList.clear();
            loadCartPane();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order Not Placed");
            alert.setHeaderText("Order Not Placed");
            alert.setContentText("Failed to place order. Please check the console for error details.");
            alert.show();
            System.err.println("Failed to place order with ID: " + lblOrderId.getText());
        }
    }

    @FXML
    void btnSearchProductOnAction(ActionEvent event) {
        String searchText = txtSearchProductText.getText().trim().toLowerCase();
        List<ProductDTO> filteredList = new ArrayList<>();

        if (!searchText.isEmpty()) {
            for (ProductDTO product : productList) {
                if (product.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(product);
                }
            }
            loadProductPanes(filteredList);
        } else {
            loadProductPanes(productList);
        }
    }

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    private void loadCustomersComboBox() {
        List<CustomerDTO> customerDTOList = customerService.getCustomers();
        ObservableList<String> customerObservableList = FXCollections.observableArrayList();

        for (CustomerDTO customer : customerDTOList) {
            customerObservableList.add(customer.getId() + " - " + customer.getName() + " - " + customer.getMobile());
        }
        cmbSelectCustomer.setItems(customerObservableList);
    }

    private void loadProductPanes(List<ProductDTO> products) {
        flowPaneProducts.getChildren().clear();
        flowPaneProducts.setHgap(15);
        flowPaneProducts.setVgap(15);
        flowPaneProducts.setPrefWrapLength(950);

        for (ProductDTO productDTO : products) {
            VBox productCard = createProductCard(productDTO);
            flowPaneProducts.getChildren().add(productCard);
        }
    }

    private void loadOrderId() {
        int lastOrderId = orderService.getLastId();
        lblOrderId.setText(String.valueOf(lastOrderId + 1));
    }

    private VBox createProductCard(ProductDTO productDTO) {
        VBox productCard = new VBox(5);
        productCard.setStyle("-fx-padding: 12; -fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(97,97,97,0.2), 15, 0, 0, 0);");
        productCard.setPrefWidth(177);
        productCard.setPrefHeight(265);
        productCard.setAlignment(Pos.CENTER);

        ImageView productImage = new ImageView();
        File imageFile = new File(productDTO.getImage());
        if (imageFile.exists()) {
            productImage.setImage(new Image(imageFile.toURI().toString()));
        }
        productImage.setFitWidth(177);
        productImage.setFitHeight(130);

        Rectangle clip = new Rectangle(177, 130);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        productImage.setClip(clip);

        Label lblProductName = new Label(productDTO.getName());
        lblProductName.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label lblCategory = new Label("Category: " + productDTO.getCategory());
        Label lblQuantity = new Label("Stock: " + productDTO.getQuantity());

        Label lblPrice = new Label("LKR " + productDTO.getPrice());
        lblPrice.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox textContainer = new VBox(3, lblProductName, lblCategory, lblQuantity);
        textContainer.setAlignment(Pos.CENTER);

        setProductCardClickAction(productCard, productDTO);

        productCard.getChildren().addAll(productImage, textContainer, lblPrice);

        return productCard;
    }

    private void setProductCardClickAction(VBox productCard, ProductDTO productDTO) {
        productCard.setOnMouseClicked(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter Quantity");
            dialog.setHeaderText("Enter the quantity for " + productDTO.getName());
            dialog.setContentText("Quantity:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    int quantity = Integer.parseInt(input);
                    boolean isProductAvailable = productService.getProductById(productDTO.getId()).getQuantity() > quantity;

                    boolean found = false;
                    for (ProductDTO listProduct : cartList) {
                        if (productDTO.getName().equals(listProduct.getName())) {
                            listProduct.setQuantity(listProduct.getQuantity() + quantity);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        if (isProductAvailable) {
                            cartList.add(new ProductDTO(productDTO.getId(),
                                    productDTO.getName(),
                                    productDTO.getCategory(),
                                    productDTO.getSize(),
                                    productDTO.getPrice(),
                                    quantity,
                                    productDTO.getImage(),
                                    productDTO.getSupplierID()
                            ));
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Stock Error");
                            alert.setHeaderText("Stock not enough to add product");
                            alert.show();
                        }
                    }
                    loadCartPane();
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Invalid quantity! Please enter a valid number.").show();
                }
            });
        });
    }


    private void loadCartPane() {
        flowPaneCart.getChildren().clear();
        double totalAmount = 0;

        VBox cartContainer = new VBox(8);
        cartContainer.setPadding(new Insets(5, 5, 5, 5));

        for (ProductDTO productDTO : cartList) {
            HBox cartItem = new HBox(6);
            cartItem.setStyle("-fx-padding: 8; -fx-background-color: #f8f9fa; "
                    + "-fx-border-width: 1; -fx-border-color: #ccc; "
                    + "-fx-background-radius: 5; -fx-border-radius: 5;");
            cartItem.setAlignment(Pos.CENTER_LEFT);
            cartItem.setPrefWidth(310);
            cartItem.setPadding(new Insets(3, 5, 3, 5));

            Label lblProductName = new Label(productDTO.getName());
            lblProductName.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
            lblProductName.setPrefWidth(70);
            lblProductName.setWrapText(true);

            Label lblPrice = new Label("LKR " + String.format("%.0f", productDTO.getPrice()));
            lblPrice.setStyle("-fx-font-size: 11; -fx-text-fill: #333;");
            lblPrice.setPrefWidth(50);

            TextField txtQuantity = new TextField(String.valueOf(productDTO.getQuantity()));
            txtQuantity.setPrefWidth(40);
            txtQuantity.setAlignment(Pos.CENTER);
            txtQuantity.setStyle("-fx-font-size: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label lblTotal = new Label("LKR " + String.format("%.0f", productDTO.getPrice() * productDTO.getQuantity()));
            lblTotal.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
            lblTotal.setPrefWidth(60);

            txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    int newQuantity = Integer.parseInt(newValue.trim());
                    if (newQuantity > 0) {
                        productDTO.setQuantity(newQuantity);
                        lblTotal.setText("LKR " + String.format("%.0f", productDTO.getPrice() * newQuantity));
                        updateTotalAmount();
                    } else {
                        txtQuantity.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    txtQuantity.setText(oldValue);
                }
            });

            Image closeImage = new Image(getClass().getResourceAsStream("/images/closeIcon.png"));
            ImageView closeImageView = new ImageView(closeImage);
            closeImageView.setFitWidth(14);
            closeImageView.setFitHeight(14);

            Button btnRemove = new Button();
            btnRemove.setGraphic(closeImageView);
            btnRemove.setStyle("-fx-background-color: transparent; -fx-padding: 4;");
            btnRemove.setTooltip(new Tooltip("Remove"));
            btnRemove.setMinWidth(24);
            btnRemove.setOnAction(event -> {
                cartList.remove(productDTO);
                loadCartPane();
            });

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            cartItem.getChildren().addAll(lblProductName, lblPrice, txtQuantity, lblTotal, btnRemove);
            cartContainer.getChildren().add(cartItem);

            totalAmount += productDTO.getPrice() * productDTO.getQuantity();
        }

        txtTotalAmount.setText(String.valueOf(totalAmount));
        flowPaneCart.getChildren().setAll(cartContainer);
    }

    private void updateTotalAmount() {
        double newTotal = cartList.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        txtTotalAmount.setText(String.valueOf(newTotal));
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

    public static Date convertDateFormat(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(inputDate, inputFormatter);
        return Date.valueOf(localDate);
    }

    public static Integer getCustomerIdByComboBox(String selectedValue) {
        if (selectedValue == null || selectedValue.isEmpty()) {
            return -1;
        }
        String pattern = "^(\\d+)\\s*-";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(selectedValue);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    public void generateBillPdf() {
        try {
            String filePath = Paths.get(System.getProperty("user.home"), "Downloads", "Clothify_PDF", "Clothify_Invoice_" + lblOrderId.getText() + ".pdf").toString();
            Files.createDirectories(Paths.get(System.getProperty("user.home"), "Downloads", "Clothify_PDF"));

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A5);
            document.setMargins(36, 36, 36, 36);

            document.add(new Paragraph("Clothify Store").setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("INVOICE").setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

            Table dateTimeTable = new Table(new float[]{1, 1});
            dateTimeTable.setWidth(UnitValue.createPercentValue(100));
            dateTimeTable.addCell(new Cell().add(new Paragraph("Date: " + lblDate.getText()).setFontSize(10)).setBorder(Border.NO_BORDER));
            dateTimeTable.addCell(new Cell().add(new Paragraph("Time: " + lblTime.getText()).setFontSize(10).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
            document.add(dateTimeTable);

            document.add(new Paragraph("Customer: " + getCustomerNameByComboBox()).setFontSize(10).setMarginBottom(10));

            float[] columnWidths = {2, 6, 3, 3, 4};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("#").setFontSize(10)));
            table.addHeaderCell(new Cell().add(new Paragraph("Item").setFontSize(10)));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setFontSize(10)));
            table.addHeaderCell(new Cell().add(new Paragraph("Qty").setFontSize(10)));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount").setFontSize(10)));

            int count = 1;
            double totalAmount = 0.0;
            for (ProductDTO productDTO : cartList) {
                double amount = productDTO.getPrice() * productDTO.getQuantity();
                totalAmount += amount;

                table.addCell(new Cell().add(new Paragraph(String.valueOf(count)).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(productDTO.getName()).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", productDTO.getPrice())).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(productDTO.getQuantity())).setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", amount)).setFontSize(10)));
                count++;
            }
            document.add(table.setMarginBottom(10));

            Table summaryTable = new Table(new float[]{3, 2});
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Amount:").setFontSize(10)).setBorder(Border.NO_BORDER));
            summaryTable.addCell(new Cell().add(new Paragraph("LKR " + String.format("%.2f", totalAmount)).setFontSize(10)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            summaryTable.addCell(new Cell().add(new Paragraph("Final Amount:").setFontSize(12)).setBorder(Border.NO_BORDER));
            summaryTable.addCell(new Cell().add(new Paragraph("LKR " + String.format("%.2f", totalAmount)).setFontSize(12)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            document.add(summaryTable.setMarginBottom(10));

            document.add(new Paragraph("Thank you for your purchase!").setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.close();

            File pdfFile = new File(filePath);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCustomerNameByComboBox() {
        String selectedValue = cmbSelectCustomer.getValue();
        if (selectedValue == null || selectedValue.isEmpty()) {
            return "N/A";
        }
        String pattern = "\\d+\\s*-\\s*([A-Za-z\\s]+)\\s*-";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(selectedValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "N/A";
    }

    private List<ProductDTO> sortProductsByCategory(String category) {
        List<ProductDTO> sortedList = new ArrayList<>();
        for (ProductDTO productDTO : productList) {
            if (productDTO.getCategory().equals(category)) {
                sortedList.add(productDTO);
            }
        }
        return sortedList;
    }
}
