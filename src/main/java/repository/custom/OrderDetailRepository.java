package repository.custom;

import entity.OrderDetail;
import repository.SuperRepository;

import java.util.List;

public interface OrderDetailRepository extends SuperRepository {
    boolean save(List<OrderDetail> orderProducts);
    List<OrderDetail> getAll();
}
