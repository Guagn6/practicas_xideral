import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceMockito userServiceMockito;

    @Test
    public void testCreateUser() {
        UserDto objectMocked = new UserDto(1L, "Prueba");
        UserDto expected = new UserDto(1L, "Prueba");
        Mockito.when(userRepository.createUser("Prueba")).thenReturn(expected);
        final UserDto result = userServiceMockito.createUser("Prueba");

        assertEquals(expected.id, result.id, "Prueba por ID");
        assertEquals(expected.name, result.name, "Prueba por nombre");
        assertEquals(expected, result, "Prueba por Objeto");

        Mockito.verify(userRepository, Mockito.times(1))
                .createUser("Prueba");
    }

    @Test
    public void testGetUser() {
        UserDto objectMocked = new UserDto(1L, "Juan");
        UserDto expected = new UserDto(1L, "Juan");
        Mockito.when(userRepository.getUser(1L)).thenReturn(expected);
        final UserDto result = userServiceMockito.getUser(1L);

        assertEquals(expected, result, "Prueba por Objeto");

        Mockito.verify(userRepository).getUser(1L);
    }
}