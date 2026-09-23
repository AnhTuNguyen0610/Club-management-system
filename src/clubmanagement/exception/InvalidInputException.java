package clubmanagement.exception;

/**
 * Nem ra khi du lieu dau vao khong hop le (rong, null, sai dinh dang...).
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}
