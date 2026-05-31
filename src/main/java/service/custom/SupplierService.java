package service.custom;

import dto.SupplierDTO;
import service.SuperService;

import java.util.List;

public interface SupplierService extends SuperService {
    List<SupplierDTO> getSuppliers();
    boolean addSupplier(SupplierDTO supplierDTO);
    boolean updateSupplier(SupplierDTO supplierDTO);
    boolean deleteSupplier(int id);
}
