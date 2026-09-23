package clubmanagement.model;

/**
 * Lop truu tuong (abstract) dai dien cho mot con nguoi trong he thong.
 * The hien Encapsulation (field private/protected + getter/setter)
 * va la nen tang cho Inheritance (Member se ke thua lop nay).
 */
public abstract class Person {

    protected String id;
    protected String name;
    protected String email;
    protected String phone;

    public Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Moi lop con phai mo ta duoc vai tro cua no trong he thong.
     * The hien Abstraction + Polymorphism.
     */
    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return "ID: " + id + ", Ten: " + name + ", Email: " + email + ", SDT: " + phone;
    }
}
