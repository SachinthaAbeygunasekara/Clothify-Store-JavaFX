package service.custom.impl;

import dto.EmployeeDTO;
import entity.Employee;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.EmployeeRepository;
import service.custom.EmployeeService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private static EmployeeServiceImpl employeeService;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    private EmployeeServiceImpl() {
        employeeRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.EMPLOYEE);
        modelMapper = new ModelMapper();
    }

    public static EmployeeServiceImpl getInstance() {
        return employeeService == null? employeeService = new EmployeeServiceImpl(): employeeService;
    }

    @Override
    public boolean addEmployee(EmployeeDTO employeeDTO) {
        try {
            Employee employee = modelMapper.map(employeeDTO, Employee.class);
            return employeeRepository.create(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateEmployee(EmployeeDTO employeeDTO) {
        try {
            Employee employee = modelMapper.map(employeeDTO, Employee.class);
            return employeeRepository.update(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<EmployeeDTO> getEmployees() {
        try {
            List<Employee> employees = employeeRepository.getAll();
            List<EmployeeDTO> employeeDTOList = new ArrayList<>();
            for (Employee employee : employees) {
                employeeDTOList.add(modelMapper.map(employee, EmployeeDTO.class));
            }
            return employeeDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deleteEmployee(int employeeId) {
        try {
            return employeeRepository.deleteById(String.valueOf(employeeId));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public EmployeeDTO getEmployeeById(int employeeId) {
        try {
            Employee employee = employeeRepository.getById(String.valueOf(employeeId));
            if (employee != null) {
                return modelMapper.map(employee, EmployeeDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
