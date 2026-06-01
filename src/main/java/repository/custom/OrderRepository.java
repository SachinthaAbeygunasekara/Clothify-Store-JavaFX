package repository.custom;

import entity.Order;
import repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order,String> {
    int getLastOrderId();
}
