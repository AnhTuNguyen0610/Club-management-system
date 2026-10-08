package clubmanagement.model;

/**
 * Su kien dang Workshop / Hoi thao chuyen de.
 */
public class Workshop extends Event {

    /** Con trong 20% suat dau tien thi duoc giam 10% (early bird). */
    private static final double EARLY_BIRD_FILL_RATE = 0.2;
    private static final double EARLY_BIRD_DISCOUNT = 0.1;

    private final double baseFee;
    private final String speaker;

    public Workshop(String eventId, String eventName, String date, int maxParticipants,
            double baseFee, String speaker) {
        super(eventId, eventName, date, maxParticipants);
        this.baseFee = baseFee;
        this.speaker = speaker;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public String getSpeaker() {
        return speaker;
    }

    /**
     * Business rule:
     * - Phi tham gia mac dinh = baseFee.
     * - Neu so luong dang ky hien tai (participants.size()) chua vuot qua
     * 20% cua maxParticipants thi giam 10% phi (khuyen khich dang ky som).
     * Input: khong co tham so, doc du lieu tu field cua chinh Workshop.
     * Output: gia tri phi (double) >= 0.
     */
    @Override
    public double calculateFee() {
        if (maxParticipants <= 0) {
            return baseFee;
        }
        double fillRate = (double) participants.size() / maxParticipants;
        if (fillRate <= EARLY_BIRD_FILL_RATE) {
            return baseFee * (1 - EARLY_BIRD_DISCOUNT);
        }
        return baseFee;
    }

    @Override
    public String getEventTypeDescription() {
        return "Workshop";
    }
}
