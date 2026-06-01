package repository.custom;

import entity.OrderDetail;
import entity.Product;
import repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    boolean updateQuantity(List<OrderDetail> orderDetailList);
}
