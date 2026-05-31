package service.custom.impl;

import dto.SupplierDTO;
import entity.Supplier;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.SupplierRepository;
import service.custom.SupplierService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;

public class SupplierServiceImpl implements SupplierService {
    private static SupplierServiceImpl supplierService;
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    private SupplierServiceImpl() {
        supplierRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.SUPPLIER);
        modelMapper = new ModelMapper();
    }

    public static SupplierServiceImpl getInstance() {
        return supplierService == null ? supplierService = new SupplierServiceImpl() : supplierService;
    }

    @Override
    public List<SupplierDTO> getSuppliers() {
        List<SupplierDTO> supplierDTOList = new ArrayList<>();
        try {
            List<Supplier> suppliers = supplierRepository.getAll();
            for (Supplier supplier : suppliers) {
                supplierDTOList.add(modelMapper.map(supplier, SupplierDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supplierDTOList;
    }

    @Override
    public boolean addSupplier(SupplierDTO supplierDTO) {
        try {
            Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);
            return supplierRepository.create(supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSupplier(SupplierDTO supplierDTO) {
        try {
            Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);
            return supplierRepository.update(supplier);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSupplier(int id) {
        try {
            return supplierRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
