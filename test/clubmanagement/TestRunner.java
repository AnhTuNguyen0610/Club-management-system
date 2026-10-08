package clubmanagement;

import clubmanagement.exception.DatabaseException;
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
import clubmanagement.repository.DatabaseConnection;
import clubmanagement.repository.EventRepository;
import clubmanagement.repository.MemberRepository;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Bo test tu viet (khong can JUnit, chay bang: ./test.sh hoac test.bat).
 * Moi test dung mot file database tam rieng nen khong dung toi data/club.db.
 */
public class TestRunner {

    private interface TestBody {
        void run() throws Exception;
    }

    private static int passed = 0;
    private static final List<String> failures = new ArrayList<>();
    private static Path tempDir;

    // ---------- moi truong test ----------

    private static class Env {
        final String dbFile = tempDir.resolve("sub/dir/t" + System.nanoTime() + ".db").toString();
        DatabaseConnection db = new DatabaseConnection(dbFile);
        MemberService members;
        EventService events;

        Env() {
            db.initSchema();
            wire();
        }

        void wire() {
            members = new MemberService(new MemberRepository(db));
            events = new EventService(new EventRepository(db));
        }

        /** Gia lap tat/mo lai chuong trinh: tao moi ket noi + repository + service. */
        void restart() {
            db = new DatabaseConnection(dbFile);
            db.initSchema();
            wire();
        }
    }

    private static Member member(String id, MembershipType type) {
        return new Member(id, "Nguyễn Văn " + id, id.toLowerCase() + "@gmail.com", "0901234567", type, LocalDate.of(2026, 1, 15));
    }

    private static Member regular(String id) {
        return member(id, MembershipType.REGULAR);
    }

    // ---------- khung chay ----------

    private static void test(String name, TestBody body) {
        try {
            body.run();
            passed++;
            System.out.println("  [PASS] " + name);
        } catch (Throwable t) {
            failures.add(name + " -> " + t);
            System.out.println("  [FAIL] " + name + " -> " + t);
        }
    }

    private static void check(boolean cond, String msg) {
        if (!cond) {
            throw new AssertionError(msg);
        }
    }

