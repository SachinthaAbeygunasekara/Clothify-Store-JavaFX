package service.custom;

import dto.CustomerDTO;
import service.SuperService;

import java.util.List;

public interface CustomerService extends SuperService {
    List<CustomerDTO> getCustomers();
    boolean addCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(int id);
    boolean updateCustomer(CustomerDTO customerDTO);
    boolean deleteCustomer(int id);
}
