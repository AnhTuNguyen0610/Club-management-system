package clubmanagement.repository;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Member;

import java.util.List;

/**
 * Truy cap bang "members" trong database bang JDBC.
 * Chi lo viec DOC/GHI du lieu - KHONG chua business rule (kiem tra trung ID,
 * validate input... van nam o MemberService).
 *
 * MODULE: BIEN (Giai doan 2 - Task B2.1)
 * File nay do Anh Tu (Tech Lead) chot san CHU KY cac method. Bien chi viet
 * phan than method, KHONG doi ten/tham so/kieu tra ve (de Nhat Minh va
 * MemberService lam viec song song khong bi vo).
 *
 * Bang "members": id, name, email, phone, membership_type, join_date, active
 * (xem docs/database.md). Quy uoc chuyen doi:
 *  - MembershipType  <-> chuoi TEXT (name() / MembershipType.valueOf())
 *  - LocalDate       <-> chuoi TEXT ISO (toString() / LocalDate.parse())
 *  - boolean active  <-> INTEGER 1/0
 * Loi SQL: boc SQLException thanh DatabaseException (xem mau trong DatabaseConnection).
 */
public class MemberRepository {

    private final DatabaseConnection db;

    public MemberRepository(DatabaseConnection db) {
        this.db = db;
    }

    /**
     * TODO: BIEN
     * Them mot thanh vien moi vao bang members.
     * Input: member da duoc MemberService validate (khong null, id/name/email hop le).
     * Luu y: KHONG kiem tra trung ID o day - MemberService da lam (existsById).
     */
    public void insert(Member member) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - insert()");
    }

    /**
     * TODO: BIEN
     * Cap nhat thong tin thanh vien da co (theo id): name, email, phone,
     * membership_type, active. KHONG doi id va join_date.
     */
    public void update(Member member) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - update()");
    }

    /**
     * TODO: BIEN
     * Xoa thanh vien theo id. Cac dong dang ky su kien cua thanh vien nay trong
     * bang event_participants se tu dong bi xoa (ON DELETE CASCADE).
     * @return true neu co xoa duoc 1 dong, false neu id khong ton tai.
     */
    public boolean deleteById(String id) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - deleteById()");
    }

    /**
     * TODO: BIEN
     * Tim thanh vien theo id (khong phan biet hoa/thuong - cot id da COLLATE NOCASE).
     * @return Member, hoac null neu khong tim thay (KHONG nem exception).
     */
    public Member findById(String id) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - findById()");
    }

    /**
     * TODO: BIEN
     * Kiem tra id da ton tai chua (dung cho MemberService.addMember).
     */
    public boolean existsById(String id) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - existsById()");
    }

    /**
     * TODO: BIEN
     * Lay toan bo thanh vien, thu tu theo thoi diem them (ORDER BY rowid).
     * Khong co du lieu -> tra ve danh sach RONG (khong tra null).
     */
    public List<Member> findAll() throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - findAll()");
    }

    /**
     * TODO: BIEN
     * Tim thanh vien co ten CHUA chuoi keyword, khong phan biet hoa/thuong.
     * Luu y (edge case): toan tu LIKE cua SQLite chi bo qua hoa/thuong voi ky tu
     * ASCII, ten co dau ("Duc" / "duc" co dau) se khong khop dung. Goi y: lay
     * findAll() roi loc bang Java (toLowerCase().contains()) de giu nguyen hanh vi
     * cua ban console cu. Nhom tu chon cach, nhung phai giai thich duoc.
     */
    public List<Member> searchByName(String keyword) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - searchByName()");
    }

    /**
     * TODO: BIEN
     * Dem so thanh vien (SELECT COUNT(*)).
     */
    public int count() throws DatabaseException {
        throw new UnsupportedOperationException("TODO: BIEN - count()");
    }
}
