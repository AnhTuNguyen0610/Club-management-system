package clubmanagement.model;

/**
 * Su kien dang Cuoc thi (co le phi dang ky va giai thuong).
 */
public class Competition extends Event {

    private final double entryFee;
    private final double prizeValue;

    public Competition(String eventId, String eventName, String date, int maxParticipants,
            double entryFee, double prizeValue) {
        super(eventId, eventName, date, maxParticipants);
        this.entryFee = entryFee;
        this.prizeValue = prizeValue;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public double getPrizeValue() {
        return prizeValue;
    }

    /**
     * Business rule:
     * - Phi tham gia mac dinh = entryFee.
     * - Thanh vien loai VIP/HONORARY duoc giam gia theo
     * MembershipType.getDiscountRate() (neu ham nay duoc goi
     * trong ngu canh biet truoc thanh vien dang ky, co the
     * overload them mot phien ban calculateFee(Member member)).
     * Output: gia tri phi (double) >= 0.
     */
    @Override
    public double calculateFee() {
        return entryFee;
    }

    public double calculateFee(Member member) {
        double discount = member.getMembershipType().getDiscountRate();
        double finalFee = entryFee * (1 - discount);
        return Math.max(0, finalFee);
    }

    @Override
    public String getEventTypeDescription() {
        return "Cuoc thi";
    }
}
