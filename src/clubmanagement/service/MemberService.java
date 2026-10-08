package clubmanagement.service;

import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Member;
import clubmanagement.repository.MemberRepository;
import clubmanagement.util.InputValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Quan ly nghiep vu lien quan den Member: them, xoa, tim kiem, cap nhat, sap xep.
 * Service chiu trach nhiem VALIDATE du lieu (ca khi UI da kiem tra) va goi
 * MemberRepository de luu xuong database.
 */
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Them mot thanh vien moi.
     * - member null, id/ten rong, email sai dinh dang, SDT sai, thieu loai thanh vien
     *   -> InvalidInputException.
     * - ID da ton tai (khong phan biet hoa/thuong) -> DuplicateMemberException.
     */
    public void addMember(Member member)
            throws DuplicateMemberException, InvalidInputException {
        if (member == null) {
            throw new InvalidInputException("Thông tin thành viên không được để trống.");
        }
        member.setId(InputValidator.requireNotBlank("Mã thành viên", member.getId()));
        member.setName(InputValidator.requireNotBlank("Họ tên", member.getName()));
        member.setEmail(InputValidator.requireEmail(member.getEmail()));
        member.setPhone(InputValidator.optionalPhone(member.getPhone()));
        if (member.getMembershipType() == null || member.getJoinDate() == null) {
            throw new InvalidInputException("Loại thành viên và ngày tham gia không được để trống.");
        }

        if (memberRepository.existsById(member.getId())) {
            throw new DuplicateMemberException(
                    "Mã thành viên \"" + member.getId() + "\" đã tồn tại.");
        }
        memberRepository.insert(member);
    }

    /**
     * Xoa thanh vien khoi CLB theo id.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public void removeMember(String memberId)
            throws MemberNotFoundException {

        // Xoa truc tiep trong database
        boolean deleted = memberId != null && memberRepository.deleteById(memberId.trim());

        // Khong xoa duoc -> khong ton tai
        if (!deleted) {
            throw new MemberNotFoundException(
                    "Không tìm thấy thành viên có mã: " + memberId);
        }
    }

    /**
     * Tim thanh vien theo id chinh xac.
     * Neu khong tim thay -> MemberNotFoundException.
     */
    public Member searchMemberById(String memberId)
            throws MemberNotFoundException {

        Member member = memberId == null ? null : memberRepository.findById(memberId.trim());

        // Khong tim thay
        if (member == null) {
            throw new MemberNotFoundException(
                    "Không tìm thấy thành viên có mã: " + memberId);
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
     * Email/SDT sai dinh dang -> InvalidInputException; khong tim thay ID -> MemberNotFoundException.
     */
    public void updateMember(String memberId, String newEmail, String newPhone)
            throws MemberNotFoundException, InvalidInputException {

        String email = InputValidator.requireEmail(newEmail);
        String phone = InputValidator.optionalPhone(newPhone);

        Member member = memberId == null ? null : memberRepository.findById(memberId.trim());

        // 2. Khong tim thay
        if (member == null) {
            throw new MemberNotFoundException(
                    "Không tìm thấy thành viên có mã: " + memberId);
        }

        // 3. Cap nhat email va phone trong object
        member.setEmail(email);
        member.setPhone(phone);

        // 4. Ghi thay doi vao database
        memberRepository.update(member);
    }
}
