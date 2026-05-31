package repository.custom;

import entity.Employee;
import repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, String> {
}
