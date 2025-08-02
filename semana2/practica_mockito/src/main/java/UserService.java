import java.util.HashMap;

public class UserService {
    private final HashMap<Long, UserDto> users = new HashMap<>();

    public UserDto createUser(Long id, String name) {
        return users.put(id, new UserDto(id, name));
    }

    public UserDto getUser(Long id) {
        return users.get(id);
    }

    public UserDto updateUser(Long id, String name) {
        return users.put(id, new UserDto(id, name));
    }
}
