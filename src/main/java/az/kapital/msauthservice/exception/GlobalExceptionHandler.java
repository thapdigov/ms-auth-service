package az.kapital.msauthservice.exception;

import az.kapital.msauthservice.model.response.ErrorCode;
import az.kapital.msauthservice.model.response.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GlobalResponse> alreadyExistsExceptionHandler(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GlobalResponse.builder()
                .id(UUID.randomUUID())
                .error_code(ErrorCode.ALREADY_EXISTS)
                .error_message(ex.getLocalizedMessage())
                .time(LocalDateTime.now())
                .build());

    }

    @ExceptionHandler(InvalidPasswordConfirmationException.class)
    public ResponseEntity<GlobalResponse> alreadyExistsExceptionHandler(InvalidPasswordConfirmationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GlobalResponse.builder()
                .id(UUID.randomUUID())
                .error_code(ErrorCode.INVALID_PASSSWORD)
                .error_message(ex.getLocalizedMessage())
                .time(LocalDateTime.now())
                .build());

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<GlobalResponse> invalidCredentialsExceptionHandler(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(GlobalResponse.builder()
                .id(UUID.randomUUID())
                .error_code(ErrorCode.INVALID_PASSSWORD)
                .error_message(ex.getLocalizedMessage())
                .time(LocalDateTime.now())
                .build());

    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalResponse> invalidTokenExceptionHandler(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GlobalResponse.builder()
                .id(UUID.randomUUID())
                .error_code(ErrorCode.INVALID_TOKEN)
                .error_message(ex.getLocalizedMessage())
                .time(LocalDateTime.now())
                .build());

    }
}
