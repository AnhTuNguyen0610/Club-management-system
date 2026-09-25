package clubmanagement.model;

/**
 * Su kien dang Workshop / Hoi thao chuyen de.
 *
 * MODULE: NHAT MINH
 */
public class Workshop extends Event {

    private double baseFee;
    private String speaker;

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
     * TODO: NHAT MINH
     * Business rule:
     * - Phi tham gia mac dinh = baseFee.
     * - Neu so luong dang ky hien tai (participants.size()) vuot qua 80%
     * maxParticipants thi giam 10% phi (khuyen khich dang ky som).
     * Input: khong co tham so, doc du lieu tu field cua chinh Workshop.
     * Output: gia tri phi (double) >= 0.
     */
    @Override
    public double calculateFee() {
        double fillRate = (double) participants.size() / maxParticipants;

        if (fillRate > 0.8)
            return baseFee * 0.9;

        return baseFee;
    }

    @Override
    public String getEventTypeDescription() {
        return "Workshop";
    }
}
