package EmotionDiary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* 일기 관련 데이터베이스 작업 클래스 */

public class DiaryDAO extends BaseDAO {
    
    public Diary createDiary(Long userId, String title, String content) 
            throws DatabaseException, ValidationException {
        
        // 유효성 검증
        validateDiary(title, content);
        
        String sql = "INSERT INTO diaries (user_id, title, content) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, content);
            
            logQuery(sql, userId, title, content.substring(0, Math.min(50, content.length())) + "...");
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    Long diaryId = rs.getLong(1);
                    System.out.println("일기 작성 성공! (ID: " + diaryId + ")");
                    
                    // 생성된 일기 반환
                    return getDiaryById(diaryId);
                }
            }
            
            throw new DatabaseException("일기 작성 실패", "DIARY_CREATE_FAILED");
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 작성 중 오류 발생", "DIARY_CREATE_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    public List<Diary> getDiariesByUserId(Long userId) throws DatabaseException {
        List<Diary> diaries = new ArrayList<>();
        String sql = "SELECT * FROM diaries WHERE user_id = ? ORDER BY created_at DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            
            logQuery(sql, userId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Diary diary = new Diary(
                    rs.getLong("diary_id"),
                    rs.getLong("user_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
                diaries.add(diary);
            }
            
            System.out.println("일기 " + diaries.size() + "개 조회 완료");
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 목록 조회 중 오류", "DIARY_LIST_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return diaries;
    }
    
    /* 특정 일기 조회 */

    public Diary getDiaryById(Long diaryId) throws DatabaseException {
        String sql = "SELECT * FROM diaries WHERE diary_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, diaryId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Diary(
                    rs.getLong("diary_id"),
                    rs.getLong("user_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 조회 중 오류", "DIARY_FETCH_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 일기 수정 */
    public boolean updateDiary(Long diaryId, String title, String content) 
            throws DatabaseException, ValidationException {
        
        validateDiary(title, content);
        
        String sql = "UPDATE diaries SET title = ?, content = ? WHERE diary_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setLong(3, diaryId);
            
            logQuery(sql, title, content.substring(0, Math.min(50, content.length())) + "...", diaryId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("일기 수정 성공! (ID: " + diaryId + ")");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 수정 중 오류", "DIARY_UPDATE_ERROR", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /* 일기 삭제 */

    public boolean deleteDiary(Long diaryId) throws DatabaseException {
        String sql = "DELETE FROM diaries WHERE diary_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, diaryId);
            
            logQuery(sql, diaryId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("일기 삭제 성공! (ID: " + diaryId + ")");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 삭제 중 오류", "DIARY_DELETE_ERROR", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /* 일기 검색 */

    public List<Diary> searchDiaries(Long userId, String keyword) throws DatabaseException {
        List<Diary> diaries = new ArrayList<>();
        String sql = "SELECT * FROM diaries WHERE user_id = ? AND (title LIKE ? OR content LIKE ?) ORDER BY created_at DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            logQuery(sql, userId, searchPattern, searchPattern);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Diary diary = new Diary(
                    rs.getLong("diary_id"),
                    rs.getLong("user_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
                diaries.add(diary);
            }
            
            System.out.println("🔍 검색 결과: " + diaries.size() + "개");
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 검색 중 오류", "DIARY_SEARCH_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return diaries;
    }
    
    /* 일기 통계 */

    public int getDiaryCount(Long userId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM diaries WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("일기 개수 조회 중 오류", "DIARY_COUNT_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 일기 유효성 검증 */
    private void validateDiary(String title, String content) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("일기 제목은 필수입니다.");
        }
        if (title.length() > 100) {
            throw new ValidationException("일기 제목은 100글자를 초과할 수 없습니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("일기 내용은 필수입니다.");
        }
    }
}