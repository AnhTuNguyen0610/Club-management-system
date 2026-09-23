package clubmanagement.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Lop truu tuong dai dien cho mot su kien cua CLB.
 * Implement Payable -> moi loai su kien phai tu tinh phi rieng (Polymorphism).
 *
 * Cac field/constructor/getter dung chung da duoc dung san boi Tech Lead (Anh Tu).
 * Logic tinh phi (calculateFee) thuoc MODULE: NHAT MINH, cai dat trong
 * cac lop con Workshop / Competition / SocialEvent.
 */
public abstract class Event implements Payable {

    protected String eventId;
    protected String eventName;
    protected String date;
    protected int maxParticipants;
    protected List<Member> participants;
    protected EventStatus status;

    public Event(String eventId, String eventName, String date, int maxParticipants) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.date = date;
        this.maxParticipants = maxParticipants;
        this.participants = new ArrayList<>();
        this.status = EventStatus.UPCOMING;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDate() {
        return date;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public List<Member> getParticipants() {
        return participants;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * Mo ta loai su kien (Workshop, Cuoc thi, Giao luu...).
     * The hien Abstraction + Polymorphism.
     */
    public abstract String getEventTypeDescription();

    @Override
    public String toString() {
        return "Su kien: " + eventName + " (" + getEventTypeDescription() + ")"
                + ", Ngay: " + date
                + ", So luong: " + participants.size() + "/" + maxParticipants
                + ", Trang thai: " + status;
    }
}
