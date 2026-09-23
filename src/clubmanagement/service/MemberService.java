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
     * TODO: BIEN
     * Them mot thanh vien moi vao CLB.
     * Input: member (khong duoc null).
     * Business rules:
     *  - Neu member null, hoac id/name/email rong -> throw InvalidInputException.
     *  - Neu id da ton tai trong danh sach members -> throw DuplicateMemberException.
     *  - Nguoc lai, them member vao danh sach.
     * Edge cases: id trung lap phan biet chu hoa/thuong? (mac dinh: khong phan biet).
     */
    public void addMember(Member member) throws DuplicateMemberException, InvalidInputException {
        // Kiem tra member null
        if (member == null) {
            throw new InvalidInputException("Member khong duoc null");
        }

        // Kiem tra id, name, email null 
        if (member.getId() == null || member.getId().trim().isEmpty()
                || member.getName() == null || member.getName().trim().isEmpty()
                || member.getEmail() == null || member.getEmail().trim().isEmpty()) {

            throw new InvalidInputException("ID, name va email khong duoc rong");
        }

        // Kiem tra ID trung, khong pbiet hoa thuong
        for (Member m : members) {
            if (m.getId().equalsIgnoreCase(member.getId())) {
                throw new DuplicateMemberException(
                        "Member ID da ton tai: " + member.getId()
                );
            }
        }

        // Neu hop le thi them vao danh sach
        members.add(member);
    }

    /**
     * TODO: BIEN
     * Xoa thanh vien khoi CLB theo id.
     * Business rules:
     *  - Neu khong tim thay id tuong ung -> throw MemberNotFoundException.
     *  - Nguoc lai xoa thanh vien khoi danh sach.
     */
    public void removeMember(String memberId) throws MemberNotFoundException {
       if (memberId == null || memberId.trim().isEmpty()) {
            throw new MemberNotFoundException(
                    "Khong tim thay member voi ID: " + memberId
            );
        }

        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId().equalsIgnoreCase(memberId.trim())) {
                members.remove(i);
                return;
            }
        }

        throw new MemberNotFoundException(
                "Khong tim thay member voi ID: " + memberId
        );
    }

    /**
     * TODO: BIEN
     * Tim thanh vien theo id chinh xac.
     * Neu khong tim thay -> throw MemberNotFoundException.
     */
    public Member searchMemberById(String memberId) throws MemberNotFoundException {
         if (memberId != null) {
            for (Member member : members) {
                if (member.getId().equalsIgnoreCase(memberId.trim())) {
                    return member;
                }
            }
        }

        throw new MemberNotFoundException(
                "Khong tim thay member voi ID: " + memberId
        );
    }

    /**
     * TODO: BIEN
     * Tim kiem gan dung theo ten (khong phan biet hoa/thuong, cho phep chua chuoi con).
     * Neu khong tim thay ai -> tra ve danh sach rong (khong throw exception).
     */
    public List<Member> searchMemberByName(String keyword) {
         List<Member> result = new ArrayList<>();

        if (keyword == null) {
            return result;
        }

        String search = keyword.trim().toLowerCase();

        if (search.isEmpty()) {
            return result;
        }

        for (Member member : members) {
            if (member.getName() != null
                    && member.getName().toLowerCase().contains(search)) {
                result.add(member);
            }
        }

        return result;
    }

    /**
     * TODO: BIEN
     * Tra ve danh sach toan bo thanh vien.
     * Luu y: nen tra ve mot ban sao (new ArrayList<>(members)) de bao ve
     * encapsulation, tranh code ben ngoai sua truc tiep danh sach goc.
     */
    public List<Member> listAllMembers() {
      return new ArrayList<>(members);
    }

    /**
     * TODO: BIEN
     * Sap xep danh sach thanh vien theo ten (A-Z).
     * Goi y: dung Collections.sort() voi Comparator, hoac cho Member
     * implements Comparable<Member> (tuy chon thiet ke).
     */
    public List<Member> sortMembersByName() {
       List<Member> result = new ArrayList<>(members);

        result.sort(Comparator.comparing(
                Member::getName,
                String.CASE_INSENSITIVE_ORDER
        ));

        return result;
    }

    /**
     * TODO: BIEN
     * Cap nhat email/phone cua mot thanh vien da ton tai.
     * Neu khong tim thay id -> throw MemberNotFoundException.
     */
    public void updateMember(String memberId, String newEmail, String newPhone) throws MemberNotFoundException {
        String memberId,
            String newEmail,
            String newPhone) throws MemberNotFoundException {

        Member member = searchMemberById(memberId);

        member.setEmail(newEmail);
        member.setPhone(newPhone);
    }
}
