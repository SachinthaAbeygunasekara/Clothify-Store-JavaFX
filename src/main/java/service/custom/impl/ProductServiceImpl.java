package service.custom.impl;

import dto.CustomerDTO;
import dto.ProductDTO;
import entity.Customer;
import entity.Product;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.ProductRepository;
import service.custom.ProductService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ProductServiceImpl implements ProductService {

    private static ProductServiceImpl productService;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    private ProductServiceImpl() {
        // Use the correct repository type for products
        productRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.PRODUCT);
        modelMapper = new ModelMapper();
    }

    public static ProductServiceImpl getInstance() {
        return productService == null ? productService = new ProductServiceImpl() : productService;
    }

    @Override
    public List<ProductDTO> getProducts() {
        try {
            List<Product> products = productRepository.getAll();
            List<ProductDTO> productDTOList = new ArrayList<>();
            for (Product product : products) {
                productDTOList.add(modelMapper.map(product, ProductDTO.class));
            }
            return productDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addProduct(ProductDTO productDTO) {
        try {
            Product product = modelMapper.map(productDTO, Product.class);
            return productRepository.create(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateProduct(ProductDTO productDTO) {
        try {
            Product product = modelMapper.map(productDTO, Product.class);
            return productRepository.update(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProduct(Integer id) {
        try {
            return productRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        try {
            Product product = productRepository.getById(id);
            if (product != null) {
                return modelMapper.map(product, ProductDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
