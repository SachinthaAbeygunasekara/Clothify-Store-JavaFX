package entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "orderhistory")
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
