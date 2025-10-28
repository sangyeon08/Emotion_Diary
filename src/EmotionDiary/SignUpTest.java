package EmotionDiary;

import java.util.Scanner;

/**
 * 회원가입 기능을 테스트하는 메인 클래스
 * 나중에 GUI나 웹 페이지로 바꿀 수 있어요!
 */
public class SignUpTest {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        
        System.out.println("========================================");
        System.out.println("     감정일기 회원가입 시스템");
        System.out.println("========================================\n");
        
        // 사용자 입력 받기
        System.out.print("사용자 이름을 입력하세요: ");
        String username = scanner.nextLine().trim();
        
        // 사용자 이름 유효성 검사
        if (username.isEmpty() || username.length() < 3) {
            System.out.println("❌ 사용자 이름은 3글자 이상이어야 합니다!");
            scanner.close();
            return;
        }
        
        // 중복 확인
        if (userDAO.isUsernameTaken(username)) {
            System.out.println("❌ 이미 사용 중인 사용자 이름입니다!");
            scanner.close();
            return;
        }
        
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
        
        // 비밀번호 유효성 검사
        if (password.length() < 4) {
            System.out.println("❌ 비밀번호는 4글자 이상이어야 합니다!");
            scanner.close();
            return;
        }
        
        System.out.print("이메일을 입력하세요 (선택사항, 엔터로 건너뛰기): ");
        String email = scanner.nextLine().trim();
        
        // 이메일이 비어있으면 null로 설정
        if (email.isEmpty()) {
            email = null;
        }
        
        // 회원가입 시도
        System.out.println("\n회원가입을 진행합니다...");
        boolean success = userDAO.registerUser(username, password, email);
        
        if (success) {
            System.out.println("\n✅ 회원가입이 완료되었습니다!");
            System.out.println("환영합니다, " + username + "님! 😊");
        } else {
            System.out.println("\n❌ 회원가입에 실패했습니다.");
        }
        
        scanner.close();
    }
}