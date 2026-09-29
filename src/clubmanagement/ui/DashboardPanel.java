package clubmanagement.ui;

import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.repository.DatabaseConnection;
import clubmanagement.service.ClubService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Man hinh "Tong quan": 4 the thong ke, bang su kien sap dien ra va trang thai database.
 *
 * Diem OOP dang chu y khi bao ve: doanh thu uoc tinh duoc tinh bang cach duyet
 * List&lt;Event&gt; va goi event.calculateFee() - moi loai su kien tu tinh phi theo cach
 * rieng (Workshop / Competition / SocialEvent) nhung noi goi khong can biet loai nao
 * (Polymorphism).
 *
 * Quan ly boi Anh Tu (Tech Lead).
 */
public class DashboardPanel extends JPanel implements Refreshable {

    private static final String CARD_TABLE = "table";
    private static final String CARD_EMPTY = "empty";

    private final ClubService clubService;
    private final DatabaseConnection db;

    private final JLabel lblMembers = bigNumber(UiUtils.PRIMARY);
    private final JLabel lblEvents = bigNumber(new Color(0x7C3AED));
    private final JLabel lblRegistrations = bigNumber(UiUtils.SUCCESS);
    private final JLabel lblRevenue = bigNumber(UiUtils.WARNING);

    private final DefaultTableModel upcomingModel = new DefaultTableModel(
            new String[]{"Mã", "Tên sự kiện", "Loại", "Ngày", "Đã đăng ký", "Phí hiện tại"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable upcomingTable = new JTable(upcomingModel);
    private final CardLayout tableCards = new CardLayout();
    private final JPanel tableHolder = new JPanel(tableCards);
    private final JLabel lblDbStatus = new JLabel();

    public DashboardPanel(ClubService clubService, DatabaseConnection db) {
        this.clubService = clubService;
        this.db = db;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);

        add(buildStatCards(), BorderLayout.NORTH);
        add(buildUpcomingCard(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ---------- Dung giao dien ----------

    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.add(statCard("Thành viên", lblMembers));
        row.add(statCard("Sự kiện", lblEvents));
        row.add(statCard("Lượt đăng ký", lblRegistrations));
        row.add(statCard("Doanh thu ước tính", lblRevenue));
        return row;
    }

    private JPanel statCard(String caption, JLabel valueLabel) {
        UiUtils.RoundedPanel card = UiUtils.card(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel captionLabel = UiUtils.muted(caption);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(captionLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    private static JLabel bigNumber(Color color) {
        JLabel label = new JLabel("0");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 30f));
        label.setForeground(color);
        return label;
    }

    private JPanel buildUpcomingCard() {
        UiUtils.RoundedPanel card = UiUtils.card(new BorderLayout(0, 12));
        card.add(UiUtils.sectionTitle("Sự kiện sắp diễn ra"), BorderLayout.NORTH);

        UiUtils.styleTable(upcomingTable);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        upcomingTable.getColumnModel().getColumn(5).setCellRenderer(right);
        upcomingTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        upcomingTable.getColumnModel().getColumn(1).setPreferredWidth(260);

        JScrollPane scroll = new JScrollPane(upcomingTable);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtils.BORDER));

        JLabel empty = UiUtils.muted("Chưa có sự kiện nào sắp diễn ra.");
        empty.setHorizontalAlignment(SwingConstants.CENTER);

        tableHolder.setOpaque(false);
        tableHolder.add(scroll, CARD_TABLE);
        tableHolder.add(empty, CARD_EMPTY);
        card.add(tableHolder, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        lblDbStatus.setFont(lblDbStatus.getFont().deriveFont(12f));
        bar.add(lblDbStatus, BorderLayout.WEST);
        return bar;
    }

    // ---------- Nap du lieu ----------

    @Override
    public void refresh() {
        List<Member> members = clubService.getMemberService().listAllMembers();
        List<Event> events = clubService.getEventService().listEvents();
        List<Event> upcoming = clubService.getEventService().listEventsByStatus(EventStatus.UPCOMING);

        int registrations = 0;
        double revenue = 0;
        for (Event event : events) {
            int count = event.getParticipants().size();
            registrations += count;
            revenue += event.calculateFee() * count; // da hinh: moi loai Event tu tinh phi
        }

        lblMembers.setText(String.valueOf(members.size()));
        lblEvents.setText(String.valueOf(events.size()));
        lblRegistrations.setText(String.valueOf(registrations));
        lblRevenue.setText(UiUtils.formatMoney(revenue));

        upcomingModel.setRowCount(0);
        for (Event event : upcoming) {
            upcomingModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    UiUtils.eventTypeLabel(event),
                    event.getDate(),
                    event.getParticipants().size() + " / " + event.getMaxParticipants(),
                    UiUtils.formatMoney(event.calculateFee())
            });
        }
        tableCards.show(tableHolder, upcoming.isEmpty() ? CARD_EMPTY : CARD_TABLE);

        updateDbStatus();
    }

    private void updateDbStatus() {
        boolean ok = db.isConnected();
        lblDbStatus.setForeground(ok ? UiUtils.SUCCESS : UiUtils.DANGER);
        lblDbStatus.setText((ok ? "●  Cơ sở dữ liệu SQLite: đã kết nối" : "●  Cơ sở dữ liệu: MẤT KẾT NỐI")
                + "   ·   " + db.getDbFilePath());
    }
}
