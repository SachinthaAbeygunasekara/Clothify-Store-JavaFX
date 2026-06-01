package service;

import service.custom.impl.*;
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
            case SUPPLIER: return (T) SupplierServiceImpl.getInstance();
            case PRODUCT: return (T) ProductServiceImpl.getInstance();
            case ORDERS: return (T) OrderServiceImpl.getInstance();
            case ORDERPRODUCT: return (T) OrderDetailServiceImpl.getInstance();
        }
        return null;
    }
}
