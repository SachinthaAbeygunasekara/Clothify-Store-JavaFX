package service.custom.impl;

import dto.CustomerDTO;
import dto.EmployeeDTO;
import entity.Customer;
import entity.Employee;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.CustomerRepository;
import service.custom.CustomerService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private static CustomerServiceImpl customerService;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    private CustomerServiceImpl() {
        customerRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.CUSTOMERS);
        modelMapper = new ModelMapper();
    }

    public static CustomerServiceImpl getInstance() {
        return customerService == null ? customerService = new CustomerServiceImpl() : customerService;
    }

    @Override
    public List<CustomerDTO> getCustomers() {
        try {
            List<Customer> customers = customerRepository.getAll();
            List<CustomerDTO> customerDTOList = new ArrayList<>();
            for (Customer customer : customers) {
                customerDTOList.add(modelMapper.map(customer, CustomerDTO.class));
            }
            return customerDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean addCustomer(CustomerDTO customerDTO) {
        try {
            Customer customer = modelMapper.map(customerDTO, Customer.class);
            return customerRepository.create(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CustomerDTO getCustomerById(int id) {
        try {
            Customer customer = customerRepository.getById(String.valueOf(id));
            if (customer != null) {
                return modelMapper.map(customer, CustomerDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateCustomer(CustomerDTO customerDTO) {
        try {
            Customer customer = modelMapper.map(customerDTO, Customer.class);
            return customerRepository.update(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id) {
        try {
            return customerRepository.deleteById(String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
