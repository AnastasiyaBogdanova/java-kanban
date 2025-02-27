package exception;

public class InvalidTaskStartTimeException extends RuntimeException {
    public InvalidTaskStartTimeException(String message) {
        super(message);
    }
}