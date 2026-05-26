package repository;

import repository.custom.impl.UserRepositoryImpl;
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
        }
        return null;

    }
}
