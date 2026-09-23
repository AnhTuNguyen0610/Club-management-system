package clubmanagement.model;

import java.time.LocalDate;

/**
 * Dai dien cho mot thanh vien cua CLB.
 * Ke thua Person (Inheritance).
 *
 * MODULE: BIEN
 * Cac field/getter/setter co ban da duoc dung san (skeleton).
 * Logic nghiep vu (validate, tim kiem, sap xep...) se duoc cai dat
 * trong MemberService, khong sua truc tiep trong class nay tru khi can thiet.
 */
public class Member extends Person {

    private MembershipType membershipType;
    private LocalDate joinDate;
    private boolean active;

    public Member(String id, String name, String email, String phone,
                   MembershipType membershipType, LocalDate joinDate) {
        super(id, name, email, phone);
        this.membershipType = membershipType;
        this.joinDate = joinDate;
        this.active = true;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getRoleDescription() {
        return "Thanh vien CLB (" + membershipType + ")";
    }

    @Override
    public String toString() {
        return super.toString() + ", Loai thanh vien: " + membershipType + ", Ngay tham gia: " + joinDate;
    }
}
