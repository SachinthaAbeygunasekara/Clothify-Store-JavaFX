package dto;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderHistoryDTO {
    private int orderId;
    private Date orderDate;
    private String productName;
    private Double unitPrice;
    private int quantity;
    private Double totalAmount;
    private String paymentMethod;
    private String customerName;
    private String employeeName;
}
