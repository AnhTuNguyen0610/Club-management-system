package clubmanagement.ui;

import clubmanagement.exception.DatabaseException;
import clubmanagement.exception.DuplicateEventException;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.model.Competition;
import clubmanagement.model.Event;
import clubmanagement.model.SocialEvent;
import clubmanagement.model.Workshop;
import clubmanagement.service.EventService;

import javax.swing.*;
import java.awt.*;

/**
 * Hop thoai tao su kien moi.
 * Hien thi cac truong nhap lieu dong thoi thay doi giao dien
 * tuy thuoc vao loai su kien (Workshop, Cuoc thi, Giao luu) ma nguoi dung chon.
 */
public class EventFormDialog extends JDialog {

    private final EventService eventService;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtDate;
    private JTextField txtMax;
    private JComboBox<String> cbType;

    private JPanel pnlWorkshop;
    private JTextField txtBaseFee;
    private JTextField txtSpeaker;

    private JPanel pnlCompetition;
    private JTextField txtEntryFee;
    private JTextField txtPrize;

    private JPanel pnlSocial;
    private JTextField txtLocation;

    /**
     * Khoi tao form, xac dinh bo cuc va cac thanh phan UI.
     */
    public EventFormDialog(Frame parent, EventService eventService) {
        super(parent, "Thêm sự kiện mới", true);
        this.eventService = eventService;

        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiUtils.CONTENT_BG);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        buildCommonFields(mainPanel);
        buildSpecificFields(mainPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        updateSpecificFieldsVisibility();
    }

    /**
     * Tao cac truong nhap lieu chung ma su kien nao cung phai co.
     */
    private void buildCommonFields(JPanel container) {
        JPanel pnlCommon = new JPanel(new GridLayout(5, 2, 10, 10));
        pnlCommon.setOpaque(false);

        txtId = new JTextField();
        txtName = new JTextField();
        txtDate = new JTextField();
        txtMax = new JTextField();
        cbType = new JComboBox<>(new String[] { "Workshop", "Cuộc thi", "Giao lưu" });

        cbType.addActionListener(e -> updateSpecificFieldsVisibility());

        pnlCommon.add(new JLabel("Mã sự kiện:"));
        pnlCommon.add(txtId);
        pnlCommon.add(new JLabel("Tên sự kiện:"));
        pnlCommon.add(txtName);
        pnlCommon.add(new JLabel("Ngày (dd/MM/yyyy):"));
        pnlCommon.add(txtDate);
        pnlCommon.add(new JLabel("Số người tối đa:"));
        pnlCommon.add(txtMax);
        pnlCommon.add(new JLabel("Loại sự kiện:"));
        pnlCommon.add(cbType);

        container.add(pnlCommon);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    /**
     * Tao cac truong nhap lieu rieng biet cho tung loai su kien.
     */
    private void buildSpecificFields(JPanel container) {
        pnlWorkshop = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlWorkshop.setOpaque(false);
        txtBaseFee = new JTextField();
        txtSpeaker = new JTextField();
        pnlWorkshop.add(new JLabel("Phí cơ bản:"));
        pnlWorkshop.add(txtBaseFee);
        pnlWorkshop.add(new JLabel("Diễn giả:"));
        pnlWorkshop.add(txtSpeaker);

        pnlCompetition = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlCompetition.setOpaque(false);
        txtEntryFee = new JTextField();
        txtPrize = new JTextField();
        pnlCompetition.add(new JLabel("Phí tham gia:"));
        pnlCompetition.add(txtEntryFee);
        pnlCompetition.add(new JLabel("Giá trị giải thưởng:"));
        pnlCompetition.add(txtPrize);

        pnlSocial = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlSocial.setOpaque(false);
        txtLocation = new JTextField();
        pnlSocial.add(new JLabel("Địa điểm:"));
        pnlSocial.add(txtLocation);

        container.add(pnlWorkshop);
        container.add(pnlCompetition);
        container.add(pnlSocial);
    }

    /**
     * Tao thanh chua cac nut chuc nang Luu va Huy o cuoi dialog.
     */
    private JPanel buildButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton btnSave = UiUtils.primaryButton("Lưu sự kiện");
        JButton btnCancel = UiUtils.secondaryButton("Hủy");

        btnSave.addActionListener(e -> onSaveClicked());
        btnCancel.addActionListener(e -> dispose());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);
        return pnlButtons;
    }

    /**
     * An/hien cac form phu tuong ung voi loai su kien dang duoc chon trong
     * JComboBox.
     */
    private void updateSpecificFieldsVisibility() {
        int index = cbType.getSelectedIndex();
        pnlWorkshop.setVisible(index == 0);
        pnlCompetition.setVisible(index == 1);
        pnlSocial.setVisible(index == 2);
    }

    /**
     * Kiem tra tinh hop le cua du lieu nguoi dung nhap.
     * Neu hop le se goi Service de luu vao CSDL, dong thoi dong cua so.
     */
    private void onSaveClicked() {
        try {
            String id = InputValidator.requireNotBlank("Mã sự kiện", txtId.getText());
            String name = InputValidator.requireNotBlank("Tên sự kiện", txtName.getText());
            String date = InputValidator.requireDate("Ngày tổ chức", txtDate.getText());
            int max = InputValidator.parsePositiveInt("Số người tối đa", txtMax.getText());

            Event event = null;
            int typeIndex = cbType.getSelectedIndex();

            if (typeIndex == 0) {
                double fee = InputValidator.parseNonNegativeDouble("Phí cơ bản", txtBaseFee.getText());
                String speaker = InputValidator.requireNotBlank("Diễn giả", txtSpeaker.getText());
                event = new Workshop(id, name, date, max, fee, speaker);
            } else if (typeIndex == 1) {
                double fee = InputValidator.parseNonNegativeDouble("Phí tham gia", txtEntryFee.getText());
                double prize = InputValidator.parseNonNegativeDouble("Giải thưởng", txtPrize.getText());
                event = new Competition(id, name, date, max, fee, prize);
            } else if (typeIndex == 2) {
                String location = InputValidator.requireNotBlank("Địa điểm", txtLocation.getText());
                event = new SocialEvent(id, name, date, max, location);
            }

            eventService.addEvent(event);
            dispose();

        } catch (InvalidInputException | DuplicateEventException e) {
            UiUtils.showWarning(this, e.getMessage());
        } catch (DatabaseException e) {
            UiUtils.showDatabaseError(this, e);
        }
    }
}