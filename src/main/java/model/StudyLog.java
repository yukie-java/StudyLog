package model;

public class StudyLog {
    private int id;
    private String userId;
    private String studyDate; // まずは String でOK（"2026-02-18"）
    private String subject;
    private int minutes;
    private String memo;

    public StudyLog() {}

    public StudyLog(String userId, String studyDate, String subject, int minutes, String memo) {
        this.userId = userId;
        this.studyDate = studyDate;
        this.subject = subject;
        this.minutes = minutes;
        this.memo = memo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStudyDate() { return studyDate; }
    public void setStudyDate(String studyDate) { this.studyDate = studyDate; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = minutes; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
