package clubmanagement.model;

/**
 * Loai thanh vien trong CLB.
 * Moi loai co mot ty le giam gia (discountRate) ap dung khi tinh phi su kien.
 */
public enum MembershipType {
    REGULAR(0.0),
    VIP(0.2),
    HONORARY(0.5);

    private final double discountRate;

    MembershipType(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
