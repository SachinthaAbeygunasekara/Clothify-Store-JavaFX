package service.custom.impl;

import dto.UserDTO;
import entity.User;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.impl.UserRepositoryImpl;
import service.custom.UserService;
import util.RepositoryType;

public class UserServiceImpl implements UserService {

    private static UserServiceImpl userService;
    private final UserRepositoryImpl userRepository;

    private UserServiceImpl() {
        userRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.USER);
    }

    public static UserServiceImpl getInstance() {
        return userService == null? userService = new UserServiceImpl(): userService;
    }

    @Override
    public UserDTO login(String email, String password) {
        User user = userRepository.login(email, password);
        if (user == null) {
            return null;
        }
        return new ModelMapper().map(user, UserDTO.class);
    }

    @Override
    public UserDTO updatePassword(String email, String password) {
        return new ModelMapper().map(userRepository.updatePassword(email, password), UserDTO.class);
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepository.getUserById(id);
        if (user != null) {
            return new ModelMapper().map(user, UserDTO.class);
        }
        return null;
    }

    @Override
    public boolean addNewUser(UserDTO userDTO) {
        if (userDTO != null) {
            User user = new ModelMapper().map(userDTO, User.class);
            return userRepository.saveUser(user);
        }
        return false;
    }
}
