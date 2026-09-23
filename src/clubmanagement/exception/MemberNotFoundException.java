package clubmanagement.exception;

/**
 * Nem ra khi khong tim thay thanh vien voi ID duoc yeu cau.
 */
public class MemberNotFoundException extends Exception {

    public MemberNotFoundException(String message) {
        super(message);
    }
}
