package clubmanagement.ui;

import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.EventFullException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Event;
import clubmanagement.model.Member;
import clubmanagement.model.MembershipType;
import clubmanagement.service.ClubService;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Giao dien dang console don gian, dung Scanner de nhap lieu.
 * Lop nay chi lam nhiem vu hien thi + goi cac Service tuong ung,
 * KHONG chua business logic (business logic nam trong Service).
 *
 * Duoc quan ly boi Tech Lead (Anh Tu). Khi Bien va Nhat Minh hoan thanh Service cua minh,
 * cac chuc nang trong menu se hoat dong day du.
 */
public class ConsoleUI {

    private final Scanner scanner;
    private final ClubService clubService;
    private final MemberService memberService;
    private final EventService eventService;

    public ConsoleUI(ClubService clubService) {
        this.scanner = new Scanner(System.in);
        this.clubService = clubService;
        this.memberService = clubService.getMemberService();
        this.eventService = clubService.getEventService();
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    memberMenu();
                    break;
                case "2":
                    eventMenu();
                    break;
                case "3":
                    clubService.printClubSummary();
                    break;
                case "0":
                    running = false;
                    System.out.println("Tam biet!");
                    break;
                default:
                    System.out.println("Lua chon khong hop le, vui long thu lai.");
            }
        }
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== HE THONG QUAN LY CAU LAC BO =====");
        System.out.println("1. Quan ly thanh vien");
        System.out.println("2. Quan ly su kien");
        System.out.println("3. Xem thong tin tong quan CLB");
        System.out.println("0. Thoat");
        System.out.print("Nhap lua chon: ");
    }

    // ---------- MEMBER MENU ----------

    private void memberMenu() {
        System.out.println();
        System.out.println("--- QUAN LY THANH VIEN ---");
        System.out.println("1. Them thanh vien");
        System.out.println("2. Xoa thanh vien");
        System.out.println("3. Tim thanh vien theo ID");
        System.out.println("4. Liet ke tat ca thanh vien");
        System.out.println("0. Quay lai");
        System.out.print("Nhap lua chon: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                addMemberFlow();
                break;
            case "2":
                removeMemberFlow();
                break;
            case "3":
                searchMemberFlow();
                break;
            case "4":
                listMembersFlow();
                break;
            case "0":
                break;
            default:
                System.out.println("Lua chon khong hop le.");
        }
    }

    private void addMemberFlow() {
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Ten: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("SDT: ");
        String phone = scanner.nextLine().trim();

        Member member = new Member(id, name, email, phone, MembershipType.REGULAR, LocalDate.now());

        try {
            memberService.addMember(member);
            System.out.println("Them thanh vien thanh cong!");
        } catch (DuplicateMemberException | InvalidInputException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void removeMemberFlow() {
        System.out.print("Nhap ID thanh vien can xoa: ");
        String id = scanner.nextLine().trim();
        try {
            memberService.removeMember(id);
            System.out.println("Xoa thanh vien thanh cong!");
        } catch (MemberNotFoundException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void searchMemberFlow() {
        System.out.print("Nhap ID thanh vien can tim: ");
        String id = scanner.nextLine().trim();
        try {
            Member member = memberService.searchMemberById(id);
            System.out.println(member);
        } catch (MemberNotFoundException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void listMembersFlow() {
        List<Member> members = memberService.listAllMembers();
        if (members == null || members.isEmpty()) {
            System.out.println("Chua co thanh vien nao.");
            return;
        }
        for (Member m : members) {
            System.out.println(m);
        }
    }

    // ---------- EVENT MENU ----------

    private void eventMenu() {
        System.out.println();
        System.out.println("--- QUAN LY SU KIEN ---");
        System.out.println("1. Liet ke tat ca su kien");
        System.out.println("2. Dang ky thanh vien vao su kien");
        System.out.println("0. Quay lai");
        System.out.print("Nhap lua chon: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                listEventsFlow();
                break;
            case "2":
                registerEventFlow();
                break;
            case "0":
                break;
            default:
                System.out.println("Lua chon khong hop le.");
        }
    }

    private void listEventsFlow() {
        List<Event> events = eventService.listEvents();
        if (events == null || events.isEmpty()) {
            System.out.println("Chua co su kien nao.");
            return;
        }
        for (Event e : events) {
            System.out.println(e + " - Phi: " + e.calculateFee());
        }
    }

    private void registerEventFlow() {
        System.out.print("Nhap ID su kien: ");
        String eventId = scanner.nextLine().trim();
        System.out.print("Nhap ID thanh vien: ");
        String memberId = scanner.nextLine().trim();

        try {
            Member member = memberService.searchMemberById(memberId);
            eventService.registerMember(eventId, member);
            System.out.println("Dang ky thanh cong!");
        } catch (MemberNotFoundException | EventFullException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
