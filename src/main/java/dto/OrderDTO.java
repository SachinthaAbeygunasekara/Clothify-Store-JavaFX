package dto;

import entity.OrderDetail;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDTO {
    private int id;
    private Date orderDate;
    private Double totalPrice;
    private String paymentMethod;
    private int userId;
    private int customerId;
    private List<OrderDetail> orderDetailList;
}
