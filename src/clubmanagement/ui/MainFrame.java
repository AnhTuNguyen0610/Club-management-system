package clubmanagement.ui;

import clubmanagement.repository.DatabaseConnection;
import clubmanagement.service.ClubService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cua so chinh: thanh dieu huong ben trai + khu noi dung ben phai (CardLayout).
 * Moi "trang" la mot JPanel implements Refreshable; MainFrame chi biet den interface
 * Refreshable de goi refresh() khi chuyen trang.
 *
 * Quan ly boi Anh Tu (Tech Lead). Bien/Nhat Minh KHONG can sua file nay: chi lam
 * MemberPanel / EventPanel, MainFrame da gan san hai man hinh do.
 */
public class MainFrame extends JFrame {

    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_MEMBERS = "members";
    private static final String PAGE_EVENTS = "events";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel pageTitle = UiUtils.title("");
    private final JLabel pageSubtitle = UiUtils.muted("");

    /** key -> trang. LinkedHashMap giu thu tu them vao. */
    private final Map<String, JPanel> pages = new LinkedHashMap<>();
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();

    public MainFrame(ClubService clubService, DatabaseConnection db) {
        super("Hệ thống quản lý Câu lạc bộ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setSize(1180, 740);
        setLocationRelativeTo(null);

        // Tao 3 trang. Bien lam MemberPanel, Nhat Minh lam EventPanel.
        addPage(PAGE_DASHBOARD, new DashboardPanel(clubService, db));
        addPage(PAGE_MEMBERS, new MemberPanel(clubService.getMemberService()));
        addPage(PAGE_EVENTS, new EventPanel(clubService.getEventService(), clubService.getMemberService()));

        setLayout(new BorderLayout());
        add(buildSidebar(clubService), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);

        showPage(PAGE_DASHBOARD);
    }

    // ---------- Dung giao dien ----------

    private void addPage(String key, JPanel page) {
        pages.put(key, page);
        contentPanel.add(page, key);
    }

    private JPanel buildSidebar(ClubService clubService) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UiUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 14, 18, 14));

        JLabel brand = new JLabel("CLB Manager");
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 20f));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel clubName = new JLabel(clubService.getClub().getClubName());
        clubName.setForeground(new Color(0x9CA3AF));
        clubName.setAlignmentX(Component.LEFT_ALIGNMENT);
        clubName.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 0));

        sidebar.add(brand);
        sidebar.add(clubName);
        sidebar.add(Box.createRigidArea(new Dimension(0, 28)));

        sidebar.add(createNav(PAGE_DASHBOARD, "Tổng quan", NavIcon.Kind.DASHBOARD));
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(createNav(PAGE_MEMBERS, "Thành viên", NavIcon.Kind.MEMBERS));
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(createNav(PAGE_EVENTS, "Sự kiện", NavIcon.Kind.EVENTS));

        sidebar.add(Box.createVerticalGlue());

        JLabel team = new JLabel("<html>Bài tập lớn OOP<br>Anh Tú · Biên · Nhật Minh</html>");
        team.setForeground(new Color(0x6B7280));
        team.setFont(team.getFont().deriveFont(11.5f));
        team.setAlignmentX(Component.LEFT_ALIGNMENT);
        team.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        sidebar.add(team);
        return sidebar;
    }

    private NavButton createNav(String pageKey, String text, NavIcon.Kind kind) {
        NavButton button = new NavButton(text, kind);
        button.addActionListener(e -> showPage(pageKey));
        navButtons.put(pageKey, button);
        return button;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UiUtils.CONTENT_BG);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(24, 28, 8, 28));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(pageTitle);
        header.add(Box.createRigidArea(new Dimension(0, 2)));
        header.add(pageSubtitle);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 28, 24, 28));

        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    // ---------- Chuyen trang ----------

    /**
     * Hien mot trang, tai lai du lieu cua no va to sang nut dieu huong tuong ung.
     * Loi khi tai du lieu (DB hong, man hinh chua hoan thien...) duoc bat o day,
     * hien mot hop thoai thay vi de chuong trinh treo.
     */
    private void showPage(String key) {
        cardLayout.show(contentPanel, key);
        navButtons.forEach((k, button) -> button.setSelectedNav(k.equals(key)));
        updateHeader(key);

        JPanel page = pages.get(key);
        if (page instanceof Refreshable) {
            try {
                ((Refreshable) page).refresh();
            } catch (RuntimeException e) {
                UiUtils.showError(this, "Không tải được dữ liệu: " + e.getMessage());
            }
        }
    }

    private void updateHeader(String key) {
        switch (key) {
            case PAGE_MEMBERS:
                pageTitle.setText("Quản lý thành viên");
                pageSubtitle.setText("Thêm, sửa, xóa và tìm kiếm thành viên của câu lạc bộ");
                break;
            case PAGE_EVENTS:
                pageTitle.setText("Quản lý sự kiện");
                pageSubtitle.setText("Tạo sự kiện, đăng ký thành viên và theo dõi trạng thái");
                break;
            default:
                pageTitle.setText("Tổng quan");
                pageSubtitle.setText("Số liệu nhanh về hoạt động của câu lạc bộ");
        }
    }

    // ---------- Nut dieu huong ----------

    private static class NavButton extends JButton {
        private boolean selectedNav;
        private boolean hover;

        NavButton(String text, NavIcon.Kind kind) {
            super(text);
            setIcon(new NavIcon(kind, Color.WHITE));
            setIconTextGap(12);
            setHorizontalAlignment(SwingConstants.LEFT);
            setForeground(new Color(0xD1D5DB));
            setFont(getFont().deriveFont(Font.PLAIN, 14f));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(200, 42));
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void setSelectedNav(boolean selected) {
            this.selectedNav = selected;
            setForeground(selected ? Color.WHITE : new Color(0xD1D5DB));
            setFont(getFont().deriveFont(selected ? Font.BOLD : Font.PLAIN, 14f));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selectedNav) {
                g2.setColor(UiUtils.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else if (hover) {
                g2.setColor(UiUtils.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- Icon ve bang code (khong can file anh) ----------

    private static class NavIcon implements Icon {
        enum Kind { DASHBOARD, MEMBERS, EVENTS }

        private static final int SIZE = 18;
        private final Kind kind;
        private final Color color;

        NavIcon(Kind kind, Color color) {
            this.kind = kind;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.translate(x, y);
            switch (kind) {
                case DASHBOARD:
                    g2.fill(new RoundRectangle2D.Double(1, 1, 7, 7, 2, 2));
                    g2.fill(new RoundRectangle2D.Double(10, 1, 7, 7, 2, 2));
                    g2.fill(new RoundRectangle2D.Double(1, 10, 7, 7, 2, 2));
                    g2.fill(new RoundRectangle2D.Double(10, 10, 7, 7, 2, 2));
                    break;
                case MEMBERS:
                    g2.fill(new Ellipse2D.Double(5.5, 1, 7, 7));
                    g2.fill(new RoundRectangle2D.Double(2, 10, 14, 7, 7, 7));
                    break;
                default: // EVENTS
                    g2.setStroke(new java.awt.BasicStroke(1.8f));
                    g2.draw(new RoundRectangle2D.Double(1.5, 3, 15, 13, 3, 3));
                    g2.fill(new RoundRectangle2D.Double(1.5, 3, 15, 4.5, 3, 3));
                    g2.fillRect(5, 0, 2, 4);
                    g2.fillRect(11, 0, 2, 4);
                    break;
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
