package clubmanagement.ui;

import clubmanagement.model.Event;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Man hinh QUAN LY SU KIEN (Swing).
 *
 * MODULE: NHAT MINH (Giai doan 3 - Task N3.1)
 * Anh Tu da gan san man hinh nay vao MainFrame (constructor nay do Anh Tu chot,
 * KHONG doi tham so). Nhat Minh chi lam ben trong file nay + tao them cac file MOI
 * EventFormDialog.java, RegisterMemberDialog.java (khong dung cham file cua nguoi khac).
 * Can danh sach thanh vien de chon dang ky -> dung memberService.listAllMembers()
 * (chi DOC, dung sua MemberService).
 *
 * ===== YEU CAU GIAO DIEN =====
 * Bo cuc BorderLayout trong the trang bo goc (UiUtils.card(...)):
 *  - NORTH : thanh cong cu = JComboBox loc trang thai ("Tất cả" + 4 trang thai, hien bang
 *            UiUtils.statusLabel) + nut "Làm mới" o ben trai; cac nut "Thêm sự kiện"
 *            (primaryButton), "Đăng ký thành viên", "Hủy đăng ký" (secondaryButton),
 *            "Đổi trạng thái" (secondaryButton) o ben phai.
 *  - CENTER: JSplitPane doc (chia tren/duoi):
 *      + tren : bang su kien, cot: Mã | Tên sự kiện | Loại | Ngày | Đã đăng ký (x/y) | Phí hiện tại | Trạng thái
 *      + duoi : bang nguoi tham gia cua su kien dang chon, cot: Mã TV | Họ tên | Email | Loại thành viên
 *      Ca hai bang goi UiUtils.styleTable(...) va KHONG cho sua truc tiep o.
 *      Chon 1 dong o bang tren -> bang duoi hien danh sach event.getParticipants().
 *  - EventFormDialog (JDialog modal) them su kien: Mã, Tên, Ngày (dd/MM/yyyy), Số lượng tối đa,
 *    Loại (JComboBox: Workshop / Cuộc thi / Giao lưu). Chon loai nao thi hien dung cac o rieng:
 *      Workshop: Phí cơ bản + Diễn giả | Cuộc thi: Phí tham gia + Giá trị giải thưởng | Giao lưu: Địa điểm.
 *
 * ===== HANH VI =====
 *  - Kiem tra form bang InputValidator (requireNotBlank, requireDate, parsePositiveInt,
 *    parseNonNegativeDouble). Sai -> UiUtils.showWarning(this, e.getMessage()).
 *  - Tao doi tuong dung lop con (new Workshop/Competition/SocialEvent) roi eventService.addEvent(...).
 *  - Bat DuplicateEventException / EventFullException / EventNotFoundException / MemberNotFoundException
 *    -> UiUtils.showError; bat DatabaseException -> UiUtils.showDatabaseError.
 *  - Doi trang thai: hop thoai chon 1 trong 4 trang thai -> eventService.updateEventStatus(...) (method MOI, xem Task N2.2).
 *  - Nhan Huy dang ky: chon nguoi trong bang duoi roi UiUtils.confirm -> eventService.cancelRegistration(...).
 *
 * ===== TIEU CHI NGHIEM THU =====
 *  1. Tao du 3 loai su kien; cot "Phí hiện tại" hien dung gia moi loai (Workshop som &lt;= 20% cho -> giam 10%).
 *  2. Dang ky den khi day cho -> lan tiep theo hien hop thoai loi (EventFullException), khong crash.
 *  3. Loc theo trang thai dung; doi trang thai xong bang cap nhat va van con sau khi khoi dong lai.
 *  4. Khong co System.out.println trong file nay; moi loi bao qua hop thoai.
 */
public class EventPanel extends JPanel implements Refreshable {

    private final EventService eventService;
    private final MemberService memberService;

    // TODO: NHAT MINH - khai bao cac component: JComboBox cbStatusFilter,
    //       JTable tblEvents + tableModel, JTable tblParticipants + participantsModel...

    public EventPanel(EventService eventService, MemberService memberService) {
        this.eventService = eventService;
        this.memberService = memberService;
        setLayout(new BorderLayout());
        setOpaque(false);

        // TODO: NHAT MINH - XOA dong placeholder ben duoi va thay bang bo cuc that (toolbar + split pane).
        add(UiUtils.placeholderPanel("Đang xây dựng", "Nhật Minh", "Task N3.1"), BorderLayout.CENTER);
    }

    /**
     * TODO: NHAT MINH
     * Duoc MainFrame goi moi khi nguoi dung mo trang nay.
     * Doc lai danh sach su kien (co ap dung bo loc trang thai dang chon) roi loadEventTable(...).
     */
    @Override
    public void refresh() {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Do danh sach su kien vao bang tren (moi Event 1 hang). Rong -> bang trong (khong nem loi).
     * Cot "Phi hien tai": UiUtils.formatMoney(event.calculateFee()) - goi calculateFee() truc tiep tren
     * Event, KHONG kiem tra loai (chinh la Polymorphism).
     */
    private void loadEventTable(List<Event> events) {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Do event.getParticipants() vao bang duoi; event == null -> bang duoi trong.
     */
    private void loadParticipantTable(Event event) {
        // TODO: NHAT MINH
    }

    /**
     * TODO: NHAT MINH
     * Tra ve su kien dang chon o bang tren (lay lai bang eventService.findEventById(id) o cot Ma
     * de co du lieu moi nhat), hoac null neu chua chon.
     * Chu y doi chi so view -> model neu bang co sap xep (convertRowIndexToModel).
     */
    private Event getSelectedEvent() {
        // TODO: NHAT MINH
        return null;
    }

    /** TODO: NHAT MINH - Mo EventFormDialog; luu thanh cong -> eventService.addEvent(...) roi refresh(). */
    private void onAddEventClicked() {
        // TODO: NHAT MINH
    }

    /** TODO: NHAT MINH - Chon thanh vien (memberService.listAllMembers()) roi eventService.registerMember(eventId, member). */
    private void onRegisterClicked() {
        // TODO: NHAT MINH
    }

    /** TODO: NHAT MINH - Xac nhan roi eventService.cancelRegistration(eventId, memberId) cho nguoi dang chon o bang duoi. */
    private void onCancelRegistrationClicked() {
        // TODO: NHAT MINH
    }

    /** TODO: NHAT MINH - Hop thoai chon trang thai moi -> eventService.updateEventStatus(eventId, status). */
    private void onChangeStatusClicked() {
        // TODO: NHAT MINH
    }
}
