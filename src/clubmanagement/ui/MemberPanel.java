package clubmanagement.ui;

import clubmanagement.model.Member;
import clubmanagement.service.MemberService;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Man hinh QUAN LY THANH VIEN (Swing).
 *
 * MODULE: BIEN (Giai doan 3 - Task B3.1)
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
 *    Loại thành viên (JComboBox&lt;MembershipType&gt;). Khi SUA: o Mã va Loai bi khoa
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

    // TODO: BIEN - khai bao cac component can dung: JTextField txtSearch,
    //       JTable tblMembers, DefaultTableModel tableModel...

    public MemberPanel(MemberService memberService) {
        this.memberService = memberService;
        setLayout(new BorderLayout());
        setOpaque(false);

        // TODO: BIEN - XOA dong placeholder ben duoi va thay bang bo cuc that (buildToolbar + buildTable).
        add(UiUtils.placeholderPanel("Đang xây dựng", "Biên", "Task B3.1"), BorderLayout.CENTER);
    }

    /**
     * TODO: BIEN
     * Duoc MainFrame goi moi khi nguoi dung mo trang nay.
     * Lay danh sach tu memberService.listAllMembers() roi do vao bang (loadTable).
     */
    @Override
    public void refresh() {
        // TODO: BIEN
    }

    /**
     * TODO: BIEN
     * Xoa het hang cu trong tableModel roi them 1 hang cho moi Member.
     * Danh sach rong -> bang trong (khong nem loi).
     */
    private void loadTable(List<Member> members) {
        // TODO: BIEN
    }

    /**
     * TODO: BIEN
     * Tra ve Member ung voi dong dang chon, hoac null neu chua chon dong nao.
     * Chu y: neu bang dang duoc sap xep, phai doi chi so view -> model
     * (table.convertRowIndexToModel) hoac lay ID tu cot dau roi searchMemberById.
     */
    private Member getSelectedMember() {
        // TODO: BIEN
        return null;
    }

    /** TODO: BIEN - Mo MemberFormDialog che do THEM; neu luu thanh cong thi goi memberService.addMember(...) va refresh. */
    private void onAddClicked() {
        // TODO: BIEN
    }

    /** TODO: BIEN - Mo MemberFormDialog che do SUA cho thanh vien dang chon; luu bang memberService.updateMember(...). */
    private void onEditClicked() {
        // TODO: BIEN
    }

    /** TODO: BIEN - Xac nhan roi memberService.removeMember(id) cho thanh vien dang chon. */
    private void onDeleteClicked() {
        // TODO: BIEN
    }

    /** TODO: BIEN - Doc o tim kiem; rong -> hien tat ca; nguoc lai memberService.searchMemberByName(keyword). */
    private void onSearchClicked() {
        // TODO: BIEN
    }
}
