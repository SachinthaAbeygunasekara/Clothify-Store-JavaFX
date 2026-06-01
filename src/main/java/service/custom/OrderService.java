package service.custom;

import dto.OrderDTO;
import service.SuperService;

import java.util.List;

public interface OrderService extends SuperService {
    int getLastId();
    List<OrderDTO> getOrders();
    OrderDTO getOrder(int id);
    boolean addOrder(OrderDTO orderDTO);
}
