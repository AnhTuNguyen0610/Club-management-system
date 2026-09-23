package clubmanagement.exception;

/**
 * Nem ra khi them mot thanh vien co ID da ton tai trong CLB.
 */
public class DuplicateMemberException extends Exception {

    public DuplicateMemberException(String message) {
        super(message);
    }
}
