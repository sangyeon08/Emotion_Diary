package EmotionDiary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiaryDAO {
    
    /**
     * 일기 작성
     */
    public boolean createDiary(Long userId, String title, String content) {
        String sql = "INSERT INTO diaries (user_id, title, content) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                System.out.println("DB 연결 실패!");
                return false;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, content);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("일기 작성 성공!");
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("일기 작성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * 특정 사용자의 모든 일기 조회
     */
    public List<Diary> getDiariesByUserId(Long userId) {
        List<Diary> diaries = new ArrayList<>();
        String sql = "SELECT * FROM diaries WHERE user_id = ? ORDER BY created_at DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            
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
            
        } catch (SQLException e) {
            System.out.println("일기 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return diaries;
    }
    
    /**
     * 특정 일기 조회
     */
    public Diary getDiaryById(Long diaryId) {
        String sql = "SELECT * FROM diaries WHERE diary_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
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
            
        } catch (SQLException e) {
            System.out.println("일기 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * 일기 수정
     */
    public boolean updateDiary(Long diaryId, String title, String content) {
        String sql = "UPDATE diaries SET title = ?, content = ? WHERE diary_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setLong(3, diaryId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("일기 수정 성공!");
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("일기 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * 일기 삭제
     */
    public boolean deleteDiary(Long diaryId) {
        String sql = "DELETE FROM diaries WHERE diary_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, diaryId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("일기 삭제 성공!");
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("일기 삭제 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * 자원 정리
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("자원 정리 중 오류: " + e.getMessage());
        }
    }
}