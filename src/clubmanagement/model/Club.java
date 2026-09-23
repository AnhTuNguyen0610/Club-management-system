package clubmanagement.model;

/**
 * Dai dien cho Cau lac bo. Chi luu thong tin co ban.
 * Viec quan ly Member/Event duoc tach rieng sang cac Service
 * (association, khong ke thua) de tranh mot class "God Object".
 */
public class Club {

    private String clubName;
    private String description;

    public Club(String clubName, String description) {
        this.clubName = clubName;
        this.description = description;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CLB: " + clubName + " - " + description;
    }
}
