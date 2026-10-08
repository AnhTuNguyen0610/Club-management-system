package clubmanagement.ui;

import clubmanagement.util.InputValidator;
import clubmanagement.exception.DatabaseException;
import clubmanagement.exception.DuplicateMemberException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.exception.MemberNotFoundException;
import clubmanagement.model.Member;
import clubmanagement.service.MemberService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Man hinh QUAN LY THANH VIEN (Swing).
 *
 * Anh Tu da gan san man hinh nay vao MainFrame (constructor nay do Anh Tu chot,
 * KHONG doi tham so). Bien chi lam ben trong file nay + tao them file
 * MemberFormDialog.java (file MOI, khong dung cham file cua nguoi khac).
 *
 * ===== YEU CAU GIAO DIEN =====
 * Bo cuc BorderLayout, dat trong 1 the trang bo goc (UiUtils.card(...)):
 *  - NORTH : thanh cong cu = o tim kiem (JTextField) + nut "Tìm" + nut "Làm mới"
 *            o ben trai; nut "Thêm" (UiUtils.primaryButton), "Sửa"
 *            (UiUtils.secondaryButton), "Xóa" (UiUtils.dangerButton) o ben phai.
 *  - CENTER: JTable trong JScrollPane, goi UiUtils.styleTable(table).
 *            Cot: Mã | Họ tên | Email | Số điện thoại | Loại thành viên | Ngày tham gia | Trạng thái
 *            Model KHONG cho sua truc tiep o (override isCellEditable -> false).
 *            Bam tieu de cot de sap xep da co san nho styleTable (thay cho nut "sap xep").
 *  - Dialog them/sua (MemberFormDialog, JDialog modal): Mã, Họ tên, Email, Số điện thoại,
 *    Loại thành viên (JComboBox<MembershipType>). Khi SUA: o Mã va Loai bi khoa
 *    (memberService.updateMember chi doi email + so dien thoai).
 *
 * ===== HANH VI =====
 *  - Kiem tra du lieu form bang InputValidator (email, so dien thoai...). Sai -> UiUtils.showWarning.
 *  - Bat DuplicateMemberException / InvalidInputException / MemberNotFoundException
 *    -> UiUtils.showError(this, e.getMessage()). Bat DatabaseException -> UiUtils.showDatabaseError.
 *  - Xoa: luon hoi UiUtils.confirm(...) truoc; chua chon dong -> UiUtils.showWarning("Vui long chon...").
 *  - Nhan nhan Lam moi / them / sua / xoa xong thi goi lai loadTable(...) de bang cap nhat.
 *  - Hien thi Loai thanh vien bang UiUtils.membershipLabel(...), ngay bang toString() cua LocalDate
 *    hoac dinh dang dd/MM/yyyy (nhom tu thong nhat).
 *
 * ===== TIEU CHI NGHIEM THU =====
 *  1. Them thanh vien -> xuat hien trong bang; them trung ID -> hop thoai bao loi, khong crash.
 *  2. Sua email/SDT -> bang cap nhat; xoa co xac nhan; tim theo ten "an" ra ca "Nguyễn Văn An" lan "Anh".
 *  3. Tat va mo lai chuong trinh (sau khi xong Task B2.2) -> du lieu van con.
 *  4. Khong co System.out.println trong file nay; moi loi bao qua hop thoai.
 */
public class MemberPanel extends JPanel implements Refreshable {

    private final MemberService memberService;

    private JTextField txtSearch;
    private JTable tblMembers;
    private DefaultTableModel tableModel;

    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;

    public MemberPanel(MemberService memberService) {
        this.memberService = memberService;

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        refresh();
    }

