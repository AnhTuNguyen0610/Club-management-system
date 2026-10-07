package clubmanagement.repository;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Member;
import clubmanagement.model.MembershipType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
 * - MembershipType <-> chuoi TEXT (name() / MembershipType.valueOf())
 * - LocalDate <-> chuoi TEXT ISO (toString() / LocalDate.parse())
 * - boolean active <-> INTEGER 1/0
 * Loi SQL: boc SQLException thanh DatabaseException (xem mau trong
 * DatabaseConnection).
 */
public class MemberRepository {

    private final DatabaseConnection db;

    public MemberRepository(DatabaseConnection db) {
        this.db = db;
    }

    /**
     * TODO: BIEN
     * Them mot thanh vien moi vao bang members.
     * Input: member da duoc MemberService validate (khong null, id/name/email hop
     * le).
     * Luu y: KHONG kiem tra trung ID o day - MemberService da lam (existsById).
     */
    public void insert(Member member) throws DatabaseException {

        String sql = """
                INSERT INTO members
                (id, name, email, phone, membership_type, join_date, active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getMembershipType().name());
            ps.setString(6, member.getJoinDate().toString());
            ps.setInt(7, member.isActive() ? 1 : 0);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot insert member: " + member.getId(), e);
        }
    }

    /**
     * TODO: BIEN
     * Cap nhat thong tin thanh vien da co (theo id): name, email, phone,
     * membership_type, active. KHONG doi id va join_date.
     */
    public void update(Member member) throws DatabaseException {

        String sql = """
                UPDATE members
                SET name = ?,
                    email = ?,
                    phone = ?,
                    membership_type = ?,
                    active = ?
                WHERE id = ?
                """;

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getMembershipType().name());
            ps.setInt(5, member.isActive() ? 1 : 0);
            ps.setString(6, member.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot update member: " + member.getId(), e);
        }
    }

    /**
     * TODO: BIEN
     * Xoa thanh vien theo id. Cac dong dang ky su kien cua thanh vien nay trong
     * bang event_participants se tu dong bi xoa (ON DELETE CASCADE).
     * 
     * @return true neu co xoa duoc 1 dong, false neu id khong ton tai.
     */
    public boolean deleteById(String id) throws DatabaseException {

        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot delete member: " + id, e);
        }
    }

    /**
     * TODO: BIEN
     * Tim thanh vien theo id (khong phan biet hoa/thuong - cot id da COLLATE
     * NOCASE).
     * 
     * @return Member, hoac null neu khong tim thay (KHONG nem exception).
     */
    public Member findById(String id) throws DatabaseException {

        String sql = """
                SELECT id, name, email, phone,
                       membership_type, join_date, active
                FROM members
                WHERE id = ?
                """;

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRowToMember(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot find member: " + id, e);
        }
    }

    /**
     * TODO: BIEN
     * Kiem tra id da ton tai chua (dung cho MemberService.addMember).
     */
    public boolean existsById(String id) throws DatabaseException {

        String sql = """
                SELECT COUNT(*)
                FROM members
                WHERE id = ?
                """;

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }

                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot check member ID: " + id, e);
        }
    }

    /**
     * TODO: BIEN
     * Lay toan bo thanh vien, thu tu theo thoi diem them (ORDER BY rowid).
     * Khong co du lieu -> tra ve danh sach RONG (khong tra null).
     */
    public List<Member> findAll() throws DatabaseException {

        List<Member> result = new ArrayList<>();

        String sql = """
                SELECT id, name, email, phone,
                       membership_type, join_date, active
                FROM members
                ORDER BY rowid
                """;

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToMember(rs));
            }

            return result;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot find all members", e);
        }
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

        List<Member> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        // Dung findAll() + loc bang Java de xu ly tot hon ten tieng Viet co dau.
        List<Member> allMembers = findAll();

        for (Member member : allMembers) {

            if (member.getName() != null
                    && member.getName().toLowerCase().contains(searchKeyword)) {

                result.add(member);
            }
        }

        return result;
    }

    /**
     * TODO: BIEN
     * Dem so thanh vien (SELECT COUNT(*)).
     */
    public int count() throws DatabaseException {

        String sql = "SELECT COUNT(*) FROM members";

        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot count members", e);
        }
    }

    /**
     * Ham phu:
     * Chuyen mot dong trong ResultSet thanh object Member.
     *
     * Khong phai method trong chu ky Tech Lead chot.
     * Chi la helper de tranh viet lai code map du lieu nhieu lan.
     */
    private Member mapRowToMember(ResultSet rs) throws SQLException {

        Member member = new Member(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                MembershipType.valueOf(
                        rs.getString("membership_type")),
                LocalDate.parse(
                        rs.getString("join_date")));

        member.setActive(rs.getInt("active") == 1);

        return member;
    }
}
