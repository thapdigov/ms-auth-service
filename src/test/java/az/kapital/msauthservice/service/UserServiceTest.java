package az.kapital.msauthservice.service;

import az.kapital.msauthservice.domain.entity.UserEntity;
import az.kapital.msauthservice.domain.repository.UserRepository;
import az.kapital.msauthservice.exception.InvalidPasswordConfirmationException;
import az.kapital.msauthservice.exception.UserAlreadyExistsException;
import az.kapital.msauthservice.model.request.UserRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private UserService userService;


    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void saveUser_ShouldSaveUser_WhenDataIsValid() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("Sanan@example.com");
        request.setPassword("Sanan123");
        request.setConfirmPassword("Sanan123");

        when(userRepository.findByUsername(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setUsername("Sanan@example.com");
        savedEntity.setPassword("encoded-password");

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserEntity result = userService.saveUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sanan@example.com", result.getUsername());
        assertEquals("encoded-password", result.getPassword());

        verify(userRepository).findByUsername("Sanan@example.com");
        verify(passwordEncoder).encode("Sanan123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void saveUser_ShouldThrowException_WhenUserAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("Bahruz@example.com");
        request.setPassword("123456");
        request.setConfirmPassword("123456");

        when(userRepository.findByUsername(request.getEmail()))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(request));

        verify(userRepository, times(1)).findByUsername("Bahruz@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ShouldThrowException_WhenPasswordsDoNotMatch() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("Sanan@example.com");
        request.setPassword("password1");
        request.setConfirmPassword("password2");

        when(userRepository.findByUsername(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidPasswordConfirmationException.class, () -> userService.saveUser(request));

        verify(userRepository, times(1)).findByUsername("Sanan@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ShouldEncodePasswordBeforeSaving() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("ALi@example.com");
        request.setPassword("Ali123");
        request.setConfirmPassword("Ali123");

        when(userRepository.findByUsername(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Ali123")).thenReturn("encoded-pass");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.saveUser(request);

        verify(passwordEncoder, times(1)).encode("Ali123");
        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();
        assertEquals("ALi@example.com", savedUser.getUsername());
        assertEquals("encoded-pass", savedUser.getPassword());
    }
}
