package clubmanagement.model;

/**
 * Bat ky doi tuong nao co the tinh duoc mot khoan phi tham gia
 * deu phai implement interface nay.
 * Day la vi du cho khai niem Interface va Polymorphism:
 * moi loai Event se co cach tinh phi (calculateFee) khac nhau.
 */
public interface Payable {

    double calculateFee();
}
