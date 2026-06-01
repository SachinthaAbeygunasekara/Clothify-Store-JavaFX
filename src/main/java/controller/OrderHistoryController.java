package controller;

import dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.ServiceFactory;
import service.custom.*;
import util.ServiceType;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderHistoryController implements Initializable {

    private final OrderService orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDERS);
    private final OrderDetailService orderDetailService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDERPRODUCT);
    private final ProductService productService = ServiceFactory.getInstance().getServiceType(ServiceType.PRODUCT);
    private final CustomerService customerService = ServiceFactory.getInstance().getServiceType(ServiceType.CUSTOMERS);
    private final UserService userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);

    private ObservableList<OrderHistoryDTO> orderHistoryItems = FXCollections.observableArrayList();

    @FXML
    public TableColumn<OrderHistoryDTO, Integer> colOrderId;

    @FXML
    public TableColumn<OrderHistoryDTO, String> colOrderDate;

    @FXML
    public TableColumn<OrderHistoryDTO, String> colProductName;

    @FXML
    public TableColumn<OrderHistoryDTO, Double> colUnitPrice;

    @FXML
    public TableColumn<OrderHistoryDTO, Integer> colQuantity;

    @FXML
    public TableColumn<OrderHistoryDTO, Double> colTotalAmount;

    @FXML
    public TableColumn<OrderHistoryDTO, String> colPaymentType;

    @FXML
    public TableColumn<OrderHistoryDTO, String> colCustomerName;

    @FXML
    public TableColumn<OrderHistoryDTO, String> colEmployeeName;

    @FXML
    public TextField txtSearchOrder;

    @FXML
    private TableView<OrderHistoryDTO> tblOrderHistory;

    @FXML
    void btnSearchOrderHistory(ActionEvent event) {
        String searchText = txtSearchOrder.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            tblOrderHistory.setItems(orderHistoryItems);
            return;
        }
        ObservableList<OrderHistoryDTO> filteredList = orderHistoryItems.filtered(order -> matchesSearch(order, searchText));
        tblOrderHistory.setItems(filteredList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colPaymentType.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        populateTable();
    }

    private void populateTable() {
        try {
            List<OrderDTO> orderDTOList = orderService.getOrders();
            List<OrderDetailDTO> orderDetailDTOList = orderDetailService.getOrderProducts();

            for (OrderDTO orderDTO : orderDTOList) {
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOList) {
                    if (orderDetailDTO.getId() == orderDTO.getId()) {
                        CustomerDTO customer = customerService.getCustomerById(orderDTO.getCustomerId());
                        ProductDTO product = productService.getProductById(orderDetailDTO.getProductId());
                        UserDTO user = userService.getUserById(orderDTO.getUserId());

                        OrderHistoryDTO orderHistory = new OrderHistoryDTO(
                                orderDTO.getId(),
                                orderDTO.getOrderDate(),
                                product != null ? product.getName() : "null",
                                product != null ? product.getPrice() : 0.0,
                                orderDetailDTO.getQuantity(),
                                (product != null ? product.getPrice() * orderDetailDTO.getQuantity() : 0.0),
                                orderDTO.getPaymentMethod() != null ? orderDTO.getPaymentMethod() : "null",
                                customer != null ? customer.getName() : "null",
                                user != null ? user.getName() : "null"
                        );
                        orderHistoryItems.add(orderHistory);
                    }
                }
            }

            tblOrderHistory.setItems(orderHistoryItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean matchesSearch(OrderHistoryDTO order, String searchText) {
        return String.valueOf(order.getOrderId()).contains(searchText) ||
                (order.getOrderDate() != null && order.getOrderDate().toString().toLowerCase().contains(searchText)) ||
                (order.getProductName() != null && order.getProductName().toLowerCase().contains(searchText)) ||
                (order.getCustomerName() != null && order.getCustomerName().toLowerCase().contains(searchText)) ||
                (order.getEmployeeName() != null && order.getEmployeeName().toLowerCase().contains(searchText)) ||
                (order.getPaymentMethod() != null && order.getPaymentMethod().toLowerCase().contains(searchText));
    }
}
