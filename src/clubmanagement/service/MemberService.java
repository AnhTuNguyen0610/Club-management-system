package clubmanagement.service;

import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Member;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Quan ly toan bo nghiep vu lien quan den Member: them, xoa, tim kiem,
 * cap nhat, sap xep.
 *
 * MODULE: BIEN
 */
public class MemberService {

    private List<Member> members;

    public MemberService() {
        this.members = new ArrayList<>();
    }

    /**
     * Them mot thanh vien moi vao CLB.
     *
     * Business rules:
     * - Neu member null, hoac id/name/email rong -> InvalidInputException.
     * - Neu id da ton tai -> DuplicateMemberException.
     * - ID khong phan biet chu hoa/thuong.
     */
    public void addMember(Member member)
            throws DuplicateMemberException, InvalidInputException {

        // 1. Kiem tra member null
        if (member == null) {
            throw new InvalidInputException("Member cannot be null");
        }

        // 2. Kiem tra id, name, email rong hoac chi co khoang trang
        if (member.getId() == null || member.getId().trim().isEmpty()
                || member.getName() == null || member.getName().trim().isEmpty()
                || member.getEmail() == null || member.getEmail().trim().isEmpty()) {

            throw new InvalidInputException(
                    "Member ID, name and email cannot be empty");
        }

        // 3. Kiem tra ID da ton tai hay chua
        for (Member m : members) {
            if (m.getId().equalsIgnoreCase(member.getId())) {
                throw new DuplicateMemberException(
                        "Member ID already exists: " + member.getId());
            }
        }

        // 4. Neu hop le thi them vao danh sach
        members.add(member);
    }

    /**
     * Xoa thanh vien khoi CLB theo id.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public void removeMember(String memberId)
            throws MemberNotFoundException {

        // Tim member theo ID
        for (int i = 0; i < members.size(); i++) {

            if (members.get(i).getId().equalsIgnoreCase(memberId)) {
                members.remove(i);
                return;
            }
        }

        // Khong tim thay
        throw new MemberNotFoundException(
                "Member not found: " + memberId);
    }

    /**
     * Tim thanh vien theo id chinh xac.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public Member searchMemberById(String memberId)
            throws MemberNotFoundException {

        for (Member member : members) {

            if (member.getId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }

        throw new MemberNotFoundException(
                "Member not found: " + memberId);
    }

    /**
     * Tim kiem gan dung theo ten.
     * Khong phan biet hoa/thuong.
     * Keyword co the la mot phan cua ten.
     * Neu khong tim thay -> tra ve danh sach rong.
     */
    public List<Member> searchMemberByName(String keyword) {

        List<Member> result = new ArrayList<>();

        // Neu keyword null/rong thi tra ve danh sach rong
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        for (Member member : members) {

            if (member.getName() != null
                    && member.getName().toLowerCase().contains(searchKeyword)) {

                result.add(member);
            }
        }

        return result;
    }

    /**
     * Tra ve danh sach toan bo thanh vien.
     * Tra ve ban sao de khong cho phep code ben ngoai
     * sua truc tiep danh sach goc.
     */
    public List<Member> listAllMembers() {

        return new ArrayList<>(members);
    }

    /**
     * Sap xep danh sach thanh vien theo ten A-Z.
     *
     * Khong sap xep truc tiep members.
     * Tao ban sao -> sap xep ban sao -> tra ve ban sao.
     */
    public List<Member> sortMembersByName() {

        List<Member> result = new ArrayList<>(members);

        result.sort(
                Comparator.comparing(
                        Member::getName,
                        String.CASE_INSENSITIVE_ORDER));

        return result;
    }

    /**
     * Cap nhat email va phone cua thanh vien.
     * Neu khong tim thay ID -> MemberNotFoundException.
     */
    public void updateMember(String memberId, String newEmail, String newPhone)
            throws MemberNotFoundException {

        // Tim member
        Member member = searchMemberById(memberId);

        // Cap nhat email va phone
        member.setEmail(newEmail);
        member.setPhone(newPhone);
    }
}
