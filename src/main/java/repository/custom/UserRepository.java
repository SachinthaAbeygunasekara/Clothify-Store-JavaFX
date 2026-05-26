package repository.custom;

import entity.User;
import repository.SuperRepository;

public interface UserRepository extends SuperRepository {

    User login(String email, String password);

    User updatePassword(String email, String password);

    User getUserById(int id);

    boolean saveUser(User user);
}
