package clubmanagement;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Club;
import clubmanagement.repository.DatabaseConnection;
import clubmanagement.repository.EventRepository;
import clubmanagement.repository.MemberRepository;
import clubmanagement.service.ClubService;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;
import clubmanagement.ui.ConsoleUI;
import clubmanagement.ui.MainFrame;
import clubmanagement.ui.UiUtils;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Diem khoi chay chuong trinh: chi khoi tao va NOI cac doi tuong voi nhau, khong chua nghiep vu.
 *
 * Thu tu noi (tu duoi len tren):
 *   DatabaseConnection -> Repository -> Service -> ClubService -> giao dien (Swing hoac Console)
 *
 * Cach chay:
 *   - Mac dinh: giao dien do hoa Swing (MainFrame).
 *   - Them tham so --console: chay ban console cu (dung de doi chieu/kiem thu nhanh).
 *
 * Duoc quan ly boi Tech Lead (Anh Tu) - Bien va Nhat Minh khong can sua file nay.
 */
public class Main {

    /** File database SQLite, tinh tu thu muc goc cua du an. Tu dong duoc tao o lan chay dau. */
    private static final String DB_FILE = "data/club.db";

    public static void main(String[] args) {
        // Ep System.out/System.err dung UTF-8: mot so may (dac biet Windows chua chay
        // "chcp 65001", hoac Linux khong cai locale UTF-8) mac dinh doc/ghi console bang
        // ASCII, khien chu tieng Viet co dau hien thanh dau "?". Lam o day 1 lan, dung cho
        // ca ConsoleUI lan cac dong log loi, khong phu thuoc cau hinh may nguoi dung.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

        boolean consoleMode = args.length > 0 && "--console".equalsIgnoreCase(args[0]);

        // 1. Ha tang co so du lieu
        DatabaseConnection db = new DatabaseConnection(DB_FILE);
        try {
            db.initSchema();
        } catch (DatabaseException e) {
            reportStartupError(e, consoleMode);
            return;
        }

        // 2. Repository -> Service (dependency truyen qua constructor)
        MemberRepository memberRepository = new MemberRepository(db);
        EventRepository eventRepository = new EventRepository(db);
        MemberService memberService = new MemberService(memberRepository);
        EventService eventService = new EventService(eventRepository);

        // 3. Dieu phoi
        Club club = new Club("CLB Lập Trình", "Câu lạc bộ dành cho sinh viên yêu thích lập trình");
        ClubService clubService = new ClubService(club, memberService, eventService);

        // 4. Giao dien
        if (consoleMode) {
            new ConsoleUI(clubService).run();
        } else {
            SwingUtilities.invokeLater(() -> {
                UiUtils.installLookAndFeel();
                new MainFrame(clubService, db).setVisible(true);
            });
        }
    }

    private static void reportStartupError(DatabaseException e, boolean consoleMode) {
        System.err.println("Khong the khoi dong: " + e.getMessage());
        if (e.getCause() != null) {
            System.err.println("Nguyen nhan: " + e.getCause().getMessage());
        }
        if (!consoleMode) {
            JOptionPane.showMessageDialog(null,
                    "Không thể khởi tạo cơ sở dữ liệu:\n" + e.getMessage(),
                    "Lỗi khởi động", JOptionPane.ERROR_MESSAGE);
        }
    }
}
