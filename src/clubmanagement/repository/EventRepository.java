package clubmanagement.repository;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Competition;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.model.MembershipType;
import clubmanagement.model.SocialEvent;
import clubmanagement.model.Workshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Truy cap cac bang "events" va "event_participants" bang JDBC.
 * Chi lo DOC/GHI du lieu; business rule (suc chua, trung ID...) nam o EventService.
 *
 * Ca 3 loai su kien nam trong MOT bang, phan biet bang cot event_type
 * (WORKSHOP / COMPETITION / SOCIAL). Khi doc len, repository tao dung lop con
 * (Workshop / Competition / SocialEvent) - the hien tinh da hinh.
 */
public class EventRepository {

    private final DatabaseConnection db;

    public EventRepository(DatabaseConnection db) {
        this.db = db;
    }

    /**
     * Them su kien moi (ghi vao bang events; danh sach participants luc moi tao la
     * rong).
     * Dung instanceof (hoac getEventTypeDescription) de xac dinh event_type va cac
     * cot rieng.
     */
    public void insert(Event event) throws DatabaseException {
        String sql = "INSERT INTO events (event_id, event_name, event_date, max_participants, status, event_type, fee, speaker, prize_value, location) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, event.getEventId());
            ps.setString(2, event.getEventName());
            ps.setString(3, event.getDate());
            ps.setInt(4, event.getMaxParticipants());
            ps.setString(5, event.getStatus().name());

            if (event instanceof Workshop) {
                Workshop w = (Workshop) event;
                ps.setString(6, "WORKSHOP");
                ps.setDouble(7, w.getBaseFee());
                ps.setString(8, w.getSpeaker());
                ps.setNull(9, Types.REAL);
                ps.setNull(10, Types.VARCHAR);
            } else if (event instanceof Competition) {
                Competition c = (Competition) event;
                ps.setString(6, "COMPETITION");
                ps.setDouble(7, c.getEntryFee());
                ps.setNull(8, Types.VARCHAR);
                ps.setDouble(9, c.getPrizeValue());
                ps.setNull(10, Types.VARCHAR);
            } else if (event instanceof SocialEvent) {
                SocialEvent s = (SocialEvent) event;
                ps.setString(6, "SOCIAL");
                ps.setNull(7, Types.REAL);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.REAL);
                ps.setString(10, s.getLocation());
            } else {
                throw new DatabaseException("Loai su kien khong duoc ho tro: " + event.getClass().getSimpleName());
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Loi khi them su kien: " + event.getEventId(), e);
        }
    }

    /**
     * Cap nhat trang thai su kien (UPDATE events SET status = ? WHERE event_id =
     * ?).
     * 
     * @return true neu co dong duoc cap nhat, false neu khong ton tai eventId.
     */
    public boolean updateStatus(String eventId, EventStatus status) throws DatabaseException {
        String sql = "UPDATE events SET status = ? WHERE event_id = ?";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, eventId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi cap nhat trang thai su kien: " + eventId, e);
        }
    }

    /**
     * Tim su kien theo id (khong phan biet hoa/thuong), KEM danh sach participants.
     * 
     * @return Event (dung lop con Workshop/Competition/SocialEvent), hoac null neu
     *         khong co.
     */
    public Event findById(String eventId) throws DatabaseException {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        Event event = null;

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    event = mapRowToEvent(rs);
                    loadParticipantsForEvent(conn, event);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi tim su kien theo ID: " + eventId, e);
        }
        return event;
    }

    /**
     * Kiem tra eventId da ton tai chua (dung cho EventService.addEvent).
     */
    public boolean existsById(String eventId) throws DatabaseException {
        String sql = "SELECT 1 FROM events WHERE event_id = ?";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi kiem tra su kien: " + eventId, e);
        }
    }

    /**
     * Lay toan bo su kien (kem participants), thu tu theo thoi diem them.
     * Khong co du lieu -> danh sach RONG (khong tra null).
     */
    public List<Event> findAll() throws DatabaseException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY rowid";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Event event = mapRowToEvent(rs);
                loadParticipantsForEvent(conn, event);
                events.add(event);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi lay danh sach tat ca su kien", e);
        }
        return events;
    }

    /**
     * Lay cac su kien co trang thai nhat dinh (kem participants).
     */
    public List<Event> findByStatus(EventStatus status) throws DatabaseException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = ? ORDER BY rowid";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = mapRowToEvent(rs);
                    loadParticipantsForEvent(conn, event);
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi lay danh sach su kien theo trang thai", e);
        }
        return events;
    }

    /**
     * Ghi 1 dong vao event_participants (dang ky thanh vien vao su kien).
     * Neu (eventId, memberId) da ton tai -> bo qua, KHONG nem loi
     * (goi y: INSERT OR IGNORE).
     */
    public void addParticipant(String eventId, String memberId) throws DatabaseException {
        String sql = "INSERT OR IGNORE INTO event_participants (event_id, member_id) VALUES (?, ?)";

        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, memberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi dang ki thanh vien vao su kien", e);
        }
    }

    /**
     * Xoa 1 dong khoi event_participants (huy dang ky). Khong ton tai -> bo qua.
     */
    public boolean removeParticipant(String eventId, String memberId) throws DatabaseException {
        String sql = "DELETE FROM event_participants WHERE event_id = ? AND member_id = ?";
        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);
            ps.setString(2, memberId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Loi khi huy dang ky su kien", e);
        }
    }

    /** Kiem tra thanh vien co ton tai trong bang members khong. */
    public boolean memberExists(String memberId) throws DatabaseException {
        String sql = "SELECT 1 FROM members WHERE id = ?";
        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Loi khi kiem tra thanh vien: " + memberId, e);
        }
    }

    /**
     * Dem so su kien (SELECT COUNT(*)).
     */
    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM events";
        try (Connection conn = db.open();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new DatabaseException("Loi khi dem so luong su kien", e);
        }
    }

    // --- CAC HAM HO TRO (Private) ---

    /**
     * Chuyen 1 dong du lieu tu bang 'events' (ResultSet) thanh object Event hop le.
     * Tuy thuoc vao gia tri cua cot 'event_type', chung ta se dung lenh if-else
     * de tao ra dung class con (new Workshop, new Competition hoac new SocialEvent)
     * va lay dung cac cot tuong ung (vi du: Workshop thi lay 'speaker', bo qua
     * 'prize_value').
     */
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        String type = rs.getString("event_type");
        String id = rs.getString("event_id");
        String name = rs.getString("event_name");
        String date = rs.getString("event_date");
        int max = rs.getInt("max_participants");
        EventStatus status = EventStatus.valueOf(rs.getString("status"));

        Event event;
        if ("WORKSHOP".equals(type)) {
            event = new Workshop(id, name, date, max, rs.getDouble("fee"), rs.getString("speaker"));
        } else if ("COMPETITION".equals(type)) {
            event = new Competition(id, name, date, max, rs.getDouble("fee"), rs.getDouble("prize_value"));
        } else if ("SOCIAL".equals(type)) {
            event = new SocialEvent(id, name, date, max, rs.getString("location"));
        } else {
            throw new SQLException("Loai su kien khong hop le trong database: " + type);
        }
        event.setStatus(status);
        return event;
    }

    /**
     * Tu dong tim va nap danh sach nguoi tham gia vao thuoc tinh participants cua
     * Event.
     * Ham nay dung lenh JOIN de ghep bang 'members' va bang 'event_participants'.
     * No tim tat ca nhung ai da dang ky eventId nay, tao ra object Member tuong
     * ung,
     * roi nhet thang vao cai List<Member> nam trong object Event.
     * Nho co ham nay, khi truyen Event len tang UI hoac Service, cai list
     * participants da co day du data y het nhu kieu luu bang RAM
     */
    private void loadParticipantsForEvent(Connection conn, Event event) throws SQLException {
        String sql = "SELECT m.* FROM members m " +
                "JOIN event_participants ep ON m.id = ep.member_id " +
                "WHERE ep.event_id = ? ORDER BY m.rowid";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            MembershipType.valueOf(rs.getString("membership_type")),
                            LocalDate.parse(rs.getString("join_date")));
                    member.setActive(rs.getInt("active") == 1);
                    event.addParticipant(member);
                }
            }
        }
    }
}
