package az.kapital.msauthservice.service;

import az.kapital.msauthservice.domain.entity.UserEntity;
import az.kapital.msauthservice.domain.repository.UserRepository;
import az.kapital.msauthservice.exception.InvalidPasswordConfirmationException;
import az.kapital.msauthservice.exception.UserAlreadyExistsException;
import az.kapital.msauthservice.model.request.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity saveUser(UserRegistrationRequest request) {

        if (userRepository.findByUsername(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists!");
        }
        if (!(request.getPassword().equals(request.getConfirmPassword()))) {
            throw new InvalidPasswordConfirmationException("Passwords do not match!");

        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
}
