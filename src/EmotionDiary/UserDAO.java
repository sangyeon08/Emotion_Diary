package EmotionDiary;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {
    
    public boolean registerUser(String username, String password, String email) {
        // SQL 쿼리문 준비 (? 는 나중에 값을 넣을 자리)
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. DB 연결
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                System.out.println("DB 연결 실패!");
                return false;
            }
            
            // 2. 비밀번호 암호화 (SHA-256 사용)
            String hashedPassword = hashPassword(password);
            
            // 3. SQL 쿼리 준비
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);      // 첫 번째 ? 에 username 입력
            pstmt.setString(2, hashedPassword); // 두 번째 ? 에 암호화된 비밀번호
            pstmt.setString(3, email);         // 세 번째 ? 에 email 입력
            
            // 4. 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();
            
            // 5. 결과 확인 (1개 이상의 행이 추가되었으면 성공)
            if (rowsAffected > 0) {
                System.out.println("회원가입 성공! 사용자: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            // 중복된 username이 있을 경우 에러 발생
            if (e.getErrorCode() == 1062) { // MySQL 중복 에러 코드
                System.out.println("이미 존재하는 사용자 이름입니다: " + username);
            } else {
                System.out.println("회원가입 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            // 6. 자원 정리 (반드시 해야 함!)
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // 1개 이상이면 중복
            }
            
        } catch (SQLException e) {
            System.out.println("사용자 이름 확인 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            
            // byte 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            System.out.println("암호화 알고리즘을 찾을 수 없습니다.");
            e.printStackTrace();
            return password;
        }
    }
    
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