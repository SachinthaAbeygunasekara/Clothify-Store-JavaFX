package service.custom;

import dto.UserDTO;
import entity.User;
import service.SuperService;

public interface UserService extends SuperService {

    UserDTO login(String email, String password);

    UserDTO updatePassword(String email, String password);

    UserDTO getUserById(int id);

    boolean addNewUser(UserDTO userDTO);
}
