package clubmanagement.service;

import clubmanagement.exception.EventFullException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.DuplicateEventException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.repository.EventRepository;

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

    // TODO: NHAT MINH (Giai doan 2 - Task N2.2)
    // Hien tai du lieu van luu trong List (in-memory) nen ban Console cu van chay.
    // Khi chuyen sang database: thay MOI thao tac tren "events" (va participants)
    // bang
    // eventRepository, roi XOA field "events". Giu nguyen chu ky cac method public
    // va
    // giu nguyen business rule/exception da co.

    private final EventRepository eventRepository;

    /**
     * Anh Tu (Tech Lead) da chot constructor nay: Main truyen EventRepository vao.
     * Nhat Minh KHONG doi chu ky constructor (neu doi se lam Main.java khong bien
     * dich duoc).
     */
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * TODO: NHAT MINH (Giai doan 2 - Task N2.2) - METHOD MOI
     * Doi trang thai mot su kien va LUU xuong database.
     * Ly do can method nay: khi du lieu nam trong DB, findEventById() tra ve mot
     * ban sao
     * moi, nen goi event.setStatus(...) ben ngoai khong con duoc luu lai.
     * Business rules:
     * - eventId khong ton tai -> throw EventNotFoundException.
     * - Cap nhat status qua eventRepository.updateStatus(...).
     * Sau khi Nhat Minh xong, Anh Tu se doi ConsoleUI/EventPanel dung method nay.
     */
    public void updateEventStatus(String eventId, EventStatus status) throws EventNotFoundException {
        boolean updated = eventRepository.updateStatus(eventId, status);
        if (!updated) {
            throw new EventNotFoundException("Event " + eventId + " khong ton tai!");
        }
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
        if (eventRepository.existsById(event.getEventId())) {
            throw new DuplicateEventException("Event " + event.getEventName() + " da ton tai!");
        }
        eventRepository.insert(event);
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
        Event event = eventRepository.findById(eventId);

        if (event == null)
            throw new EventNotFoundException("Event khong ton tai!");

        if (event.getParticipants().size() >= event.getMaxParticipants())
            throw new EventFullException("Event da dat so luong nguoi dang ki toi da, khong the dang ki them!");

        boolean isExist = false;
        for (Member m : event.getParticipants()) {
            if (m.getId().equalsIgnoreCase(member.getId())) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            eventRepository.addParticipant(eventId, member.getId());
        }
    }

    /**
     * Huy dang ky cua mot thanh vien khoi su kien.
     * Neu event hoac member khong ton tai trong danh sach -> bo qua (khong throw).
     */
    public void cancelRegistration(String eventId, String memberId) {
        eventRepository.removeParticipant(eventId, memberId);
    }

    /**
     * Tra ve danh sach toan bo su kien (nen tra ve ban sao de bao ve
     * encapsulation).
     */
    public List<Event> listEvents() {
        return eventRepository.findAll();
    }

    /**
     * Loc danh sach su kien theo trang thai (status).
     */
    public List<Event> listEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    /**
     * Tim kiem su kien theo id, tra ve null neu khong tim thay.
     */
    public Event findEventById(String eventId) {
        return eventRepository.findById(eventId);
    }
}
