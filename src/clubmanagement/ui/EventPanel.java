package clubmanagement.ui;

import clubmanagement.exception.DatabaseException;
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
 * MODULE: NHAT MINH (Giai doan 3 - Task N3.1)
 * Anh Tu da gan san man hinh nay vao MainFrame (constructor nay do Anh Tu chot,
 * KHONG doi tham so). Nhat Minh chi lam ben trong file nay + tao them cac file
 * MOI
 * EventFormDialog.java, RegisterMemberDialog.java (khong dung cham file cua
 * nguoi khac).
 * Can danh sach thanh vien de chon dang ky -> dung
 * memberService.listAllMembers()
 * (chi DOC, dung sua MemberService).
 *
 * ===== YEU CAU GIAO DIEN =====
 * Bo cuc BorderLayout trong the trang bo goc (UiUtils.card(...)):
 * - NORTH : thanh cong cu = JComboBox loc trang thai ("Tất cả" + 4 trang thai,
 * hien bang
 * UiUtils.statusLabel) + nut "Làm mới" o ben trai; cac nut "Thêm sự kiện"
 * (primaryButton), "Đăng ký thành viên", "Hủy đăng ký" (secondaryButton),
 * "Đổi trạng thái" (secondaryButton) o ben phai.
 * - CENTER: JSplitPane doc (chia tren/duoi):
 * + tren : bang su kien, cot: Mã | Tên sự kiện | Loại | Ngày | Đã đăng ký (x/y)
 * | Phí hiện tại | Trạng thái
 * + duoi : bang nguoi tham gia cua su kien dang chon, cot: Mã TV | Họ tên |
 * Email | Loại thành viên
 * Ca hai bang goi UiUtils.styleTable(...) va KHONG cho sua truc tiep o.
 * Chon 1 dong o bang tren -> bang duoi hien danh sach event.getParticipants().
 * - EventFormDialog (JDialog modal) them su kien: Mã, Tên, Ngày (dd/MM/yyyy),
 * Số lượng tối đa,
 * Loại (JComboBox: Workshop / Cuộc thi / Giao lưu). Chon loai nao thi hien dung
 * cac o rieng:
 * Workshop: Phí cơ bản + Diễn giả | Cuộc thi: Phí tham gia + Giá trị giải
 * thưởng | Giao lưu: Địa điểm.
 *
 * ===== HANH VI =====
 * - Kiem tra form bang InputValidator (requireNotBlank, requireDate,
 * parsePositiveInt,
 * parseNonNegativeDouble). Sai -> UiUtils.showWarning(this, e.getMessage()).
 * - Tao doi tuong dung lop con (new Workshop/Competition/SocialEvent) roi
 * eventService.addEvent(...).
 * - Bat DuplicateEventException / EventFullException / EventNotFoundException /
 * MemberNotFoundException
 * -> UiUtils.showError; bat DatabaseException -> UiUtils.showDatabaseError.
 * - Doi trang thai: hop thoai chon 1 trong 4 trang thai ->
 * eventService.updateEventStatus(...) (method MOI, xem Task N2.2).
 * - Nhan Huy dang ky: chon nguoi trong bang duoi roi UiUtils.confirm ->
 * eventService.cancelRegistration(...).
 *
 * ===== TIEU CHI NGHIEM THU =====
 * 1. Tao du 3 loai su kien; cot "Phí hiện tại" hien dung gia moi loai (Workshop
 * som &lt;= 20% cho -> giam 10%).
 * 2. Dang ky den khi day cho -> lan tiep theo hien hop thoai loi
 * (EventFullException), khong crash.
 * 3. Loc theo trang thai dung; doi trang thai xong bang cap nhat va van con sau
 * khi khoi dong lai.
 * 4. Khong co System.out.println trong file nay; moi loi bao qua hop thoai.
 */
public class EventPanel extends JPanel implements Refreshable {

    private final EventService eventService;
    private final MemberService memberService;

    private JComboBox<String> cbStatusFilter;
    private JTable tblEvents;
    private DefaultTableModel eventTableModel;
    private JTable tblParticipants;
    private DefaultTableModel participantTableModel;

    public EventPanel(EventService eventService, MemberService memberService) {
        this.eventService = eventService;
        this.memberService = memberService;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftToolbar.setOpaque(false);
        cbStatusFilter = new JComboBox<>(new String[] { "Tất cả", "UPCOMING", "ONGOING", "FINISHED", "CANCELLED" });
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblEvents),
                new JScrollPane(tblParticipants));
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
     * TODO: NHAT MINH
     * Duoc MainFrame goi moi khi nguoi dung mo trang nay.
     * Doc lai danh sach su kien (co ap dung bo loc trang thai dang chon) roi
     * loadEventTable(...).
     */
    @Override
    public void refresh() {
        try {
            int selectedIndex = cbStatusFilter.getSelectedIndex();
            List<Event> events;
            if (selectedIndex == 0) {
                events = eventService.listEvents();
            } else {
                EventStatus status = EventStatus.valueOf(cbStatusFilter.getSelectedItem().toString());
                events = eventService.listEventsByStatus(status);
            }
            loadEventTable(events);
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * TODO: NHAT MINH
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
     * TODO: NHAT MINH
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
     * TODO: NHAT MINH
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
     * TODO: NHAT MINH - Mo EventFormDialog; luu thanh cong ->
     * eventService.addEvent(...) roi refresh().
     */
    private void onAddEventClicked() {
        EventFormDialog dialog = new EventFormDialog((Frame) SwingUtilities.getWindowAncestor(this), eventService);
        dialog.setVisible(true);
        refresh();
    }

    /**
     * TODO: NHAT MINH - Chon thanh vien (memberService.listAllMembers()) roi
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
     * TODO: NHAT MINH - Xac nhan roi eventService.cancelRegistration(eventId,
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
                eventService.cancelRegistration(selectedEvent.getEventId(), memberId);
                refresh();
            } catch (Exception e) {
                UiUtils.showError(this, "Lỗi khi hủy đăng ký: " + e.getMessage());
            }
        }
    }

    /**
     * TODO: NHAT MINH - Hop thoai chon trang thai moi ->
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
            } catch (Exception e) {
                UiUtils.showError(this, "Lỗi khi cập nhật trạng thái: " + e.getMessage());
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
