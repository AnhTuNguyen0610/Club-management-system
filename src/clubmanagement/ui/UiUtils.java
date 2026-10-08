package clubmanagement.ui;

import clubmanagement.exception.DatabaseException;
import clubmanagement.model.Competition;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.MembershipType;
import clubmanagement.model.SocialEvent;
import clubmanagement.model.Workshop;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Cac hang so mau sac + ham tien ich dung chung cho MOI man hinh Swing, de giao
 * dien dong bo va Bien/Nhat Minh khong phai tu tay chinh style.
 *
 * Quan ly boi Anh Tu (Tech Lead). Bien/Nhat Minh CHI GOI, khong sua file nay
 * (can them ham moi -> bao Anh Tu).
 */
public final class UiUtils {

    // ---------- Bang mau ----------
    public static final Color PRIMARY = new Color(0x2563EB);
    public static final Color SIDEBAR_BG = new Color(0x111827);
    public static final Color SIDEBAR_HOVER = new Color(0x1F2937);
    public static final Color CONTENT_BG = new Color(0xF3F4F6);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x111827);
    public static final Color TEXT_MUTED = new Color(0x6B7280);
    public static final Color BORDER = new Color(0xE5E7EB);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color WARNING = new Color(0xD97706);

    private static final NumberFormat MONEY_FORMAT = NumberFormat.getInstance(new Locale.Builder().setLanguage("vi").setRegion("VN").build());

    private UiUtils() {
        // lop tien ich, khong tao doi tuong
    }

    // ---------- Look and Feel ----------

    /**
     * Cai giao dien FlatLaf (hien dai). Neu thieu file jar luc chay thi tu dong
     * dung giao dien he thong de chuong trinh van mo duoc.
     * Phai goi TRUOC khi tao bat ky component Swing nao.
     */
    public static void installLookAndFeel() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("Component.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 12);
        } catch (Throwable t) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // giu giao dien mac dinh cua Swing
            }
        }
    }

    // ---------- Nhan (label) ----------

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    // ---------- Nut bam ----------

    /** Nut chinh (xanh) - hanh dong chinh nhu "Them". */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty("FlatLaf.style",
                "background: #2563EB; foreground: #FFFFFF; hoverBackground: #1D4ED8; "
                        + "pressedBackground: #1E40AF; borderWidth: 0; focusWidth: 0; margin: 6,16,6,16");
        return b;
    }

    /** Nut phu (trang, vien xam) - "Lam moi", "Huy"... */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty("FlatLaf.style",
                "background: #FFFFFF; foreground: #111827; hoverBackground: #F3F4F6; "
                        + "borderColor: #D1D5DB; margin: 6,16,6,16");
        return b;
    }

    /** Nut nguy hiem (do) - "Xoa". */
    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.putClientProperty("FlatLaf.style",
                "background: #DC2626; foreground: #FFFFFF; hoverBackground: #B91C1C; "
                        + "pressedBackground: #991B1B; borderWidth: 0; focusWidth: 0; margin: 6,16,6,16");
        return b;
    }

    // ---------- Bang (JTable) ----------

    /**
     * Ap style thong nhat cho JTable: hang cao, header dam, khong ke doc,
     * bam tieu de cot de sap xep, chi chon 1 dong.
     * Nho dat model KHONG cho sua o (isCellEditable tra ve false) o phia goi.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xDBEAFE));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 38));
        header.setReorderingAllowed(false);
    }

    // ---------- Khung (card) ----------

    /** Khung trang, bo goc, co vien mong - dung de gom nhom noi dung. */
    public static RoundedPanel card(LayoutManager layout) {
        RoundedPanel p = new RoundedPanel(layout, 16);
        p.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        return p;
    }

    // ---------- Hop thoai ----------

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Hop thoai xac nhan (vd truoc khi xoa). true = nguoi dung bam "Xác nhận". */
    public static boolean confirm(Component parent, String message) {
        Object[] options = {"Xác nhận", "Hủy"};
        int choice = JOptionPane.showOptionDialog(parent, message, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        return choice == 0;
    }

    /** Bao loi ha tang DB cho nguoi dung bang mot hop thoai duy nhat. */
    public static void showDatabaseError(Component parent, DatabaseException e) {
        showError(parent, "Lỗi cơ sở dữ liệu: " + e.getMessage());
    }

    // ---------- Nhan hien thi tieng Viet (co dau) cho model/enum ----------
    // Model va Console giu chuoi khong dau; GUI dung cac ham nay de hien thi dep.

    /** Workshop -> "Workshop", Competition -> "Cuộc thi", SocialEvent -> "Giao lưu". */
    public static String eventTypeLabel(Event event) {
        if (event instanceof Workshop) {
            return "Workshop";
        }
        if (event instanceof Competition) {
            return "Cuộc thi";
        }
        if (event instanceof SocialEvent) {
            return "Giao lưu";
        }
        return event.getEventTypeDescription();
    }

    public static String statusLabel(EventStatus status) {
        switch (status) {
            case UPCOMING:
                return "Sắp diễn ra";
            case ONGOING:
                return "Đang diễn ra";
            case FINISHED:
                return "Đã kết thúc";
            case CANCELLED:
                return "Đã hủy";
            default:
                return status.name();
        }
    }

    public static String membershipLabel(MembershipType type) {
        switch (type) {
            case REGULAR:
                return "Thường";
            case VIP:
                return "VIP";
            case HONORARY:
                return "Danh dự";
            default:
                return type.name();
        }
    }

    // ---------- Dinh dang ----------

    /** 1500000 -> "1.500.000 đ" */
    public static String formatMoney(double amount) {
        return MONEY_FORMAT.format(Math.round(amount)) + " đ";
    }

    // ---------- Panel bo goc ----------

    /** JPanel ve nen trang bo goc + vien mong. */
    public static class RoundedPanel extends JPanel {
        private final int arc;

        public RoundedPanel(LayoutManager layout, int arc) {
            super(layout);
            this.arc = arc;
            setOpaque(false);
            setBackground(CARD_BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
