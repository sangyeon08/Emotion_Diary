package EmotionDiary;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* 사용자 관련 데이터베이스 작업을 처리하는 DAO 클래스 */

public class UserDAO extends BaseDAO {
    
    /* 회원가입 처리 */

    public User registerUser(String username, String password, String email) 
            throws DatabaseException, ValidationException {
        
        // 입력 유효성 검증
        validateUsername(username);
        validatePassword(password);
        
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 중복 확인
            if (isUsernameTaken(username)) {
                throw new ValidationException("이미 사용 중인 사용자 이름입니다: " + username);
            }
            
            conn = getConnection();
            
            // 비밀번호 암호화
            String hashedPassword = hashPassword(password);
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            
            logQuery(sql, username, "****", email);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // 생성된 사용자 ID 가져오기
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    Long userId = rs.getLong(1);
                    System.out.println("회원가입 성공! 사용자: " + username + " (ID: " + userId + ")");
                    
                    // 새로 생성된 사용자 정보 반환
                    return getUserById(userId);
                }
            }
            
            throw new DatabaseException("회원가입 실패: 사용자 생성 안됨", "USER_CREATE_FAILED");
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL 중복 에러
                throw new ValidationException("이미 존재하는 사용자 이름입니다: " + username);
            }
            throw new DatabaseException("회원가입 중 오류 발생", "REGISTER_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 로그인 처리 */

    public User loginUser(String username, String password) 
            throws DatabaseException, AuthenticationException {
        
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            logQuery(sql, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);
                
                // 비밀번호 확인
                if (storedHash.equals(inputHash)) {
                    System.out.println("로그인 성공: " + username);
                    
                    return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                    );
                } else {
                    throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
                }
            } else {
                throw new AuthenticationException("존재하지 않는 사용자입니다: " + username);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("로그인 중 오류 발생", "LOGIN_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 사용자 이름 중복 확인 */

    public boolean isUsernameTaken(String username) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new DatabaseException("사용자 이름 확인 중 오류", "USERNAME_CHECK_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 사용자 ID로 사용자 조회 */

    public User getUserById(Long userId) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("email"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                );
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("사용자 조회 중 오류", "USER_FETCH_ERROR", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /* 비밀번호 해시화 (SHA-256) */

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            
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
            System.err.println("암호화 알고리즘을 찾을 수 없습니다.");
            e.printStackTrace();
            return password; // fallback (실제로는 예외를 던져야 함)
        }
    }
    
    /* 사용자 이름 유효성 검사 */

    private void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("사용자 이름은 필수입니다.");
        }
        if (username.length() < 3) {
            throw new ValidationException("사용자 이름은 3글자 이상이어야 합니다.");
        }
        if (username.length() > 50) {
            throw new ValidationException("사용자 이름은 50글자를 초과할 수 없습니다.");
        }
    }
    
    /* 비밀번호 유효성 검사 */

    private void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("비밀번호는 필수입니다.");
        }
        if (password.length() < 4) {
            throw new ValidationException("비밀번호는 4글자 이상이어야 합니다.");
        }
    }
}