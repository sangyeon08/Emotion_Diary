package EmotionDiary;

import java.sql.*;

/* 모든 DAO의 부모 클래스 */

public abstract class BaseDAO {
    
    protected Connection getConnection() throws DatabaseException {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                throw new DatabaseException("데이터베이스 연결 실패", "DB_CONNECTION_FAILED");
            }
            return conn;
        } catch (Exception e) {
            throw new DatabaseException("데이터베이스 연결 중 오류 발생", "DB_ERROR", e);
        }
    }
    
    protected void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("ResultSet 닫기 실패: " + e.getMessage());
        }
        
        try {
            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }
        } catch (SQLException e) {
            System.err.println("PreparedStatement 닫기 실패: " + e.getMessage());
        }
        
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Connection 닫기 실패: " + e.getMessage());
        }
    }
    
    protected void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    protected void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    protected void rollbackTransaction(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("트랜잭션 롤백 실패: " + e.getMessage());
        }
    }
    
    protected void logQuery(String query, Object... params) {
        System.out.println("[SQL] " + query);
        if (params.length > 0) {
            System.out.print("[PARAMS] ");
            for (int i = 0; i < params.length; i++) {
                System.out.print(params[i] + (i < params.length - 1 ? ", " : ""));
            }
            System.out.println();
        }
    }
}