package clubmanagement.repository;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;

import java.util.List;

/**
 * Truy cap cac bang "events" va "event_participants" bang JDBC.
 * Chi lo DOC/GHI du lieu - business rule (su kien day, trung ID...) van nam o EventService.
 *
 * MODULE: NHAT MINH (Giai doan 2 - Task N2.1)
 * Anh Tu chot san CHU KY method. Nhat Minh chi viet phan than, KHONG doi
 * ten/tham so/kieu tra ve.
 *
 * Bang "events" luu ca 3 loai su kien trong MOT bang, phan biet bang cot event_type:
 *   WORKSHOP    -> fee = baseFee,  speaker
 *   COMPETITION -> fee = entryFee, prize_value
 *   SOCIAL      -> location
 * Cac cot khong dung cho loai do de NULL. Khi doc len phai tao dung lop con
 * (new Workshop / new Competition / new SocialEvent) - day chinh la cho the
 * hien tinh DA HINH khi nap du lieu tu DB.
 *
 * Danh sach nguoi tham gia (Event.getParticipants()) lay tu bang event_participants
 * JOIN members. De MODULE nay doc lap voi MemberRepository (khong phai cho Bien),
 * hay tu viet 1 ham private map ResultSet -> Member ngay trong file nay.
 * Quy uoc chuyen doi giong MemberRepository: MembershipType/EventStatus <-> TEXT (name()/valueOf()),
 * LocalDate <-> TEXT ISO, boolean <-> 1/0.
 */
public class EventRepository {

    private final DatabaseConnection db;

    public EventRepository(DatabaseConnection db) {
        this.db = db;
    }

    /**
     * TODO: NHAT MINH
     * Them su kien moi (ghi vao bang events; danh sach participants luc moi tao la rong).
     * Dung instanceof (hoac getEventTypeDescription) de xac dinh event_type va cac cot rieng.
     */
    public void insert(Event event) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - insert()");
    }

    /**
     * TODO: NHAT MINH
     * Cap nhat trang thai su kien (UPDATE events SET status = ? WHERE event_id = ?).
     * @return true neu co dong duoc cap nhat, false neu khong ton tai eventId.
     */
    public boolean updateStatus(String eventId, EventStatus status) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - updateStatus()");
    }

    /**
     * TODO: NHAT MINH
     * Tim su kien theo id (khong phan biet hoa/thuong), KEM danh sach participants.
     * @return Event (dung lop con Workshop/Competition/SocialEvent), hoac null neu khong co.
     */
    public Event findById(String eventId) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - findById()");
    }

    /**
     * TODO: NHAT MINH
     * Kiem tra eventId da ton tai chua (dung cho EventService.addEvent).
     */
    public boolean existsById(String eventId) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - existsById()");
    }

    /**
     * TODO: NHAT MINH
     * Lay toan bo su kien (kem participants), thu tu theo thoi diem them.
     * Khong co du lieu -> danh sach RONG (khong tra null).
     */
    public List<Event> findAll() throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - findAll()");
    }

    /**
     * TODO: NHAT MINH
     * Lay cac su kien co trang thai nhat dinh (kem participants).
     */
    public List<Event> findByStatus(EventStatus status) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - findByStatus()");
    }

    /**
     * TODO: NHAT MINH
     * Ghi 1 dong vao event_participants (dang ky thanh vien vao su kien).
     * Neu (eventId, memberId) da ton tai -> bo qua, KHONG nem loi
     * (goi y: INSERT OR IGNORE).
     */
    public void addParticipant(String eventId, String memberId) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - addParticipant()");
    }

    /**
     * TODO: NHAT MINH
     * Xoa 1 dong khoi event_participants (huy dang ky). Khong ton tai -> bo qua.
     */
    public void removeParticipant(String eventId, String memberId) throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - removeParticipant()");
    }

    /**
     * TODO: NHAT MINH
     * Dem so su kien (SELECT COUNT(*)).
     */
    public int count() throws DatabaseException {
        throw new UnsupportedOperationException("TODO: NHAT MINH - count()");
    }
}
