package az.kapital.msauthservice.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String tokenIsInvalidOrExpired) {
    }
}
