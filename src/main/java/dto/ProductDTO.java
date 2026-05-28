package dto;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDTO {
    private int id;
    private String name;
    private String category;
    private String size;
    private Double price;
    private Integer quantity;
    private String image;
    private Integer supplierID;
}
