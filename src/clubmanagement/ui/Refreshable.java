package clubmanagement.ui;

/**
 * Interface cho moi man hinh (panel) co the "tai lai du lieu".
 * MainFrame goi refresh() moi khi nguoi dung chuyen sang man hinh do, nhung
 * MainFrame khong can biet ben trong man hinh la gi (Polymorphism qua interface).
 */
public interface Refreshable {

    /** Doc lai du lieu tu Service va ve lai man hinh. */
    void refresh();
}
