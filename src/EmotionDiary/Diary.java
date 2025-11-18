package EmotionDiary;

/* 일기 정보를 담는 클래스 */

public class Diary {
    private Long diaryId;
    private Long userId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    
    // 기본 생성자
    public Diary() {
    }
    
    // 일기 작성용 생성자
    public Diary(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
    
    // 전체 생성자
    public Diary(Long diaryId, Long userId, String title, String content, 
                 String createdAt, String updatedAt) {
        this.diaryId = diaryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter & Setter
    public Long getDiaryId() {
        return diaryId;
    }
    
    public void setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Diary{" +
                "diaryId=" + diaryId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}