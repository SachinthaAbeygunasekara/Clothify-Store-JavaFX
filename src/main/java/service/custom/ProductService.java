package service.custom;

import dto.ProductDTO;
import service.SuperService;

import java.util.List;

public interface ProductService extends SuperService {
    List<ProductDTO> getProducts();
    boolean addProduct(ProductDTO productDTO);
    boolean updateProduct(ProductDTO productDTO);
    boolean deleteProduct(Integer id);
    ProductDTO getProductById(Integer id);
}
