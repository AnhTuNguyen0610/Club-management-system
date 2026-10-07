package clubmanagement.exception;

/**
 * Loi ha tang khi lam viec voi co so du lieu (mat ket noi, sai SQL, khong
 * ghi duoc file...).
 *
 * Ke thua RuntimeException (unchecked) co chu dich: day KHONG phai loi do nguoi
 * dung nhap sai (nhu DuplicateMemberException) nen tang service khong the xu ly
 * tiep. Nho vay chu ky method cua Service/Repository khong phai them
 * "throws SQLException", va UI chi can bat chung mot cho de hien hop thoai loi.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
