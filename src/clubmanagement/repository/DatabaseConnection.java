package clubmanagement.repository;

import clubmanagement.exception.DatabaseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Quan ly ket noi toi co so du lieu SQLite (mot file .db, khong can cai server).
 *
 * Vai tro: chi lo viec MO KET NOI va TAO BANG. Moi cau lenh SELECT/INSERT/...
 * nam trong cac Repository (MemberRepository, EventRepository), cac Repository
 * giu tham chieu toi doi tuong nay (association).
 *
 * MAU CODE CHUAN de Bien / Nhat Minh bat chuoc khi viet Repository:
 * <pre>
 * String sql = "SELECT COUNT(*) FROM members WHERE id = ?";
 * try (Connection conn = db.open();
 *      PreparedStatement ps = conn.prepareStatement(sql)) {
 *     ps.setString(1, id);                       // KHONG noi chuoi vao SQL
 *     try (ResultSet rs = ps.executeQuery()) {
 *         return rs.next() &amp;&amp; rs.getInt(1) &gt; 0;
 *     }
 * } catch (SQLException e) {
 *     throw new DatabaseException("Loi khi kiem tra thanh vien", e);
 * }
 * </pre>
 * Giai thich (de bao ve): try-with-resources tu dong dong Connection /
 * PreparedStatement / ResultSet; PreparedStatement dung dau "?" de chong SQL
 * injection; SQLException duoc boc thanh DatabaseException (unchecked).
 */
public class DatabaseConnection {

    private final String dbFilePath;
    private final String url;

    /**
     * @param dbFilePath duong dan file database, vi du "data/club.db".
     *                   Thu muc cha se duoc tu dong tao neu chua ton tai.
     */
    public DatabaseConnection(String dbFilePath) {
        this.dbFilePath = dbFilePath;
        this.url = "jdbc:sqlite:" + dbFilePath;
    }

    public String getDbFilePath() {
        return dbFilePath;
    }

    /**
     * Mo mot ket noi moi. Nguoi goi PHAI dong ket noi (dung try-with-resources).
     * SQLite mac dinh khong ep khoa ngoai, nen bat PRAGMA foreign_keys cho tung ket noi.
     */
    public Connection open() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * Tao thu muc chua file db (neu can) va tao 3 bang neu chua co.
     * An toan khi goi nhieu lan (CREATE TABLE IF NOT EXISTS).
     * Xem so do bang chi tiet trong docs/database.md.
     */
    public void initSchema() {
        createParentDirectory();

        String createMembers =
                "CREATE TABLE IF NOT EXISTS members ("
                + " id              TEXT PRIMARY KEY COLLATE NOCASE,"   // NOCASE: ID khong phan biet hoa/thuong
                + " name            TEXT NOT NULL,"
                + " email           TEXT NOT NULL,"
                + " phone           TEXT,"
                + " membership_type TEXT NOT NULL,"                       // REGULAR / VIP / HONORARY
                + " join_date       TEXT NOT NULL,"                       // ISO yyyy-MM-dd (LocalDate.toString())
                + " active          INTEGER NOT NULL DEFAULT 1"           // 1 = dang hoat dong, 0 = ngung
                + ")";

        String createEvents =
                "CREATE TABLE IF NOT EXISTS events ("
                + " event_id         TEXT PRIMARY KEY COLLATE NOCASE,"
                + " event_name       TEXT NOT NULL,"
                + " event_date       TEXT NOT NULL,"                      // luu nguyen chuoi nguoi dung nhap (dd/MM/yyyy)
                + " max_participants INTEGER NOT NULL,"
                + " status           TEXT NOT NULL,"                      // UPCOMING / ONGOING / FINISHED / CANCELLED
                + " event_type       TEXT NOT NULL,"                      // WORKSHOP / COMPETITION / SOCIAL
                + " fee              REAL,"                               // Workshop: baseFee | Competition: entryFee
                + " speaker          TEXT,"                               // chi Workshop
                + " prize_value      REAL,"                               // chi Competition
                + " location         TEXT"                                // chi SocialEvent
                + ")";

        String createParticipants =
                "CREATE TABLE IF NOT EXISTS event_participants ("
                + " event_id  TEXT NOT NULL,"
                + " member_id TEXT NOT NULL,"
                + " PRIMARY KEY (event_id, member_id),"
                + " FOREIGN KEY (event_id)  REFERENCES events(event_id)  ON DELETE CASCADE,"
                + " FOREIGN KEY (member_id) REFERENCES members(id)       ON DELETE CASCADE"
                + ")";

        try (Connection conn = open(); Statement st = conn.createStatement()) {
            st.executeUpdate(createMembers);
            st.executeUpdate(createEvents);
            st.executeUpdate(createParticipants);
        } catch (SQLException e) {
            throw new DatabaseException("Khong the khoi tao co so du lieu tai: " + dbFilePath, e);
        }
    }

    /**
     * Kiem tra ket noi con song khong (dung cho Dashboard hien trang thai he thong).
     * Khong nem exception: tra ve false neu co loi.
     */
    public boolean isConnected() {
        try (Connection conn = open();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private void createParentDirectory() {
        Path parent = Paths.get(dbFilePath).toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new DatabaseException("Khong the tao thu muc du lieu: " + parent, e);
        }
    }
}
