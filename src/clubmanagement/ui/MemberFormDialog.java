package clubmanagement.ui;

import clubmanagement.util.InputValidator;
import clubmanagement.exception.InvalidInputException;
import clubmanagement.model.Member;
import clubmanagement.model.MembershipType;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;

/**
 * Dialog dung chung cho Them / Sua thanh vien.
 *
 * - Them: nhap day du thong tin.
 * - Sua: chi cho sua Email va So dien thoai.
 *
 */
public class MemberFormDialog extends JDialog {

    private final JTextField txtId = new JTextField(25);
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtEmail = new JTextField(25);
    private final JTextField txtPhone = new JTextField(25);

    private final JComboBox<MembershipType> cboMembershipType = new JComboBox<>(MembershipType.values());

    private final boolean editMode;

    private Member result;
    private boolean saved = false;

    public MemberFormDialog(Window owner, String title, Member member) {
        super(owner, title, ModalityType.APPLICATION_MODAL);

        this.editMode = member != null;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(owner);

        initForm(member);
        initButtons();
    }

    /**
     * Khoi tao du lieu cho form.
     */
    private void initForm(Member member) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        txtId.setToolTipText("Mã thành viên");
        txtName.setToolTipText("Họ tên");
        txtEmail.setToolTipText("Email");
        txtPhone.setToolTipText("Số điện thoại");

        if (editMode) {
            txtId.setText(member.getId());
            txtName.setText(member.getName());
            txtEmail.setText(member.getEmail());
            txtPhone.setText(member.getPhone());
            cboMembershipType.setSelectedItem(member.getMembershipType());

            // Khi sua: khong cho thay doi ID va loai thanh vien.
            txtId.setEditable(false);
            txtName.setEditable(false);
            cboMembershipType.setEnabled(false);
        }

        addRow(formPanel, 0, "Mã:", txtId);
        addRow(formPanel, 1, "Họ tên:", txtName);
        addRow(formPanel, 2, "Email:", txtEmail);
        addRow(formPanel, 3, "SĐT:", txtPhone);
        addRow(formPanel, 4, "Loại thành viên:", cboMembershipType);

        add(formPanel, BorderLayout.CENTER);
    }

    /**
     * Tao mot dong label + component.
     */
    private void addRow(
            JPanel panel,
            int row,
            String labelText,
            java.awt.Component component) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 5, 6, 10);

        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 5);

        panel.add(component, gbc);
    }

    /**
     * Tao cac nut Huy / Luu.
     */
    private void initButtons() {
        JButton btnCancel = UiUtils.secondaryButton("Hủy");
        JButton btnSave = UiUtils.primaryButton("Lưu");

        btnCancel.addActionListener(e -> {
            saved = false;
            result = null;
            dispose();
        });

        btnSave.addActionListener(e -> save());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 20, 15, 20));

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnSave);
    }

    /**
     * Kiem tra du lieu va tao Member.
     */
    private void save() {
        try {
            String id = InputValidator.requireNotBlank(
                    "Mã thành viên",
                    txtId.getText());

            String name = InputValidator.requireNotBlank(
                    "Họ tên",
                    txtName.getText());

            String email = InputValidator.requireEmail(
                    txtEmail.getText());

            String phone = InputValidator.optionalPhone(
                    txtPhone.getText());

            MembershipType membershipType = (MembershipType) cboMembershipType.getSelectedItem();

            if (membershipType == null) {
                throw new InvalidInputException(
                        "Vui lòng chọn loại thành viên.");
            }

            if (editMode) {
                /*
                 * Khi sua, MemberService chi cap nhat Email + SĐT.
                 * Ta giu nguyen ID, ten, loai thanh vien va ngay tham gia.
                 */
                result = new Member(
                        id,
                        name,
                        email,
                        phone,
                        membershipType,
                        LocalDate.now());
            } else {
                /*
                 * Thanh vien moi co ngay tham gia la ngay hien tai.
                 */
                result = new Member(
                        id,
                        name,
                        email,
                        phone,
                        membershipType,
                        LocalDate.now());
            }

            saved = true;
            dispose();

        } catch (InvalidInputException e) {
            UiUtils.showWarning(this, e.getMessage());
        }
    }

    /**
     * Mo dialog va tra ve Member neu nguoi dung bam Luu.
     * Tra ve null neu bam Huy.
     */
    public Member showDialog() {
        setVisible(true);
        return saved ? result : null;
    }
}