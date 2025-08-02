import java.util.HashMap;

public class UserServiceMockito {
    private final UserRepository userRepository;

    public UserServiceMockito(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }
        return userRepository.createUser(name);
    }

    public UserDto getUser(final long id) {
        return userRepository.getUser(id);
    }

}
