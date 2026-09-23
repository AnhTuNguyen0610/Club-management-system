package clubmanagement.service;

import clubmanagement.model.Club;
import clubmanagement.model.Event;
import clubmanagement.model.Member;

import java.util.List;

/**
 * Lop dieu phoi (orchestration) o muc cao nhat: giu tham chieu den
 * Club, MemberService, EventService (Composition/Association) va cung
 * cap vai thao tac tong hop don gian.
 *
 * Duoc quan ly boi Tech Lead (Anh Tu) de tranh xung dot voi module cua
 * Bien va Nhat Minh.
 */
public class ClubService {

    private final Club club;
    private final MemberService memberService;
    private final EventService eventService;

    public ClubService(Club club, MemberService memberService, EventService eventService) {
        this.club = club;
        this.memberService = memberService;
        this.eventService = eventService;
    }

    public Club getClub() {
        return club;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public EventService getEventService() {
        return eventService;
    }

    /**
     * In ra thong tin tong quan cua CLB: ten, so luong thanh vien, so luong su kien.
     * Ham nay chi tong hop du lieu tu 2 service, khong chua business logic rieng.
     */
    public void printClubSummary() {
        System.out.println("===== THONG TIN CAU LAC BO =====");
        System.out.println(club);

        List<Member> members = memberService.listAllMembers();
        List<Event> events = eventService.listEvents();

        System.out.println("Tong so thanh vien: " + (members == null ? 0 : members.size()));
        System.out.println("Tong so su kien: " + (events == null ? 0 : events.size()));
    }
}
