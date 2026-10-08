package clubmanagement.ui;

import clubmanagement.exception.DatabaseException;
import clubmanagement.exception.EventFullException;
import clubmanagement.exception.EventNotFoundException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Event;
import clubmanagement.model.Member;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Hop thoai hien thi danh sach thanh vien de dang ky vao mot su kien.
 * Doc du lieu truc tiep tu MemberService va tuong tac dang ky qua EventService.
 */
public class RegisterMemberDialog extends JDialog {

    private final EventService eventService;
    private final MemberService memberService;
    private final Event event;

    private JTable tblMembers;
    private DefaultTableModel tableModel;

    /**
     * Khoi tao giao dien chon thanh vien dang ky.
     */
    public RegisterMemberDialog(Frame parent, EventService eventService, MemberService memberService, Event event) {
        super(parent, "Đăng ký thành viên vào sự kiện: " + event.getEventName(), true);
        this.eventService = eventService;
        this.memberService = memberService;
        this.event = event;

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiUtils.CONTENT_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setOpaque(false);

        buildTable(mainPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadMembers();
    }

    /**
     * Dựng bang danh sach tat ca thanh vien co trong cau lac bo.
     */
    private void buildTable(JPanel container) {
        String[] cols = { "Mã TV", "Họ tên", "Email", "Loại thành viên" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMembers = new JTable(tableModel);
        UiUtils.styleTable(tblMembers);

        JScrollPane scrollPane = new JScrollPane(tblMembers);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiUtils.BORDER));

        container.add(new JLabel("Chọn một thành viên từ danh sách dưới đây:"), BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tao cac nut hanh dong o duoi cung dialog.
     */
    private JPanel buildButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton btnSave = UiUtils.primaryButton("Xác nhận Đăng ký");
        JButton btnCancel = UiUtils.secondaryButton("Hủy");

        btnSave.addActionListener(e -> onRegisterClicked());
        btnCancel.addActionListener(e -> dispose());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);
        return pnlButtons;
    }

    /**
     * Lay danh sach tu service cua Bien va do vao bang (Giao tiep giua cac Module).
     */
    private void loadMembers() {
        try {
            List<Member> members = memberService.listAllMembers();
            for (Member m : members) {
                Object[] row = {
                        m.getId(),
                        m.getName(),
                        m.getEmail(),
                        UiUtils.membershipLabel(m.getMembershipType())
                };
                tableModel.addRow(row);
            }
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * Lay thong tin thanh vien dang duoc chon trong bang,
     * truyen xuong EventService de luu vao bang event_participants.
     */
    private void onRegisterClicked() {
        int selectedRow = tblMembers.getSelectedRow();
        if (selectedRow < 0) {
            UiUtils.showWarning(this, "Vui lòng chọn một thành viên từ bảng!");
            return;
        }

        int modelRow = tblMembers.convertRowIndexToModel(selectedRow);
        String memberId = (String) tableModel.getValueAt(modelRow, 0);

        try {
            Member selectedMember = memberService.searchMemberById(memberId);
            if (eventService.registerMember(event.getEventId(), selectedMember)) {
                UiUtils.showInfo(this, "Đăng ký thành công!");
                dispose();
            } else {
                UiUtils.showWarning(this, selectedMember.getName() + " đã đăng ký sự kiện này rồi.");
            }
        } catch (EventFullException | InvalidInputException e) {
            UiUtils.showWarning(this, e.getMessage());
        } catch (EventNotFoundException | MemberNotFoundException e) {
            UiUtils.showError(this, e.getMessage());
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }
}