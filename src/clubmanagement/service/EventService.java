package clubmanagement.service;

import clubmanagement.exception.DuplicateEventException;
import clubmanagement.exception.EventFullException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.repository.EventRepository;
import clubmanagement.util.InputValidator;

import java.util.List;

/**
 * Nghiep vu cua Event: tao su kien, doi trang thai, dang ky / huy dang ky,
 * liet ke va tim kiem. Moi quy tac (suc chua, trung lap, trang thai) nam o day;
 * EventRepository chi doc/ghi database.
 */
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Them su kien moi.
     * - event null, ma/ten rong, ngay sai dinh dang dd/MM/yyyy, suc chua <= 0
     *   -> InvalidInputException.
     * - Ma da ton tai (khong phan biet hoa/thuong) -> DuplicateEventException.
     */
    public void addEvent(Event event) throws DuplicateEventException, InvalidInputException {
        if (event == null) {
            throw new InvalidInputException("Thông tin sự kiện không được để trống.");
        }
        InputValidator.requireNotBlank("Mã sự kiện", event.getEventId());
        InputValidator.requireNotBlank("Tên sự kiện", event.getEventName());
        InputValidator.requireDate("Ngày tổ chức", event.getDate());
        if (event.getMaxParticipants() <= 0) {
            throw new InvalidInputException("Số người tối đa phải lớn hơn 0.");
        }
        if (eventRepository.existsById(event.getEventId())) {
            throw new DuplicateEventException(
                    "Mã sự kiện \"" + event.getEventId() + "\" đã tồn tại.");
        }
        eventRepository.insert(event);
    }

    /** Doi trang thai su kien. Ma khong ton tai -> EventNotFoundException. */
    public void updateEventStatus(String eventId, EventStatus status)
            throws EventNotFoundException, InvalidInputException {
        if (status == null) {
            throw new InvalidInputException("Trạng thái không được để trống.");
        }
        boolean updated = eventRepository.updateStatus(eventId, status);
        if (!updated) {
            throw new EventNotFoundException("Không tìm thấy sự kiện có mã: " + eventId);
        }
    }

    /**
     * Dang ky thanh vien vao su kien.
     * Thu tu kiem tra: su kien ton tai -> dang UPCOMING -> thanh vien ton tai ->
     * da dang ky chua -> con cho khong.
     *
     * @return true neu dang ky moi thanh cong, false neu thanh vien da dang ky tu truoc
     *         (khong them trung).
     */
    public boolean registerMember(String eventId, Member member)
            throws EventNotFoundException, EventFullException,
            MemberNotFoundException, InvalidInputException {
        if (member == null) {
            throw new InvalidInputException("Chưa chọn thành viên để đăng ký.");
        }
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new EventNotFoundException("Không tìm thấy sự kiện có mã: " + eventId);
        }
        if (event.getStatus() != EventStatus.UPCOMING) {
            throw new InvalidInputException("Chỉ có thể đăng ký vào sự kiện đang ở trạng thái \"Sắp diễn ra\".");
        }
        if (!eventRepository.memberExists(member.getId())) {
            throw new MemberNotFoundException("Không tìm thấy thành viên có mã: " + member.getId());
        }
        if (event.hasParticipant(member.getId())) {
            return false;
        }
        if (event.isFull()) {
            throw new EventFullException("Sự kiện đã đủ số lượng người đăng ký tối đa ("
                    + event.getMaxParticipants() + "), không thể đăng ký thêm.");
        }
        eventRepository.addParticipant(event.getEventId(), member.getId());
        return true;
    }

    /**
     * Huy dang ky.
     *
     * @return true neu da huy, false neu thanh vien vua khong dang ky su kien do.
     */
    public boolean cancelRegistration(String eventId, String memberId) {
        return eventRepository.removeParticipant(eventId, memberId);
    }

    /** Toan bo su kien (moi lan goi tra ve danh sach moi doc tu database). */
    public List<Event> listEvents() {
        return eventRepository.findAll();
    }

    /** Loc theo trang thai; status null -> tat ca. */
    public List<Event> listEventsByStatus(EventStatus status) {
        if (status == null) {
            return eventRepository.findAll();
        }
        return eventRepository.findByStatus(status);
    }

    /** Tim theo ma; tra ve null neu khong co. */
    public Event findEventById(String eventId) {
        if (eventId == null) {
            return null;
        }
        return eventRepository.findById(eventId.trim());
    }
}