    private static void eq(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(msg + " (mong doi: " + expected + ", thuc te: " + actual + ")");
        }
    }

    private static void expect(Class<? extends Throwable> type, String msg, TestBody body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return;
            }
            throw new AssertionError(msg + ": mong doi " + type.getSimpleName() + " nhung nhan " + t);
        }
        throw new AssertionError(msg + ": mong doi " + type.getSimpleName() + " nhung khong co exception");
    }

    // ---------- main ----------

    public static void main(String[] args) throws Exception {
        tempDir = Files.createTempDirectory("clubtest");
        try {
            System.out.println("== Member ==");
            memberTests();
            System.out.println("== Event ==");
            eventTests();
            System.out.println("== Database / Persistence ==");
            databaseTests();
            System.out.println("== Edge cases ==");
            edgeTests();
        } finally {
            deleteRecursively(tempDir);
        }
        System.out.println();
        System.out.println("Ket qua: " + passed + " PASS, " + failures.size() + " FAIL");
        for (String f : failures) {
            System.out.println("  - " + f);
        }
        System.exit(failures.isEmpty() ? 0 : 1);
    }

    // ---------- Member ----------

    private static void memberTests() {
        test("Them + doc thanh vien", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            Member m = e.members.searchMemberById("M01");
            eq("Nguyễn Văn M01", m.getName(), "ten");
            eq(MembershipType.REGULAR, m.getMembershipType(), "loai");
            eq(LocalDate.of(2026, 1, 15), m.getJoinDate(), "ngay tham gia");
            check(m.isActive(), "active");
        });
        test("Danh sach rong khi chua co du lieu", () -> {
            Env e = new Env();
            check(e.members.listAllMembers().isEmpty(), "list rong");
            check(e.members.sortMembersByName().isEmpty(), "sort rong");
            check(e.members.searchMemberByName("a").isEmpty(), "search rong");
        });
        test("Trung ID (ke ca khac hoa/thuong) bi chan", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            expect(DuplicateMemberException.class, "trung y het", () -> e.members.addMember(regular("M01")));
            expect(DuplicateMemberException.class, "khac hoa thuong", () -> e.members.addMember(regular("m01")));
            eq(1, e.members.listAllMembers().size(), "chi con 1");
        });
        test("Input khong hop le bi chan", () -> {
            Env e = new Env();
            expect(InvalidInputException.class, "null", () -> e.members.addMember(null));
            expect(InvalidInputException.class, "id rong", () -> e.members.addMember(regular("  ")));
            Member noName = regular("M02");
            noName.setName("   ");
            expect(InvalidInputException.class, "ten rong", () -> e.members.addMember(noName));
            Member badMail = regular("M03");
            badMail.setEmail("abc");
            expect(InvalidInputException.class, "email sai", () -> e.members.addMember(badMail));
            Member badPhone = regular("M04");
            badPhone.setPhone("12ab");
            expect(InvalidInputException.class, "sdt sai", () -> e.members.addMember(badPhone));
            Member noType = new Member("M05", "A", "a@b.co", "", null, LocalDate.now());
            expect(InvalidInputException.class, "thieu loai", () -> e.members.addMember(noType));
            eq(0, e.members.listAllMembers().size(), "khong co gi duoc luu");
        });
        test("SDT de trong hop le; ID/ten duoc trim", () -> {
            Env e = new Env();
            Member m = new Member("  M09 ", "  Lan  ", "lan@x.vn", "", MembershipType.VIP, LocalDate.now());
            e.members.addMember(m);
            eq("Lan", e.members.searchMemberById("M09").getName(), "ten da trim");
        });
        test("Tim theo ID khong phan biet hoa/thuong; khong ton tai -> NotFound", () -> {
            Env e = new Env();
            e.members.addMember(regular("ABC"));
            eq("ABC", e.members.searchMemberById("abc").getId(), "tim abc");
            expect(MemberNotFoundException.class, "khong co", () -> e.members.searchMemberById("zzz"));
            expect(MemberNotFoundException.class, "null", () -> e.members.searchMemberById(null));
        });
        test("Tim theo ten gan dung, khong phan biet hoa/thuong, co dau", () -> {
            Env e = new Env();
            Member a = regular("M01");
            a.setName("Nguyễn Văn An");
            Member b = regular("M02");
            b.setName("Trần Anh");
            Member c = regular("M03");
            c.setName("Lê Bình");
            e.members.addMember(a);
            e.members.addMember(b);
            e.members.addMember(c);
            eq(2, e.members.searchMemberByName("AN").size(), "an");
            eq(1, e.members.searchMemberByName("bình").size(), "co dau");
            eq(0, e.members.searchMemberByName("zzz").size(), "khong co");
            eq(0, e.members.searchMemberByName("   ").size(), "rong");
        });
        test("Sap xep theo ten", () -> {
            Env e = new Env();
            Member a = regular("M01");
            a.setName("Zed");
            Member b = regular("M02");
            b.setName("anna");
            e.members.addMember(a);
            e.members.addMember(b);
            eq("anna", e.members.sortMembersByName().get(0).getName(), "anna dung dau");
        });
        test("Cap nhat email/SDT hop le", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.updateMember("M01", "moi@x.vn", "0988777666");
            Member m = e.members.searchMemberById("M01");
            eq("moi@x.vn", m.getEmail(), "email");
            eq("0988777666", m.getPhone(), "phone");
        });
        test("Cap nhat sai -> InvalidInput; khong ton tai -> NotFound", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            expect(InvalidInputException.class, "email sai", () -> e.members.updateMember("M01", "sai", "0988777666"));
            expect(InvalidInputException.class, "sdt sai", () -> e.members.updateMember("M01", "a@b.co", "abc"));
            expect(MemberNotFoundException.class, "khong co", () -> e.members.updateMember("X", "a@b.co", ""));
            eq("m01@gmail.com", e.members.searchMemberById("M01").getEmail(), "du lieu cu khong doi");
        });
        test("Xoa thanh vien; xoa lai -> NotFound", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.removeMember("M01");
            eq(0, e.members.listAllMembers().size(), "da xoa");
            expect(MemberNotFoundException.class, "xoa lai", () -> e.members.removeMember("M01"));
            expect(MemberNotFoundException.class, "null", () -> e.members.removeMember(null));
        });
        test("Chong SQL injection trong tim kiem / them", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            Member evil = regular("M02");
            evil.setName("Robert'); DROP TABLE members;--");
            e.members.addMember(evil);
            eq(2, e.members.listAllMembers().size(), "bang van con");
            expect(MemberNotFoundException.class, "id doc", () -> e.members.searchMemberById("' OR '1'='1"));
        });
    }

    // ---------- Event ----------

    private static void eventTests() {
        test("Tao 3 loai su kien, doc lai dung lop con va thuoc tinh rieng", () -> {
            Env e = new Env();
            e.events.addEvent(new Workshop("W1", "Java", "20/11/2026", 10, 100000, "Anh Tú"));
            e.events.addEvent(new Competition("C1", "Hackathon", "21/11/2026", 10, 50000, 1000000));
            e.events.addEvent(new SocialEvent("S1", "Picnic", "22/11/2026", 10, "Công viên"));
            check(e.events.findEventById("W1") instanceof Workshop, "Workshop");
            check(e.events.findEventById("C1") instanceof Competition, "Competition");
            check(e.events.findEventById("S1") instanceof SocialEvent, "SocialEvent");
            eq("Anh Tú", ((Workshop) e.events.findEventById("W1")).getSpeaker(), "speaker");
            eq(1000000.0, ((Competition) e.events.findEventById("C1")).getPrizeValue(), "prize");
            eq("Công viên", ((SocialEvent) e.events.findEventById("S1")).getLocation(), "location");
            eq(3, e.events.listEvents().size(), "3 su kien");
            eq("W1", e.events.listEvents().get(0).getEventId(), "thu tu them");
        });
        test("Trung ma su kien (ke ca khac hoa/thuong) bi chan", () -> {
            Env e = new Env();
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            expect(DuplicateEventException.class, "trung", () -> e.events.addEvent(new SocialEvent("S1", "B", "22/11/2026", 10, "y")));
            expect(DuplicateEventException.class, "khac hoa thuong", () -> e.events.addEvent(new SocialEvent("s1", "B", "22/11/2026", 10, "y")));
            eq(1, e.events.listEvents().size(), "con 1");
        });
        test("Validate su kien: null, id/ten rong, ngay sai, suc chua <= 0", () -> {
            Env e = new Env();
            expect(InvalidInputException.class, "null", () -> e.events.addEvent(null));
            expect(InvalidInputException.class, "id rong", () -> e.events.addEvent(new SocialEvent(" ", "A", "22/11/2026", 10, "x")));
            expect(InvalidInputException.class, "ten rong", () -> e.events.addEvent(new SocialEvent("S1", "", "22/11/2026", 10, "x")));
            expect(InvalidInputException.class, "ngay sai", () -> e.events.addEvent(new SocialEvent("S1", "A", "31/02/2026", 10, "x")));
            expect(InvalidInputException.class, "ngay sai dinh dang", () -> e.events.addEvent(new SocialEvent("S1", "A", "2026-11-20", 10, "x")));
            expect(InvalidInputException.class, "capacity 0", () -> e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 0, "x")));
            expect(InvalidInputException.class, "capacity am", () -> e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", -5, "x")));
            eq(0, e.events.listEvents().size(), "khong luu gi");
        });
        test("Capacity rat lon van luu duoc", () -> {
            Env e = new Env();
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", Integer.MAX_VALUE, "x"));
            eq(Integer.MAX_VALUE, e.events.findEventById("S1").getMaxParticipants(), "max int");
        });
        test("Doi trang thai + loc theo trang thai", () -> {
            Env e = new Env();
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            e.events.addEvent(new SocialEvent("S2", "B", "22/11/2026", 10, "x"));
            eq(2, e.events.listEventsByStatus(EventStatus.UPCOMING).size(), "2 upcoming");
            e.events.updateEventStatus("S1", EventStatus.FINISHED);
            eq(EventStatus.FINISHED, e.events.findEventById("S1").getStatus(), "da doi");
            eq(1, e.events.listEventsByStatus(EventStatus.UPCOMING).size(), "1 upcoming");
            eq(1, e.events.listEventsByStatus(EventStatus.FINISHED).size(), "1 finished");
            eq(0, e.events.listEventsByStatus(EventStatus.CANCELLED).size(), "0 cancelled");
            expect(EventNotFoundException.class, "khong ton tai", () -> e.events.updateEventStatus("zz", EventStatus.ONGOING));
            expect(InvalidInputException.class, "null status", () -> e.events.updateEventStatus("S1", null));
        });
        test("Tim su kien khong ton tai -> null", () -> {
            Env e = new Env();
            check(e.events.findEventById("nope") == null, "null");
            check(e.events.findEventById(null) == null, "null input");
        });
        test("Dang ky thanh cong, liet ke nguoi tham gia", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.addMember(regular("M02"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            check(e.events.registerMember("S1", e.members.searchMemberById("M01")), "dang ky 1");
            check(e.events.registerMember("S1", e.members.searchMemberById("M02")), "dang ky 2");
            Event ev = e.events.findEventById("S1");
            eq(2, ev.getParticipants().size(), "2 nguoi");
            eq("M01", ev.getParticipants().get(0).getId(), "thu tu dang ky");
        });
        test("Dang ky trung khong them lan 2 (ke ca khac hoa/thuong)", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            Member m = e.members.searchMemberById("M01");
            check(e.events.registerMember("S1", m), "lan 1");
            check(!e.events.registerMember("S1", m), "lan 2 bi tu choi");
            check(!e.events.registerMember("s1", m), "khac hoa thuong su kien");
            eq(1, e.events.findEventById("S1").getParticipants().size(), "chi 1");
        });
        test("Su kien day -> EventFullException; nguoi da dang ky van bao 'trung' chu khong bao day", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.addMember(regular("M02"));
            e.members.addMember(regular("M03"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 2, "x"));
            e.events.registerMember("S1", e.members.searchMemberById("M01"));
            e.events.registerMember("S1", e.members.searchMemberById("M02"));
            expect(EventFullException.class, "day", () -> e.events.registerMember("S1", e.members.searchMemberById("M03")));
            check(!e.events.registerMember("S1", e.members.searchMemberById("M01")), "da dang ky -> false");
            eq(2, e.events.findEventById("S1").getParticipants().size(), "van 2");
        });
        test("Capacity = 1: nguoi thu 2 bi chan, huy roi dang ky lai duoc", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.addMember(regular("M02"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 1, "x"));
            e.events.registerMember("S1", e.members.searchMemberById("M01"));
            expect(EventFullException.class, "day", () -> e.events.registerMember("S1", e.members.searchMemberById("M02")));
            check(e.events.cancelRegistration("S1", "M01"), "huy");
            check(e.events.registerMember("S1", e.members.searchMemberById("M02")), "dang ky lai");
        });
        test("Dang ky: su kien/thanh vien khong ton tai, su kien khong con UPCOMING", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 5, "x"));
            Member m = e.members.searchMemberById("M01");
            expect(EventNotFoundException.class, "su kien khong co", () -> e.events.registerMember("zz", m));
            expect(MemberNotFoundException.class, "thanh vien khong co", () -> e.events.registerMember("S1", regular("GHOST")));
            expect(InvalidInputException.class, "member null", () -> e.events.registerMember("S1", null));
            e.events.updateEventStatus("S1", EventStatus.CANCELLED);
            expect(InvalidInputException.class, "da huy", () -> e.events.registerMember("S1", m));
            e.events.updateEventStatus("S1", EventStatus.FINISHED);
            expect(InvalidInputException.class, "da ket thuc", () -> e.events.registerMember("S1", m));
            e.events.updateEventStatus("S1", EventStatus.ONGOING);
            expect(InvalidInputException.class, "dang dien ra", () -> e.events.registerMember("S1", m));
        });
        test("Huy dang ky: thanh cong / khong ton tai tra ve false", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 5, "x"));
            e.events.registerMember("S1", e.members.searchMemberById("M01"));
            check(e.events.cancelRegistration("S1", "m01"), "huy duoc (khac hoa/thuong)");
            check(!e.events.cancelRegistration("S1", "M01"), "huy lan 2");
            check(!e.events.cancelRegistration("zz", "M01"), "su kien khong co");
            check(!e.events.cancelRegistration("S1", "ghost"), "thanh vien khong co");
            eq(0, e.events.findEventById("S1").getParticipants().size(), "rong");
        });
        test("Phi Workshop: early bird (<=20% suat) giam 10%, sau do phi day du", () -> {
            Env e = new Env();
            e.events.addEvent(new Workshop("W1", "Java", "20/11/2026", 5, 100000, "A"));
            eq(90000.0, e.events.findEventById("W1").calculateFee(), 0.001, "0/5 -> giam");
            for (int i = 1; i <= 5; i++) {
                e.members.addMember(regular("M0" + i));
            }
            e.events.registerMember("W1", e.members.searchMemberById("M01"));
            eq(90000.0, e.events.findEventById("W1").calculateFee(), 0.001, "1/5 = 20% -> van giam");
            e.events.registerMember("W1", e.members.searchMemberById("M02"));
            eq(100000.0, e.events.findEventById("W1").calculateFee(), 0.001, "2/5 = 40% -> day du");
        });
        test("Phi Competition: giam theo loai thanh vien; SocialEvent mien phi", () -> {
            Competition c = new Competition("C1", "Cuoc thi", "20/11/2026", 5, 100000, 0);
            eq(100000.0, c.calculateFee(), 0.001, "co ban");
            eq(100000.0, c.calculateFee(member("A", MembershipType.REGULAR)), 0.001, "REGULAR");
            eq(80000.0, c.calculateFee(member("B", MembershipType.VIP)), 0.001, "VIP -20%");
            eq(50000.0, c.calculateFee(member("C", MembershipType.HONORARY)), 0.001, "HONORARY -50%");
            eq(0.0, new SocialEvent("S1", "x", "20/11/2026", 5, "y").calculateFee(), 0.001, "mien phi");
        });
        test("Da hinh: calculateFee goi qua kieu Event", () -> {
            List<Event> list = new ArrayList<>();
            list.add(new Workshop("W", "a", "20/11/2026", 10, 100, "x"));
            list.add(new Competition("C", "b", "20/11/2026", 10, 200, 0));
            list.add(new SocialEvent("S", "c", "20/11/2026", 10, "y"));
            eq(90.0, list.get(0).calculateFee(), 0.001, "workshop");
            eq(200.0, list.get(1).calculateFee(), 0.001, "competition");
            eq(0.0, list.get(2).calculateFee(), 0.001, "social");
            eq("Workshop", list.get(0).getEventTypeDescription(), "mo ta");
        });
        test("Danh sach participants khong sua duoc tu ben ngoai (encapsulation)", () -> {
            Event ev = new SocialEvent("S1", "A", "22/11/2026", 5, "x");
            expect(UnsupportedOperationException.class, "unmodifiable", () -> ev.getParticipants().add(regular("M01")));
        });
        test("equals/hashCode theo ID khong phan biet hoa/thuong", () -> {
            eq(regular("abc"), regular("ABC"), "member equals");
            eq(regular("abc").hashCode(), regular("ABC").hashCode(), "member hash");
            eq(new SocialEvent("s1", "a", "20/11/2026", 1, "x"), new SocialEvent("S1", "b", "21/11/2026", 2, "y"), "event equals");
        });
    }

    private static void eq(double expected, double actual, double delta, String msg) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(msg + " (mong doi: " + expected + ", thuc te: " + actual + ")");
        }
    }

    // ---------- Database ----------

    private static void databaseTests() {
        test("Khoi tao tu thu muc chua ton tai + goi initSchema nhieu lan an toan", () -> {
            Env e = new Env();
            check(Files.exists(Path.of(e.dbFile)), "file db da tao");
            e.db.initSchema();
            e.db.initSchema();
            check(e.db.isConnected(), "ket noi duoc");
        });
        test("Du lieu con nguyen sau khi 'khoi dong lai' (persistence)", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.addMember(member("M02", MembershipType.VIP));
            e.events.addEvent(new Workshop("W1", "Java", "20/11/2026", 10, 100000, "A"));
            e.events.addEvent(new Competition("C1", "Cup", "21/11/2026", 10, 5000, 99));
            e.events.addEvent(new SocialEvent("S1", "Picnic", "22/11/2026", 10, "Park"));
            e.events.registerMember("W1", e.members.searchMemberById("M01"));
            e.events.registerMember("C1", e.members.searchMemberById("M02"));
            e.events.updateEventStatus("S1", EventStatus.CANCELLED);
            e.restart();
            eq(2, e.members.listAllMembers().size(), "thanh vien");
            eq(MembershipType.VIP, e.members.searchMemberById("M02").getMembershipType(), "loai VIP");
            eq(3, e.events.listEvents().size(), "su kien");
            eq(1, e.events.findEventById("W1").getParticipants().size(), "dang ky W1");
            eq(1, e.events.findEventById("C1").getParticipants().size(), "dang ky C1");
            eq(EventStatus.CANCELLED, e.events.findEventById("S1").getStatus(), "trang thai");
            check(e.events.findEventById("C1") instanceof Competition, "van la Competition");
        });
        test("Khoa ngoai: xoa thanh vien -> dang ky su kien bi xoa theo (CASCADE)", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.members.addMember(regular("M02"));
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            e.events.registerMember("S1", e.members.searchMemberById("M01"));
            e.events.registerMember("S1", e.members.searchMemberById("M02"));
            e.members.removeMember("M01");
            Event ev = e.events.findEventById("S1");
            eq(1, ev.getParticipants().size(), "con 1");
            eq("M02", ev.getParticipants().get(0).getId(), "con M02");
        });
        test("Khoa ngoai: khong the ghi dang ky cho thanh vien/su kien khong ton tai", () -> {
            Env e = new Env();
            e.events.addEvent(new SocialEvent("S1", "A", "22/11/2026", 10, "x"));
            EventRepository repo = new EventRepository(e.db);
            expect(DatabaseException.class, "member ma", () -> repo.addParticipant("S1", "GHOST"));
            expect(DatabaseException.class, "event ma", () -> repo.addParticipant("GHOST", "M01"));
        });
        test("Khoa chinh ngan trung o muc database (khong qua service)", () -> {
            Env e = new Env();
            MemberRepository repo = new MemberRepository(e.db);
            repo.insert(regular("M01"));
            expect(DatabaseException.class, "trung PK", () -> repo.insert(regular("m01")));
        });
        test("Cap nhat member qua repository giu nguyen ID va ngay tham gia", () -> {
            Env e = new Env();
            MemberRepository repo = new MemberRepository(e.db);
            repo.insert(regular("M01"));
            Member m = repo.findById("M01");
            m.setName("Ten Moi");
            m.setActive(false);
            repo.update(m);
            Member after = repo.findById("m01");
            eq("Ten Moi", after.getName(), "ten");
            check(!after.isActive(), "active=false");
            eq(LocalDate.of(2026, 1, 15), after.getJoinDate(), "join date");
            eq(1, repo.count(), "count");
        });
        test("Loi database duoc bao bang DatabaseException (khong nuot loi)", () -> {
            Env e = new Env();
            try (Connection c = e.db.open(); Statement st = c.createStatement()) {
                st.execute("DROP TABLE event_participants");
                st.execute("DROP TABLE members");
            }
            expect(DatabaseException.class, "bang mat", () -> e.members.listAllMembers());
        });
        test("File database bi xoa giua chung -> tu tao lai bang khi khoi dong lai", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            Files.delete(Path.of(e.dbFile));
            e.restart();
            eq(0, e.members.listAllMembers().size(), "db moi, rong");
            e.members.addMember(regular("M01"));
        });
        test("Duong dan database khong tao duoc -> DatabaseException ro rang", () -> {
            Path file = tempDir.resolve("iamafile");
            Files.writeString(file, "x");
            DatabaseConnection bad = new DatabaseConnection(file.resolve("child/club.db").toString());
            expect(DatabaseException.class, "thu muc khong tao duoc", bad::initSchema);
            check(!bad.isConnected(), "khong ket noi duoc");
        });
        test("Du lieu chi co dau tieng Viet va ky tu dac biet luu/doc dung", () -> {
            Env e = new Env();
            Member m = regular("M01");
            m.setName("Đặng Thị Hồng Nhung ơ ư");
            e.members.addMember(m);
            e.restart();
            eq("Đặng Thị Hồng Nhung ơ ư", e.members.searchMemberById("M01").getName(), "unicode");
        });
    }

    // ---------- Edge cases bo sung ----------

    private static void edgeTests() {
        test("Danh sach su kien rong; loc tren DB rong", () -> {
            Env e = new Env();
            check(e.events.listEvents().isEmpty(), "rong");
            for (EventStatus s : EventStatus.values()) {
                check(e.events.listEventsByStatus(s).isEmpty(), "rong " + s);
            }
        });
        test("Workshop capacity 0 khong chia cho 0", () -> {
            Workshop w = new Workshop("W", "a", "20/11/2026", 0, 100, "x");
            eq(100.0, w.calculateFee(), 0.001, "phi day du");
        });
        test("Xoa thanh vien dang tham gia su kien: su kien con nguyen, so luong giam", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            e.events.addEvent(new Workshop("W1", "A", "20/11/2026", 4, 100, "x"));
            e.events.registerMember("W1", e.members.searchMemberById("M01"));
            e.members.removeMember("M01");
            eq(0, e.events.findEventById("W1").getParticipantCount(), "0 nguoi");
            eq(1, e.events.listEvents().size(), "su kien con");
        });
        test("Sap xep ten khong thay doi thu tu luu trong database", () -> {
            Env e = new Env();
            Member a = regular("M01");
            a.setName("Zed");
            Member b = regular("M02");
            b.setName("Amy");
            e.members.addMember(a);
            e.members.addMember(b);
            e.members.sortMembersByName();
            eq("Zed", e.members.listAllMembers().get(0).getName(), "thu tu goc");
            List<Member> sorted = new ArrayList<>(e.members.listAllMembers());
            sorted.sort(Comparator.comparing(Member::getName));
            eq("Amy", sorted.get(0).getName(), "ban sao");
        });
        test("Ket noi khong bi ro ri: mo/dong rat nhieu lan van on dinh", () -> {
            Env e = new Env();
            e.members.addMember(regular("M01"));
            for (int i = 0; i < 300; i++) {
                e.members.searchMemberById("M01");
                e.members.listAllMembers();
            }
        });
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }
}
