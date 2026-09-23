package clubmanagement;

import clubmanagement.model.Club;
import clubmanagement.service.ClubService;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;
import clubmanagement.ui.ConsoleUI;

/**
 * Diem khoi chay chuong trinh.
 * Chi lam nhiem vu khoi tao cac doi tuong va chay ConsoleUI.
 * Duoc quan ly boi Tech Lead (Anh Tu) - Bien va Nhat Minh khong can sua file nay.
 */
public class Main {

    public static void main(String[] args) {
        Club club = new Club("CLB Lap Trinh", "Cau lac bo danh cho sinh vien yeu thich lap trinh");

        MemberService memberService = new MemberService();
        EventService eventService = new EventService();
        ClubService clubService = new ClubService(club, memberService, eventService);

        ConsoleUI ui = new ConsoleUI(clubService);
        ui.run();
    }
}
