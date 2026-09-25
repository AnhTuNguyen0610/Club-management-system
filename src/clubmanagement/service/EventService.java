package clubmanagement.service;

import clubmanagement.exception.EventFullException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.DuplicateEventException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly toan bo nghiep vu lien quan den Event: tao su kien, dang ky
 * thanh vien, huy dang ky, liet ke su kien.
 *
 * MODULE: NHAT MINH
 * Chi duoc sua trong file nay va Event/Workshop/Competition/SocialEvent.java.
 * Khong sua cac file thuoc module cua Bien (Member) de tranh git conflict.
 */
public class EventService {

    private List<Event> events;

    public EventService() {
        this.events = new ArrayList<>();
    }

    /**
     * Them mot su kien moi vao he thong.
     * Business rules:
     * - Neu eventId da ton tai trong danh sach -> khong them, co the
     * in canh bao hoac throw exception phu hop (co the tu tao them
     * DuplicateEventException tuong tu DuplicateMemberException neu can).
     * - Nguoc lai them event vao danh sach.
     */
    public void addEvent(Event event) throws DuplicateEventException {
        if (findEventById(event.getEventId()) != null) {
            throw new DuplicateEventException("Event " + event.getEventName() + " da ton tai!");
        }

        events.add(event);
    }

    /**
     * Dang ky mot thanh vien tham gia su kien.
     * Business rules:
     * - Tim event theo eventId; neu khong ton tai -> throw MemberNotFoundException
     * (hoac tao rieng EventNotFoundException neu nhom muon ro rang hon).
     * - Neu participants.size() >= maxParticipants -> throw EventFullException.
     * - Neu member da co trong danh sach participants -> khong them trung.
     * - Nguoc lai them member vao participants cua event.
     */
    public void registerMember(String eventId, Member member) throws EventFullException, EventNotFoundException {
        Event event = findEventById(eventId);

        if (event == null)
            throw new EventNotFoundException("Event khong ton tai!");

        if (event.getParticipants().size() >= event.getMaxParticipants())
            throw new EventFullException("Event da dat so luong nguoi dang ki toi da, khong the dang ki them!");

        boolean isExist = false;
        for (Member m : event.getParticipants()) {
            if (m.getId().equals(member.getId())) {
                isExist = true;
                break;
            }
        }

        if (!isExist)
            event.getParticipants().add(member);
    }

    /**
     * Huy dang ky cua mot thanh vien khoi su kien.
     * Neu event hoac member khong ton tai trong danh sach -> bo qua (khong throw).
     */
    public void cancelRegistration(String eventId, String memberId) {
        Event event = findEventById(eventId);

        if (event != null) {
            event.getParticipants().removeIf(m -> m.getId().equals(memberId));
        }
    }

    /**
     * Tra ve danh sach toan bo su kien (nen tra ve ban sao de bao ve
     * encapsulation).
     */
    public List<Event> listEvents() {
        return new ArrayList<>(events);
    }

    /**
     * Loc danh sach su kien theo trang thai (status).
     */
    public List<Event> listEventsByStatus(EventStatus status) {
        List<Event> filteredEvent = new ArrayList<>();

        for (Event event : events) {
            if (event.getStatus() == status)
                filteredEvent.add(event);
        }

        return filteredEvent;
    }

    /**
     * Tim kiem su kien theo id, tra ve null neu khong tim thay.
     */
    public Event findEventById(String eventId) {
        for (Event event : events) {
            if (event.getEventId() == eventId)
                return event;
        }
        return null;
    }
}
