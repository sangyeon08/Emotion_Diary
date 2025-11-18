package EmotionDiary;

/* 데이터베이스 관련 커스텀 예외 클래스 */

public class DatabaseException extends Exception {
    private String errorCode;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DatabaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        if (errorCode != null) {
            return "DatabaseException [errorCode=" + errorCode + ", message=" + getMessage() + "]";
        }
        return "DatabaseException [message=" + getMessage() + "]";
    }
}

/* 인증 관련 커스텀 예외 */
class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/* 유효성 검증 관련 커스텀 예외 */
class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}