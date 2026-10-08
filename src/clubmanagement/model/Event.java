package clubmanagement.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Lop truu tuong dai dien cho mot su kien cua CLB.
 * Implement Payable -> moi loai su kien phai tu tinh phi rieng (Polymorphism).
 * Danh sach nguoi tham gia duoc bao ve (Encapsulation): ben ngoai chi doc duoc,
 * muon them phai goi addParticipant().
 */
public abstract class Event implements Payable {

    protected String eventId;
    protected String eventName;
    protected String date;
    protected int maxParticipants;
    protected final List<Member> participants;
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

    /** Tra ve danh sach CHI DOC cac thanh vien da dang ky. */
    public List<Member> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public void addParticipant(Member member) {
        participants.add(member);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public boolean isFull() {
        return participants.size() >= maxParticipants;
    }

    public boolean hasParticipant(String memberId) {
        for (Member m : participants) {
            if (m.getId().equalsIgnoreCase(memberId)) {
                return true;
            }
        }
        return false;
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

    /** Hai su kien la mot neu cung ma (khong phan biet hoa/thuong). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return eventId != null && eventId.equalsIgnoreCase(((Event) o).eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId == null ? null : eventId.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return "ID: " + eventId
                + " - Su kien: " + eventName + " (" + getEventTypeDescription() + ")"
                + ", Ngay: " + date
                + ", So luong: " + participants.size() + "/" + maxParticipants
                + ", Trang thai: " + status;
    }
}
