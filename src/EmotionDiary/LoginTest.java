package EmotionDiary;

import java.util.Scanner;

public class LoginTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        System.out.println("========================================");
        System.out.println("         감정일기 로그인 시스템");
        System.out.println("========================================\n");

        // 사용자 입력 받기
        System.out.print("사용자 이름을 입력하세요: ");
        String username = scanner.nextLine().trim();

        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        // 로그인 시도
        System.out.println("\n로그인을 시도합니다...");
        User user = userDAO.loginUser(username, password);

        if (user != null) {
            System.out.println("\n로그인 성공!");
            System.out.println("환영합니다, " + user.getUsername() + "님!");
            System.out.println("사용자 정보: " + user);
        } else {
            System.out.println("\n로그인 실패.");
            System.out.println("사용자 이름 또는 비밀번호가 올바르지 않습니다.");
        }

        scanner.close();
    }
}
