package clubmanagement.ui;

import clubmanagement.exception.DuplicateEventException;
import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.EventFullException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Competition;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.model.MembershipType;
import clubmanagement.model.SocialEvent;
import clubmanagement.model.Workshop;
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
 * Duoc quan ly boi Tech Lead (Anh Tu). Da noi day du cac chuc nang cua
 * MemberService (Bien) va EventService (Nhat Minh) vao menu - ban demo
 * "khung dau tien" da hoan chinh.
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
        System.out.println("5. Tim thanh vien theo ten");
        System.out.println("6. Cap nhat email/SDT thanh vien");
        System.out.println("7. Sap xep thanh vien theo ten");
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
            case "5":
                searchMemberByNameFlow();
                break;
            case "6":
                updateMemberFlow();
                break;
            case "7":
                sortMembersFlow();
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

    private void searchMemberByNameFlow() {
        System.out.print("Nhap ten (hoac mot phan ten) can tim: ");
        String keyword = scanner.nextLine().trim();
        List<Member> result = memberService.searchMemberByName(keyword);
        if (result == null || result.isEmpty()) {
            System.out.println("Khong tim thay thanh vien nao phu hop.");
            return;
        }
        for (Member m : result) {
            System.out.println(m);
        }
    }

    private void updateMemberFlow() {
        System.out.print("Nhap ID thanh vien can cap nhat: ");
        String id = scanner.nextLine().trim();
        System.out.print("Email moi: ");
        String email = scanner.nextLine().trim();
        System.out.print("SDT moi: ");
        String phone = scanner.nextLine().trim();

        try {
            memberService.updateMember(id, email, phone);
            System.out.println("Cap nhat thanh cong!");
        } catch (MemberNotFoundException | InvalidInputException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void sortMembersFlow() {
        List<Member> sorted = memberService.sortMembersByName();
        if (sorted == null || sorted.isEmpty()) {
            System.out.println("Chua co thanh vien nao.");
            return;
        }
        for (Member m : sorted) {
            System.out.println(m);
        }
    }

    // ---------- EVENT MENU ----------

    private void eventMenu() {
        System.out.println();
        System.out.println("--- QUAN LY SU KIEN ---");
        System.out.println("1. Liet ke tat ca su kien");
        System.out.println("2. Dang ky thanh vien vao su kien");
        System.out.println("3. Them su kien moi");
        System.out.println("4. Huy dang ky thanh vien khoi su kien");
        System.out.println("5. Loc su kien theo trang thai");
        System.out.println("6. Cap nhat trang thai su kien");
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
            case "3":
                addEventFlow();
                break;
            case "4":
                cancelRegistrationFlow();
                break;
            case "5":
                filterEventsByStatusFlow();
                break;
            case "6":
                changeEventStatusFlow();
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
            if (eventService.registerMember(eventId, member)) {
                System.out.println("Dang ky thanh cong!");
            } else {
                System.out.println("Thanh vien da dang ky su kien nay roi.");
            }
        } catch (MemberNotFoundException | EventFullException | EventNotFoundException
                | InvalidInputException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void cancelRegistrationFlow() {
        System.out.print("Nhap ID su kien: ");
        String eventId = scanner.nextLine().trim();
        System.out.print("Nhap ID thanh vien can huy dang ky: ");
        String memberId = scanner.nextLine().trim();

        if (eventService.cancelRegistration(eventId, memberId)) {
            System.out.println("Da huy dang ky.");
        } else {
            System.out.println("Thanh vien khong co trong danh sach dang ky cua su kien nay.");
        }
    }

    private void filterEventsByStatusFlow() {
        System.out.println("Chon trang thai: 1. UPCOMING | 2. ONGOING | 3. FINISHED | 4. CANCELLED");
        String choice = scanner.nextLine().trim();
        EventStatus status = parseEventStatus(choice);
        if (status == null) {
            System.out.println("Lua chon khong hop le.");
            return;
        }

        List<Event> filtered = eventService.listEventsByStatus(status);
        if (filtered == null || filtered.isEmpty()) {
            System.out.println("Khong co su kien nao o trang thai " + status + ".");
            return;
        }
        for (Event e : filtered) {
            System.out.println(e + " - Phi: " + e.calculateFee());
        }
    }

    private void changeEventStatusFlow() {
        System.out.print("Nhap ID su kien can cap nhat trang thai: ");
        String eventId = scanner.nextLine().trim();
        Event event = eventService.findEventById(eventId);
        if (event == null) {
            System.out.println("Loi: Khong tim thay su kien voi ID nay.");
            return;
        }

        System.out.println("Chon trang thai moi: 1. UPCOMING | 2. ONGOING | 3. FINISHED | 4. CANCELLED");
        String choice = scanner.nextLine().trim();
        EventStatus status = parseEventStatus(choice);
        if (status == null) {
            System.out.println("Lua chon khong hop le.");
            return;
        }

        event.setStatus(status);
        System.out.println("Cap nhat trang thai thanh cong!");
    }

    private EventStatus parseEventStatus(String choice) {
        switch (choice) {
            case "1":
                return EventStatus.UPCOMING;
            case "2":
                return EventStatus.ONGOING;
            case "3":
                return EventStatus.FINISHED;
            case "4":
                return EventStatus.CANCELLED;
            default:
                return null;
        }
    }

    private void addEventFlow() {
        System.out.println();
        System.out.println("--- THEM SU KIEN MOI ---");
        System.out.print("Nhap ID su kien: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nhap ten su kien: ");
        String name = scanner.nextLine().trim();
        System.out.print("Nhap ngay (dd/MM/yyyy): ");
        String date = scanner.nextLine().trim();
        
        System.out.print("Nhap so luong toi da: ");
        int max = 0;
        try {
            max = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Loi: So luong phai la so nguyen!");
            return;
        }

        System.out.println("Chon loai su kien:");
        System.out.println("1. Workshop");
        System.out.println("2. Cuoc thi (Competition)");
        System.out.println("3. Giao luu (Social Event)");
        System.out.print("Lua chon: ");
        String type = scanner.nextLine().trim();

        Event event = null;
        try {
            switch (type) {
                case "1":
                    System.out.print("Nhap phi co ban (baseFee): ");
                    double baseFee = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Nhap ten dien gia (speaker): ");
                    String speaker = scanner.nextLine().trim();
                    event = new Workshop(id, name, date, max, baseFee, speaker);
                    break;
                case "2":
                    System.out.print("Nhap phi dang ky (entryFee): ");
                    double entryFee = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Nhap gia tri giai thuong (prizeValue): ");
                    double prizeValue = Double.parseDouble(scanner.nextLine().trim());
                    event = new Competition(id, name, date, max, entryFee, prizeValue);
                    break;
                case "3":
                    System.out.print("Nhap dia diem (location): ");
                    String location = scanner.nextLine().trim();
                    event = new SocialEvent(id, name, date, max, location);
                    break;
                default:
                    System.out.println("Loai su kien khong hop le. Huy thao tac!");
                    return;
            }

            eventService.addEvent(event);
            System.out.println("Them su kien thanh cong!");

        } catch (DuplicateEventException | InvalidInputException e) {
            System.out.println("Loi: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Loi: Vui long nhap dung dinh dang so cho tien/phi!");
        }
    }
}
