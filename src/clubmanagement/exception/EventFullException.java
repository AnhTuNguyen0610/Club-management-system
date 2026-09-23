package clubmanagement.exception;

/**
 * Nem ra khi so luong nguoi dang ky su kien da dat toi da (maxParticipants).
 */
public class EventFullException extends Exception {

    public EventFullException(String message) {
        super(message);
    }
}
