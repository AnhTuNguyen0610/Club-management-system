package clubmanagement.ui;

import clubmanagement.util.InputValidator;
import clubmanagement.exception.DatabaseException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Man hinh QUAN LY SU KIEN (Swing).
 *
 * - Tren: thanh cong cu (loc theo trang thai, them su kien, dang ky, huy dang ky,
 *   doi trang thai) + bang danh sach su kien. Nhap dup chuot vao 1 su kien de xem chi tiet.
 * - Duoi: bang nguoi tham gia cua su kien dang chon.
 * Man hinh chi goi EventService / MemberService, khong chua nghiep vu.
 */
public class EventPanel extends JPanel implements Refreshable {

    private final EventService eventService;
    private final MemberService memberService;

    private JComboBox<String> cbStatusFilter;
    private JTable tblEvents;
    private DefaultTableModel eventTableModel;
    private JTable tblParticipants;
    private DefaultTableModel participantTableModel;
    private JLabel lblParticipants;
    private boolean loading = false;

    private static final EventStatus[] FILTER_STATUSES = EventStatus.values();

    public EventPanel(EventService eventService, MemberService memberService) {
        this.eventService = eventService;
        this.memberService = memberService;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftToolbar.setOpaque(false);
        String[] filterLabels = new String[FILTER_STATUSES.length + 1];
        filterLabels[0] = "Tất cả";
        for (int i = 0; i < FILTER_STATUSES.length; i++) {
            filterLabels[i + 1] = UiUtils.statusLabel(FILTER_STATUSES[i]);
        }
        cbStatusFilter = new JComboBox<>(filterLabels);
        cbStatusFilter.addActionListener(e -> refresh());

        JButton btnRefresh = UiUtils.secondaryButton("Làm mới");
        btnRefresh.addActionListener(e -> refresh());

        leftToolbar.add(new JLabel("Lọc trạng thái:"));
        leftToolbar.add(cbStatusFilter);
        leftToolbar.add(btnRefresh);

        JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightToolbar.setOpaque(false);

        JButton btnAddEvent = UiUtils.primaryButton("Thêm sự kiện");
        btnAddEvent.addActionListener(e -> onAddEventClicked());

        JButton btnRegister = UiUtils.secondaryButton("Đăng ký thành viên");
        btnRegister.addActionListener(e -> onRegisterClicked());

        JButton btnCancelReg = UiUtils.secondaryButton("Hủy đăng ký");
        btnCancelReg.addActionListener(e -> onCancelRegistrationClicked());

        JButton btnChangeStatus = UiUtils.secondaryButton("Đổi trạng thái");
        btnChangeStatus.addActionListener(e -> onChangeStatusClicked());

        rightToolbar.add(btnAddEvent);
        rightToolbar.add(btnRegister);
        rightToolbar.add(btnCancelReg);
        rightToolbar.add(btnChangeStatus);

        toolbar.add(leftToolbar, BorderLayout.WEST);
        toolbar.add(rightToolbar, BorderLayout.EAST);

        String[] eventColumns = { "Mã", "Tên sự kiện", "Loại", "Ngày", "Đã đăng ký (x/y)", "Phí hiện tại",
                "Trạng thái" };
        eventTableModel = new DefaultTableModel(eventColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblEvents = new JTable(eventTableModel);
        UiUtils.styleTable(tblEvents);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        tblEvents.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblEvents.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblEvents.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblEvents.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblEvents.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        tblEvents.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        tblEvents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Nháy đúp chuột
                    Event selectedEvent = getSelectedEvent();
                    if (selectedEvent != null) {
                        showEventDetails(selectedEvent);
                    }
                }
            }
        });

        String[] participantColumns = { "Mã TV", "Họ tên", "Email", "Loại thành viên" };
        participantTableModel = new DefaultTableModel(participantColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblParticipants = new JTable(participantTableModel);
        UiUtils.styleTable(tblParticipants);

        tblParticipants.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblParticipants.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        tblEvents.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblEvents.getColumnModel().getColumn(1).setPreferredWidth(260);
        tblEvents.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblEvents.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblEvents.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblEvents.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblEvents.getColumnModel().getColumn(6).setPreferredWidth(110);
        tblParticipants.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblParticipants.getColumnModel().getColumn(2).setPreferredWidth(220);

        tblEvents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loading) {
                onEventSelectionChanged();
            }
        });

        lblParticipants = new JLabel("Người tham gia: (chưa chọn sự kiện)");
        lblParticipants.setFont(lblParticipants.getFont().deriveFont(java.awt.Font.BOLD));
        lblParticipants.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 0));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(lblParticipants, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(tblParticipants), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblEvents),
                bottomPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        JPanel cardWrapper = UiUtils.card(new BorderLayout());
        cardWrapper.add(contentPanel, BorderLayout.CENTER);
        add(cardWrapper, BorderLayout.CENTER);
    }

    /**
     * Duoc MainFrame goi moi khi nguoi dung mo trang nay.
     * Doc lai danh sach su kien (co ap dung bo loc trang thai dang chon) roi
     * loadEventTable(...).
     */
    @Override
    public void refresh() {
        String selectedId = getSelectedEventId();
        try {
            int selectedIndex = cbStatusFilter.getSelectedIndex();
            List<Event> events;
            if (selectedIndex <= 0) {
                events = eventService.listEvents();
            } else {
                events = eventService.listEventsByStatus(FILTER_STATUSES[selectedIndex - 1]);
            }
            loading = true;
            loadEventTable(events);
            restoreSelection(selectedId);
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        } finally {
            loading = false;
        }
        onEventSelectionChanged();
    }

    private String getSelectedEventId() {
        int row = tblEvents.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (String) eventTableModel.getValueAt(tblEvents.convertRowIndexToModel(row), 0);
    }

    private void restoreSelection(String eventId) {
        if (eventId == null) {
            return;
        }
        for (int i = 0; i < eventTableModel.getRowCount(); i++) {
            if (eventId.equalsIgnoreCase((String) eventTableModel.getValueAt(i, 0))) {
                int viewRow = tblEvents.convertRowIndexToView(i);
                tblEvents.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }

    /** Chon/bo chon 1 su kien -> nap lai bang nguoi tham gia. */
    private void onEventSelectionChanged() {
        String eventId = getSelectedEventId();
        if (eventId == null) {
            loadParticipantTable(null);
            lblParticipants.setText("Người tham gia: (chưa chọn sự kiện)");
            return;
        }
        Event event = getSelectedEvent();
        loadParticipantTable(event);
        if (event != null) {
            lblParticipants.setText("Người tham gia \"" + event.getEventName() + "\": "
                    + event.getParticipantCount() + "/" + event.getMaxParticipants());
        }
    }

    /**
     * Do danh sach su kien vao bang tren (moi Event 1 hang). Rong -> bang trong
     * (khong nem loi).
     * Cot "Phi hien tai": UiUtils.formatMoney(event.calculateFee()) - goi
     * calculateFee() truc tiep tren
     * Event, KHONG kiem tra loai (chinh la Polymorphism).
     */
    private void loadEventTable(List<Event> events) {
        eventTableModel.setRowCount(0);
        for (Event event : events) {
            Object[] row = {
                    event.getEventId(),
                    event.getEventName(),
                    UiUtils.eventTypeLabel(event),
                    event.getDate(),
                    event.getParticipants().size() + "/" + event.getMaxParticipants(),
                    UiUtils.formatMoney(event.calculateFee()),
                    UiUtils.statusLabel(event.getStatus())
            };
            eventTableModel.addRow(row);
        }
    }

    /**
     * Do event.getParticipants() vao bang duoi; event == null -> bang duoi trong.
     */
    private void loadParticipantTable(Event event) {
        participantTableModel.setRowCount(0);
        if (event != null) {
            for (Member m : event.getParticipants()) {
                Object[] row = {
                        m.getId(),
                        m.getName(),
                        m.getEmail(),
                        UiUtils.membershipLabel(m.getMembershipType())
                };
                participantTableModel.addRow(row);
            }
        }
    }

    /**
     * Tra ve su kien dang chon o bang tren (lay lai bang
     * eventService.findEventById(id) o cot Ma
     * de co du lieu moi nhat), hoac null neu chua chon.
     * Chu y doi chi so view -> model neu bang co sap xep (convertRowIndexToModel).
     */
    private Event getSelectedEvent() {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tblEvents.convertRowIndexToModel(selectedRow);
            String eventId = (String) eventTableModel.getValueAt(modelRow, 0);
            try {
                return eventService.findEventById(eventId);
            } catch (DatabaseException e) {
                UiUtils.showDatabaseError(this, e);
            }
        }
        return null;
    }

    /**
     * Mo EventFormDialog; luu thanh cong ->
     * eventService.addEvent(...) roi refresh().
     */
    private void onAddEventClicked() {
        EventFormDialog dialog = new EventFormDialog((Frame) SwingUtilities.getWindowAncestor(this), eventService);
        dialog.setVisible(true);
        refresh();
    }

    /**
     * Chon thanh vien (memberService.listAllMembers()) roi
     * eventService.registerMember(eventId, member).
     */
    private void onRegisterClicked() {
        Event selectedEvent = getSelectedEvent();
        if (selectedEvent == null) {
            UiUtils.showWarning(this, "Vui lòng chọn một sự kiện để đăng ký thành viên!");
            return;
        }

        if (selectedEvent.getStatus() != EventStatus.UPCOMING) {
            UiUtils.showWarning(this, "Chỉ có thể đăng ký vào sự kiện sắp diễn ra (Sắp diễn ra)!");
            return;
        }

        RegisterMemberDialog dialog = new RegisterMemberDialog((Frame) SwingUtilities.getWindowAncestor(this),
                eventService, memberService, selectedEvent);
        dialog.setVisible(true);
        refresh();
    }

    /**
     * Xac nhan roi eventService.cancelRegistration(eventId,
     * memberId) cho nguoi dang chon o bang duoi.
     */
    private void onCancelRegistrationClicked() {
        Event selectedEvent = getSelectedEvent();
        if (selectedEvent == null) {
            UiUtils.showWarning(this, "Vui lòng chọn một sự kiện ở bảng trên!");
            return;
        }

        int selectedParticipantRow = tblParticipants.getSelectedRow();
        if (selectedParticipantRow < 0) {
            UiUtils.showWarning(this, "Vui lòng chọn một thành viên ở bảng dưới để hủy đăng ký!");
            return;
        }

        int modelRow = tblParticipants.convertRowIndexToModel(selectedParticipantRow);
        String memberId = (String) participantTableModel.getValueAt(modelRow, 0);
        String memberName = (String) participantTableModel.getValueAt(modelRow, 1);

        boolean confirm = UiUtils.confirm(this,
                "Bạn có chắc chắn muốn hủy đăng ký cho thành viên: " + memberName + "?");
        if (confirm) {
            try {
                if (eventService.cancelRegistration(selectedEvent.getEventId(), memberId)) {
                    UiUtils.showInfo(this, "Đã hủy đăng ký của " + memberName + ".");
                } else {
                    UiUtils.showWarning(this, "Thành viên này không còn trong danh sách đăng ký.");
                }
                refresh();
            } catch (DatabaseException e) {
                UiUtils.showDatabaseError(this, e);
            }
        }
    }

    /**
     * Hop thoai chon trang thai moi ->
     * eventService.updateEventStatus(eventId, status).
     */
    private void onChangeStatusClicked() {
        Event selectedEvent = getSelectedEvent();
        if (selectedEvent == null) {
            UiUtils.showWarning(this, "Vui lòng chọn một sự kiện để đổi trạng thái!");
            return;
        }

        String[] statuses = { "UPCOMING", "ONGOING", "FINISHED", "CANCELLED" };
        String[] displayStatuses = { "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc", "Đã hủy" };

        String currentStatusDisplay = UiUtils.statusLabel(selectedEvent.getStatus());

        String newStatusDisplay = (String) JOptionPane.showInputDialog(this,
                "Chọn trạng thái mới:", "Đổi trạng thái sự kiện",
                JOptionPane.QUESTION_MESSAGE, null, displayStatuses, currentStatusDisplay);

        if (newStatusDisplay != null && !newStatusDisplay.equals(currentStatusDisplay)) {
            try {
                EventStatus newStatus = EventStatus.UPCOMING;
                for (int i = 0; i < displayStatuses.length; i++) {
                    if (displayStatuses[i].equals(newStatusDisplay)) {
                        newStatus = EventStatus.valueOf(statuses[i]);
                        break;
                    }
                }
                eventService.updateEventStatus(selectedEvent.getEventId(), newStatus);
                refresh();
            } catch (EventNotFoundException | InvalidInputException e) {
                UiUtils.showError(this, e.getMessage());
                refresh();
            } catch (DatabaseException e) {
                UiUtils.showDatabaseError(this, e);
            }
        }
    }

    private void showEventDetails(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tên sự kiện: ").append(event.getEventName()).append("\n");
        sb.append("Loại: ").append(UiUtils.eventTypeLabel(event)).append("\n");
        sb.append("Phí tham gia: ").append(UiUtils.formatMoney(event.calculateFee())).append("\n\n");

        if (event instanceof clubmanagement.model.Workshop) {
            clubmanagement.model.Workshop w = (clubmanagement.model.Workshop) event;
            sb.append("Diễn giả khách mời: ").append(w.getSpeaker()).append("\n");
        } else if (event instanceof clubmanagement.model.Competition) {
            clubmanagement.model.Competition c = (clubmanagement.model.Competition) event;
            sb.append("Tổng giá trị giải thưởng: ").append(UiUtils.formatMoney(c.getPrizeValue())).append("\n");
        } else if (event instanceof clubmanagement.model.SocialEvent) {
            clubmanagement.model.SocialEvent s = (clubmanagement.model.SocialEvent) event;
            sb.append("Địa điểm tổ chức: ").append(s.getLocation()).append("\n");
        }

        UiUtils.showInfo(this, sb.toString());
    }
}
