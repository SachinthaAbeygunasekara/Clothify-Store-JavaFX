package repository;

import repository.custom.impl.*;
import util.RepositoryType;

public class RepositoryFactory {
    private static RepositoryFactory instance;
    private RepositoryFactory(){}

    public static RepositoryFactory getInstance() {
        return instance==null?instance=new RepositoryFactory():instance;
    }

    public <T extends SuperRepository>T getRepositoryType(RepositoryType repositoryType){
        switch (repositoryType){
            case USER:return (T) new UserRepositoryImpl();
            case EMPLOYEE:return (T) new EmployeeRepositoryImpl();
            case CUSTOMERS:return (T) new CustomerRepositoryImpl();
            case SUPPLIER:return (T) new SupplierRepositoryImpl();
            case PRODUCT:return (T) new ProductRepositoryImpl();
        }
        return null;

    }
}
