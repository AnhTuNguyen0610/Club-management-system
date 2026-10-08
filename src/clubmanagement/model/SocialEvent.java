package clubmanagement.model;

/**
 * Su kien giao luu / sinh hoat CLB (thong thuong mien phi).
 */
public class SocialEvent extends Event {

    private final String location;

    public SocialEvent(String eventId, String eventName, String date, int maxParticipants,
            String location) {
        super(eventId, eventName, date, maxParticipants);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Business rule:
     * - Su kien giao luu mac dinh mien phi -> tra ve 0.
     * Output: gia tri phi (double), thuong la 0.
     */
    @Override
    public double calculateFee() {
        return 0;
    }

    @Override
    public String getEventTypeDescription() {
        return "Giao luu";
    }
}
