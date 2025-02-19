package exception;

public class InvalidTaskStartTimeException extends Exception {
    public InvalidTaskStartTimeException(String message) {
        super(message);
    }
}