package clubmanagement.service;

import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly toan bo nghiep vu lien quan den Member: them, xoa, tim kiem,
 * cap nhat, sap xep.
 *
 * MODULE: BIEN
 * Chi duoc sua trong file nay va Member.java (neu can).
 * Khong sua cac file thuoc module cua Nhat Minh (Event) de tranh git conflict.
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
        // TODO: BIEN
    }

    /**
     * TODO: BIEN
     * Xoa thanh vien khoi CLB theo id.
     * Business rules:
     *  - Neu khong tim thay id tuong ung -> throw MemberNotFoundException.
     *  - Nguoc lai xoa thanh vien khoi danh sach.
     */
    public void removeMember(String memberId) throws MemberNotFoundException {
        // TODO: BIEN
    }

    /**
     * TODO: BIEN
     * Tim thanh vien theo id chinh xac.
     * Neu khong tim thay -> throw MemberNotFoundException.
     */
    public Member searchMemberById(String memberId) throws MemberNotFoundException {
        // TODO: BIEN
        return null;
    }

    /**
     * TODO: BIEN
     * Tim kiem gan dung theo ten (khong phan biet hoa/thuong, cho phep chua chuoi con).
     * Neu khong tim thay ai -> tra ve danh sach rong (khong throw exception).
     */
    public List<Member> searchMemberByName(String keyword) {
        // TODO: BIEN
        return null;
    }

    /**
     * TODO: BIEN
     * Tra ve danh sach toan bo thanh vien.
     * Luu y: nen tra ve mot ban sao (new ArrayList<>(members)) de bao ve
     * encapsulation, tranh code ben ngoai sua truc tiep danh sach goc.
     */
    public List<Member> listAllMembers() {
        // TODO: BIEN
        return null;
    }

    /**
     * TODO: BIEN
     * Sap xep danh sach thanh vien theo ten (A-Z).
     * Goi y: dung Collections.sort() voi Comparator, hoac cho Member
     * implements Comparable<Member> (tuy chon thiet ke).
     */
    public List<Member> sortMembersByName() {
        // TODO: BIEN
        return null;
    }

    /**
     * TODO: BIEN
     * Cap nhat email/phone cua mot thanh vien da ton tai.
     * Neu khong tim thay id -> throw MemberNotFoundException.
     */
    public void updateMember(String memberId, String newEmail, String newPhone) throws MemberNotFoundException {
        // TODO: BIEN
    }
}
