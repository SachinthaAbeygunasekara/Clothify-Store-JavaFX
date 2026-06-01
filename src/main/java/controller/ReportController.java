package controller;

import db.DBConnection;
import dto.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Rectangle;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import net.sf.jasperreports.view.JasperViewer;
import service.ServiceFactory;
import service.custom.*;
import util.ServiceType;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ReportController implements Initializable {

    @FXML
    public ComboBox cmbSaleSortTime;

    @FXML
    public BarChart<String, Number> chartCustomer;

    @FXML
    public AreaChart<String, Number> chartSales;

    @FXML
    public LineChart chartSupplier;

    @FXML
    public Rectangle rect1;

    @FXML
    public Rectangle rect2;

    @FXML
    public Rectangle rect3;

    @FXML
    public ComboBox cmbProductCategories;

    private CustomerService customerService;
    private OrderService orderService;
    private ProductService productService;
    private OrderDetailService orderDetailService;
    private SupplierService supplierService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbProductCategories.getItems().addAll("Gents", "Ladies", "Kids", "Accessories", "Footwear");
        cmbProductCategories.setValue("Gents");
        cmbSaleSortTime.getItems().addAll("All Time", "Last Month", "Last Week", "Last Day");
        cmbSaleSortTime.setValue("All Time");

        customerService = ServiceFactory.getInstance().getServiceType(ServiceType.CUSTOMERS);
        orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDERS);
        productService = ServiceFactory.getInstance().getServiceType(ServiceType.PRODUCT);
        orderDetailService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDERPRODUCT);
        supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);

        loadCustomerChart();
        loadAllSalesChart();
        loadSupplierChart("Gents");
    }

    @FXML
    void btnCustomerReportsOnAction(ActionEvent event) {
        generateReport("Customers.jrxml");
    }

    @FXML
    void btnSalesReportsOnAction(ActionEvent event) {
        String selectedOption = cmbSaleSortTime.getSelectionModel().getSelectedItem().toString();

        switch (selectedOption) {
            case "All Time":
                generateReport("AllTimeOrders.jrxml");
                break;
            case "Last Month":
                generateReport("LastMonthOrders.jrxml");
                break;
            case "Last Week":
                generateReport("LastWeekOrders.jrxml");
                break;
            case "Last Day":
                generateReport("LastDayOrders.jrxml");
                break;
            default:
                generateReport("AllTimeOrders.jrxml");
                break;
        }
    }

    @FXML
    void btnSupplierReportsOnAction(ActionEvent event) {
        generateReport("Suppliers.jrxml");
    }

    @FXML
    void cmbSalesOnAction(ActionEvent event) {
        String selectedOption = cmbSaleSortTime.getSelectionModel().getSelectedItem().toString();
        switch (selectedOption) {
            case "All Time":
                loadAllSalesChart();
                break;
            case "Last Month":
                loadSalesChart(LocalDate.now().minusMonths(1), null);
                break;
            case "Last Week":
                loadSalesChart(LocalDate.now().minusWeeks(1), null);
                break;
            case "Last Day":
                loadSalesChart(LocalDate.now().minusDays(1), null);
                break;
        }
    }

    public void cmbProductCategoriesOnAction(ActionEvent actionEvent) {
        if (cmbProductCategories.getSelectionModel().getSelectedItem().equals("Gents")) {
            loadSupplierChart("Gents");
        }
        if (cmbProductCategories.getSelectionModel().getSelectedItem().equals("Ladies")) {
            loadSupplierChart("Ladies");
        }
        if (cmbProductCategories.getSelectionModel().getSelectedItem().equals("Kids")) {
            loadSupplierChart("Kids");
        }
        if (cmbProductCategories.getSelectionModel().getSelectedItem().equals("Accessories")) {
            loadSupplierChart("Accessories");
        }
        if (cmbProductCategories.getSelectionModel().getSelectedItem().equals("Footwear")) {
            loadSupplierChart("Footwear");
        }
    }

    private void loadSupplierChart(String category) {
        List<SupplierDTO> supplierDTOList = supplierService.getSuppliers();
        List<ProductDTO> productDTOtList = productService.getProducts();

        Map<String, Integer> supplierProductCount = new HashMap<>();

        for (SupplierDTO supplierDTO : supplierDTOList) {
            int count = 0;

            for (ProductDTO productDTO : productDTOtList) {
                if (supplierDTO.getId() == productDTO.getSupplierID() &&
                        category.equalsIgnoreCase(productDTO.getCategory())) {
                    count++;
                }
            }
            supplierProductCount.put(supplierDTO.getCompany(), count);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(category + " Clothes Count");

        for (Map.Entry<String, Integer> entry : supplierProductCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartSupplier.getData().clear();
        chartSupplier.getData().add(series);
    }

    private void loadCustomerChart() {
        List<CustomerDTO> customerDTOList = customerService.getCustomers();
        chartCustomer.getData().clear();

        Map<String, Integer> addressCountMap = new HashMap<>();
        for (CustomerDTO customerDTO : customerDTOList) {
            String address = customerDTO.getAddress();
            addressCountMap.put(address, addressCountMap.getOrDefault(address, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Customers per Address");

        for (Map.Entry<String, Integer> entry : addressCountMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartCustomer.getData().add(series);
        adjustYAxis(chartCustomer, addressCountMap.values());
    }

    private void loadAllSalesChart() {
        loadSalesChart(null, null);
    }

    private void loadSalesChart(LocalDate startDate, LocalDate endDate) {
        List<OrderDTO> orderDTOList = orderService.getOrders();
        List<String> categories = new ArrayList<>();
        List<Integer> salesQuantities = new ArrayList<>();

        for (OrderDTO orderDTO : orderDTOList) {
            LocalDate orderLocalDate = orderDTO.getOrderDate().toLocalDate();

            if (startDate != null && orderLocalDate.isBefore(startDate)) continue;
            if (endDate != null && orderLocalDate.isAfter(endDate)) continue;

            for (OrderDetailDTO orderDetailDTO : orderDetailService.getOrderProducts()) {
                ProductDTO productDTO = productService.getProductById(orderDetailDTO.getProductId());
                if (productDTO != null && productDTO.getCategory() != null) {
                    String category = productDTO.getCategory();
                    int quantity = orderDetailDTO.getQuantity();
                    int index = categories.indexOf(category);

                    if (index != -1) {
                        salesQuantities.set(index, salesQuantities.get(index) + quantity);
                    } else {
                        categories.add(category);
                        salesQuantities.add(quantity);
                    }
                }
            }
        }

        chartSales.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sold Quantity by Category");

        for (int i = 0; i < categories.size(); i++) {
            series.getData().add(new XYChart.Data<>(categories.get(i), salesQuantities.get(i)));
        }

        chartSales.getData().add(series);

        adjustYAxis(chartSales, salesQuantities);
    }

    private void adjustYAxis(Chart chart, Collection<Integer> dataValues) {
        NumberAxis yAxis = (NumberAxis) ((XYChart<String, Number>) chart).getYAxis();
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(Collections.max(dataValues) + 5);
    }

    private void generateReport(String reportFileName) {
        // Load jrxml from resources/reports on the classpath so the code works on any machine
        try (InputStream is = getClass().getResourceAsStream("/reports/" + reportFileName)) {
            if (is == null) {
                throw new FileNotFoundException("Report resource not found: /reports/" + reportFileName);
            }

            JasperDesign design = JRXmlLoader.load(is);

            // Some JRXML templates expect field names like customerId, orderId, totalAmount, employeeId, mobileNumber, etc.
            // The application's DB schema uses different column names (id, totalPrice, userId, mobile, supplyItem, ...).
            // To make the reports work regardless of column names, set a query on the design that aliases DB columns to the JRXML field names.
            String queryText = null;
            switch (reportFileName) {
                case "Customers.jrxml":
                    queryText = "SELECT id AS customerId, name, mobile AS mobileNumber, address FROM customer";
                    break;
                case "Suppliers.jrxml":
                    queryText = "SELECT id AS supplierId, name, company, email, supplyItem AS item FROM supplier";
                    break;
                case "AllTimeOrders.jrxml":
                    queryText = "SELECT id AS orderId, orderDate, totalPrice AS totalAmount, paymentMethod, userId AS employeeId, customerId FROM orders";
                    break;
                case "LastMonthOrders.jrxml":
                    queryText = "SELECT id AS orderId, orderDate, totalPrice AS totalAmount, paymentMethod, userId AS employeeId, customerId FROM orders WHERE orderDate >= CURDATE() - INTERVAL 30 DAY";
                    break;
                case "LastWeekOrders.jrxml":
                    queryText = "SELECT id AS orderId, orderDate, totalPrice AS totalAmount, paymentMethod, userId AS employeeId, customerId FROM orders WHERE orderDate >= CURDATE() - INTERVAL 7 DAY";
                    break;
                case "LastDayOrders.jrxml":
                    queryText = "SELECT id AS orderId, orderDate, totalPrice AS totalAmount, paymentMethod, userId AS employeeId, customerId FROM orders WHERE orderDate >= CURDATE() - INTERVAL 1 DAY";
                    break;
                default:
                    queryText = null;
            }

            if (queryText != null) {
                JRDesignQuery jrQuery = new JRDesignQuery();
                jrQuery.setText(queryText);
                design.setQuery(jrQuery);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(design);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
