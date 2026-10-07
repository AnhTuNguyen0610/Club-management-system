package clubmanagement.service;

import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Member;
import clubmanagement.repository.MemberRepository;

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

    // TODO: BIEN (Giai doan 2 - Task B2.2)
    // Hien tai du lieu van luu trong List (in-memory) nen ban Console cu van chay.
    // Khi chuyen sang database: thay MOI thao tac tren "members" bang
    // memberRepository,
    // roi XOA field "members". Giu nguyen chu ky (signature) cac method public va
    // giu
    // nguyen cac business rule/exception da co.

    private final MemberRepository memberRepository;

    /**
     * Anh Tu (Tech Lead) da chot constructor nay: Main truyen MemberRepository vao.
     * Bien KHONG doi chu ky constructor (neu doi se lam Main.java khong bien dich
     * duoc).
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
        // Thay cho viec duyet List members cu
        if (memberRepository.existsById(member.getId())) {
            throw new DuplicateMemberException(
                    "Member ID already exists: " + member.getId());
        }

        // 4. Neu hop le thi luu vao database
        memberRepository.insert(member);
    }

    /**
     * Xoa thanh vien khoi CLB theo id.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public void removeMember(String memberId)
            throws MemberNotFoundException {

        // Xoa truc tiep trong database
        boolean deleted = memberRepository.deleteById(memberId);

        // Khong xoa duoc -> khong ton tai
        if (!deleted) {
            throw new MemberNotFoundException(
                    "Member not found: " + memberId);
        }
    }

    /**
     * Tim thanh vien theo id chinh xac.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public Member searchMemberById(String memberId)
            throws MemberNotFoundException {

        // Tim trong database
        Member member = memberRepository.findById(memberId);

        // Khong tim thay
        if (member == null) {
            throw new MemberNotFoundException(
                    "Member not found: " + memberId);
        }

        return member;
    }

    /**
     * Tim kiem gan dung theo ten.
     * Khong phan biet hoa/thuong.
     * Keyword co the la mot phan cua ten.
     * Neu khong tim thay -> tra ve danh sach rong.
     */
    public List<Member> searchMemberByName(String keyword) {

        // Repository da xu ly viec tim kiem trong database
        return memberRepository.searchByName(keyword);
    }

    /**
     * Tra ve danh sach toan bo thanh vien.
     * Tra ve ban sao de khong cho phep code ben ngoai
     * sua truc tiep danh sach goc.
     */
    public List<Member> listAllMembers() {

        // Lay du lieu tu database
        return memberRepository.findAll();
    }

    /**
     * Sap xep danh sach thanh vien theo ten A-Z.
     *
     * Khong sap xep truc tiep du lieu trong database.
     * Lay danh sach tu repository -> tao ban sao -> sap xep ban sao.
     */
    public List<Member> sortMembersByName() {

        // Lay danh sach tu database
        List<Member> result = new ArrayList<>(
                memberRepository.findAll());

        // Sap xep A-Z, khong phan biet hoa/thuong
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

        // 1. Tim member trong database
        Member member = memberRepository.findById(memberId);

        // 2. Khong tim thay
        if (member == null) {
            throw new MemberNotFoundException(
                    "Member not found: " + memberId);
        }

        // 3. Cap nhat email va phone trong object
        member.setEmail(newEmail);
        member.setPhone(newPhone);

        // 4. Ghi thay doi vao database
        memberRepository.update(member);
    }
}
