package clubmanagement.ui;

import clubmanagement.exception.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Kiem tra du lieu nguoi dung nhap tren FORM (truoc khi goi Service).
 * Sai -> nem InvalidInputException voi thong bao tieng Viet san sang hien cho nguoi dung.
 *
 * Tach rieng de Bien va Nhat Minh dung CHUNG (khong moi nguoi tu viet mot kieu),
 * thong bao loi dong nhat. Cach dung trong onSaveClicked():
 * <pre>
 * try {
 *     String name  = InputValidator.requireNotBlank("Họ tên", txtName.getText());
 *     String email = InputValidator.requireEmail(txtEmail.getText());
 *     ...
 * } catch (InvalidInputException e) {
 *     UiUtils.showWarning(this, e.getMessage());
 *     return;
 * }
 * </pre>
 * Quan ly boi Anh Tu. Bien/Nhat Minh chi goi, khong sua.
 */
public final class InputValidator {

    /** Dinh dang ngay cua su kien: dd/MM/yyyy (STRICT: 31/02/2026 bi tu choi). */
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d{9,12}$");

    private InputValidator() {
    }

    /** Bat buoc khac rong. Tra ve chuoi da trim(). */
    public static String requireNotBlank(String fieldName, String value) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " không được để trống.");
        }
        return value.trim();
    }

    /** Email hop le dang a@b.c. Tra ve chuoi da trim(). */
    public static String requireEmail(String value) throws InvalidInputException {
        String email = requireNotBlank("Email", value);
        if (!EMAIL.matcher(email).matches()) {
            throw new InvalidInputException("Email không đúng định dạng (ví dụ: ten@gmail.com).");
        }
        return email;
    }

    /** So dien thoai KHONG bat buoc: de trong thi hop le; neu nhap thi 9-12 chu so (co the co dau +). */
    public static String optionalPhone(String value) throws InvalidInputException {
        String phone = value == null ? "" : value.trim();
        if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
            throw new InvalidInputException("Số điện thoại chỉ gồm 9-12 chữ số (có thể bắt đầu bằng +).");
        }
        return phone;
    }

    /** So nguyen duong (> 0). */
    public static int parsePositiveInt(String fieldName, String value) throws InvalidInputException {
        String text = requireNotBlank(fieldName, value);
        try {
            int number = Integer.parseInt(text);
            if (number <= 0) {
                throw new InvalidInputException(fieldName + " phải lớn hơn 0.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " phải là số nguyên.");
        }
    }

    /** So thuc khong am (>= 0), chap nhan 0. */
    public static double parseNonNegativeDouble(String fieldName, String value) throws InvalidInputException {
        String text = requireNotBlank(fieldName, value);
        try {
            double number = Double.parseDouble(text);
            if (number < 0 || Double.isNaN(number) || Double.isInfinite(number)) {
                throw new InvalidInputException(fieldName + " không được là số âm.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " phải là số.");
        }
    }

    /** Ngay hop le theo dd/MM/yyyy. Tra ve chuoi da trim() (giu nguyen dinh dang nhap). */
    public static String requireDate(String fieldName, String value) throws InvalidInputException {
        String text = requireNotBlank(fieldName, value);
        try {
            LocalDate.parse(text, DATE_FORMAT);
            return text;
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(fieldName + " phải đúng định dạng " + DATE_PATTERN
                    + " và là ngày có thật (ví dụ: 20/11/2026).");
        }
    }
}
