package service;

import service.custom.impl.CustomerServiceImpl;
import service.custom.impl.EmployeeServiceImpl;
import service.custom.impl.SupplierServiceImpl;
import service.custom.impl.UserServiceImpl;
import util.ServiceType;

public class ServiceFactory {
    private static ServiceFactory serviceFactory;

    private ServiceFactory() {}

    public static ServiceFactory getInstance() {
        return serviceFactory == null? serviceFactory = new ServiceFactory(): serviceFactory;
    }

    public <T extends SuperService> T getServiceType(ServiceType serviceType){
        switch (serviceType){
            case USER: return (T) UserServiceImpl.getInstance();
            case EMPLOYEE: return (T) EmployeeServiceImpl.getInstance();
            case CUSTOMERS: return (T) CustomerServiceImpl.getInstance();
            case SUPPLIER: return (T) SupplierServiceImpl.getInstance();        }
        return null;
    }
}
