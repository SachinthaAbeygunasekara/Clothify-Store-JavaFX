package dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SupplierDTO {
    private int id;
    private String name;
    private String company;
    private String email;
    private String supplyItem;
}
