package service.custom;

import dto.OrderDetailDTO;
import service.SuperService;

import java.util.List;

public interface OrderDetailService extends SuperService {
    List<OrderDetailDTO> getOrderProducts();
}
