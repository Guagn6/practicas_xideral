public interface UserRepository {
    UserDto createUser(String name);
    UserDto getUser(Long id);
}
