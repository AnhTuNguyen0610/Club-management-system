package clubmanagement.service;

import clubmanagement.exception.EventFullException;
import clubmanagement.exception.MemberNotFoundException;
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
     * TODO: NHAT MINH
     * Them mot su kien moi vao he thong.
     * Business rules:
     *  - Neu eventId da ton tai trong danh sach -> khong them, co the
     *    in canh bao hoac throw exception phu hop (co the tu tao them
     *    DuplicateEventException tuong tu DuplicateMemberException neu can).
     *  - Nguoc lai them event vao danh sach.
     */
    public void addEvent(Event event) {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Dang ky mot thanh vien tham gia su kien.
     * Business rules:
     *  - Tim event theo eventId; neu khong ton tai -> throw MemberNotFoundException
     *    (hoac tao rieng EventNotFoundException neu nhom muon ro rang hon).
     *  - Neu participants.size() >= maxParticipants -> throw EventFullException.
     *  - Neu member da co trong danh sach participants -> khong them trung.
     *  - Nguoc lai them member vao participants cua event.
     */
    public void registerMember(String eventId, Member member) throws EventFullException, MemberNotFoundException {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Huy dang ky cua mot thanh vien khoi su kien.
     * Neu event hoac member khong ton tai trong danh sach -> bo qua (khong throw).
     */
    public void cancelRegistration(String eventId, String memberId) {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Tra ve danh sach toan bo su kien (nen tra ve ban sao de bao ve encapsulation).
     */
    public List<Event> listEvents() {
        // TODO: NHAT MINH
        return null;
    }

    /**
     * TODO: NHAT MINH
     * Loc danh sach su kien theo trang thai (status).
     */
    public List<Event> listEventsByStatus(EventStatus status) {
        // TODO: NHAT MINH
        return null;
    }

    /**
     * TODO: NHAT MINH
     * Tim kiem su kien theo id, tra ve null neu khong tim thay.
     */
    public Event findEventById(String eventId) {
        // TODO: NHAT MINH
        return null;
    }
}