    /**
     * Tao thanh cong cu:
     * - Ben trai: o tim kiem + Tim + Lam moi
     * - Ben phai: Them + Sua + Xoa
     */
    private JPanel buildToolbar() {
        JPanel toolbar = UiUtils.card(new BorderLayout(10, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        txtSearch = new JTextField(20);

        btnSearch = UiUtils.secondaryButton("Tìm");
        btnRefresh = UiUtils.secondaryButton("Làm mới");

        leftPanel.add(new JLabel("Tìm theo tên:"));
        leftPanel.add(txtSearch);
        leftPanel.add(btnSearch);
        leftPanel.add(btnRefresh);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        btnAdd = UiUtils.primaryButton("Thêm");
        btnEdit = UiUtils.secondaryButton("Sửa");
        btnDelete = UiUtils.dangerButton("Xóa");

        rightPanel.add(btnAdd);
        rightPanel.add(btnEdit);
        rightPanel.add(btnDelete);

        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        btnSearch.addActionListener(e -> onSearchClicked());

        btnRefresh.addActionListener(e -> refresh());

        btnAdd.addActionListener(e -> onAddClicked());

        btnEdit.addActionListener(e -> onEditClicked());

        btnDelete.addActionListener(e -> onDeleteClicked());

        txtSearch.addActionListener(e -> onSearchClicked());

        return toolbar;
    }

    /**
     * Tao JTable hien thi danh sach thanh vien.
     */
    private JPanel buildTable() {
        String[] columns = {
                "Mã",
                "Họ tên",
                "Email",
                "Số điện thoại",
                "Loại thành viên",
                "Ngày tham gia",
                "Trạng thái"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMembers = new JTable(tableModel);

        UiUtils.styleTable(tblMembers);

        JScrollPane scrollPane = new JScrollPane(tblMembers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = UiUtils.card(new BorderLayout());
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * MainFrame goi ham nay khi mo trang.
     */
    @Override
    public void refresh() {
        try {
            List<Member> members = memberService.listAllMembers();
            loadTable(members);
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * Xoa bang cu va nap lai danh sach thanh vien.
     */
    private void loadTable(List<Member> members) {
        tableModel.setRowCount(0);

        if (members == null) {
            return;
        }

        for (Member member : members) {
            tableModel.addRow(new Object[]{
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    UiUtils.membershipLabel(member.getMembershipType()),
                    member.getJoinDate(),
                    member.isActive() ? "Đang hoạt động" : "Ngừng hoạt động"
            });
        }
    }

    /**
     * Lay Member dang duoc chon.
     * Do JTable co sorter nen phai convert view index -> model index.
     */
    private Member getSelectedMember() {
        int viewRow = tblMembers.getSelectedRow();

        if (viewRow < 0) {
            return null;
        }

        int modelRow = tblMembers.convertRowIndexToModel(viewRow);

        String memberId = tableModel.getValueAt(modelRow, 0).toString();

        try {
            return memberService.searchMemberById(memberId);
        } catch (MemberNotFoundException e) {
            UiUtils.showError(this, e.getMessage());
            return null;
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
            return null;
        }
    }

    /**
     * Mo dialog THEM.
     */
    private void onAddClicked() {
        MemberFormDialog dialog = new MemberFormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Thêm thành viên",
                null);

        Member input = dialog.showDialog();
        if (input == null) {
            return;
        }

        try {
            memberService.addMember(input);
            refresh();
            UiUtils.showInfo(this, "Đã thêm thành viên " + input.getName() + ".");
        } catch (DuplicateMemberException | InvalidInputException e) {
            UiUtils.showError(this, e.getMessage());
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * Mo dialog SUA.
     */
    private void onEditClicked() {
        Member selectedMember = getSelectedMember();

        if (selectedMember == null) {
            UiUtils.showWarning(this, "Vui lòng chọn một thành viên.");
            return;
        }

        MemberFormDialog dialog = new MemberFormDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Sửa thành viên",
                selectedMember);

        Member input = dialog.showDialog();
        if (input == null) {
            return;
        }

        try {
            memberService.updateMember(
                    selectedMember.getId(),
                    input.getEmail(),
                    input.getPhone());
            refresh();
            UiUtils.showInfo(this, "Đã cập nhật thông tin thành viên.");
        } catch (MemberNotFoundException | InvalidInputException e) {
            UiUtils.showError(this, e.getMessage());
            refresh();
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * Xoa thanh vien sau khi xac nhan.
     */
    private void onDeleteClicked() {
        Member selectedMember = getSelectedMember();

        if (selectedMember == null) {
            UiUtils.showWarning(this, "Vui lòng chọn một thành viên.");
            return;
        }

        boolean confirmed = UiUtils.confirm(
                this,
                "Bạn có chắc muốn xóa thành viên \""
                        + selectedMember.getName()
                        + "\" (" + selectedMember.getId() + ")?\n"
                        + "Các đăng ký sự kiện của thành viên này cũng sẽ bị xóa."
        );

        if (!confirmed) {
            return;
        }

        try {
            memberService.removeMember(selectedMember.getId());

            refresh();
            UiUtils.showInfo(this, "Đã xóa thành viên " + selectedMember.getName() + ".");

        } catch (MemberNotFoundException e) {
            UiUtils.showError(this, e.getMessage());

        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }

    /**
     * Tim theo ten.
     * Rong -> hien tat ca.
     */
    private void onSearchClicked() {
        String keyword = txtSearch.getText().trim();

        try {
            if (keyword.isEmpty()) {
                refresh();
            } else {
                List<Member> members =
                        memberService.searchMemberByName(keyword);

                loadTable(members);
            }

        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }
}
