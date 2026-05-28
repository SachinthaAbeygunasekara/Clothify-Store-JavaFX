package dto;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class OrderDetailDTO {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
}
