package service.custom;

import dto.EmployeeDTO;
import service.SuperService;

import java.util.List;

public interface EmployeeService extends SuperService {
    boolean addEmployee (EmployeeDTO employeeDTO);
    boolean updateEmployee (EmployeeDTO employeeDTO);
    List<EmployeeDTO> getEmployees();
    boolean deleteEmployee (int employeeId);
    EmployeeDTO getEmployeeById (int employeeId);
}
