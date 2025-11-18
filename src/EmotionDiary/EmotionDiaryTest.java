package EmotionDiary;

import java.util.List;
import java.util.Scanner;

public class EmotionDiaryTest {
    
    private static Scanner scanner = new Scanner(System.in);
    private static UserDAO userDAO = new UserDAO();
    private static DiaryDAO diaryDAO = new DiaryDAO();
    private static User currentUser = null;
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("     ✨ 감정일기 시스템 ✨");
        System.out.println("========================================\n");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== 메뉴 ===");
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("3. 종료");
        System.out.print("선택: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("프로그램을 종료합니다.");
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 선택입니다.");
        }
    }
    
    private static void login() {
        System.out.print("\n사용자 이름: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();
        
        User user = userDAO.loginUser(username, password);
        
        if (user != null) {
            currentUser = user;
            System.out.println("\n환영합니다, " + username + "님!");
        } else {
            System.out.println("\n로그인 실패! 사용자 이름 또는 비밀번호를 확인하세요.");
        }
    }
    
    private static void register() {
        System.out.print("\n사용자 이름 (3자 이상): ");
        String username = scanner.nextLine().trim();
        
        if (username.length() < 3) {
            System.out.println("사용자 이름은 3글자 이상이어야 합니다!");
            return;
        }
        
        if (userDAO.isUsernameTaken(username)) {
            System.out.println("이미 사용 중인 사용자 이름입니다!");
            return;
        }
        
        System.out.print("비밀번호 (4자 이상): ");
        String password = scanner.nextLine();
        
        if (password.length() < 4) {
            System.out.println("비밀번호는 4글자 이상이어야 합니다!");
            return;
        }
        
        System.out.print("이메일 (선택사항, 엔터로 건너뛰기): ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            email = null;
        }
        
        boolean success = userDAO.registerUser(username, password, email);
        
        if (success) {
            System.out.println("\n회원가입이 완료되었습니다!");
        } else {
            System.out.println("\n회원가입에 실패했습니다.");
        }
    }
    
    /**
     * 메인 메뉴 (로그인 후)
     */
    private static void showMainMenu() {
        System.out.println("\n=== 메인 메뉴 ===");
        System.out.println("1. 일기 쓰기");
        System.out.println("2. 내 일기 보기");
        System.out.println("3. 로그아웃");
        System.out.print("선택: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                writeDiary();
                break;
            case "2":
                viewDiaries();
                break;
            case "3":
                logout();
                break;
            default:
                System.out.println("잘못된 선택입니다.");
        }
    }
    
    /**
     * 일기 쓰기
     */
    private static void writeDiary() {
        System.out.println("\n=== 일기 쓰기 ===");
        
        System.out.print("제목 (감정 이모지 포함 가능 예: 😊 행복한 하루): ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("제목을 입력해주세요!");
            return;
        }
        
        System.out.println("내용 (여러 줄 입력 가능, 빈 줄 입력 시 종료):");
        StringBuilder content = new StringBuilder();
        
        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;
            content.append(line).append("\n");
        }
        
        if (content.length() == 0) {
            System.out.println("내용을 입력해주세요!");
            return;
        }
        
        boolean success = diaryDAO.createDiary(
            currentUser.getId(), 
            title, 
            content.toString().trim()
        );
        
        if (success) {
            System.out.println("\n일기가 저장되었습니다! 📝");
        } else {
            System.out.println("\n일기 저장에 실패했습니다.");
        }
    }
    
    /**
     * 일기 목록 보기
     */
    private static void viewDiaries() {
        System.out.println("\n=== 내 일기 목록 ===");
        
        List<Diary> diaries = diaryDAO.getDiariesByUserId(currentUser.getId());
        
        if (diaries.isEmpty()) {
            System.out.println("작성된 일기가 없습니다.");
            return;
        }
        
        for (int i = 0; i < diaries.size(); i++) {
            Diary diary = diaries.get(i);
            System.out.println("\n[" + (i + 1) + "] " + diary.getTitle());
            System.out.println("    날짜: " + diary.getCreatedAt());
            System.out.println("    내용: " + 
                (diary.getContent().length() > 50 ? 
                 diary.getContent().substring(0, 50) + "..." : 
                 diary.getContent()));
            System.out.println("    ----------------------------------------");
        }
        
        System.out.print("\n자세히 볼 번호 (0: 돌아가기): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= diaries.size()) {
                viewDiaryDetail(diaries.get(choice - 1));
            }
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력입니다.");
        }
    }
    
    /**
     * 일기 상세 보기
     */
    private static void viewDiaryDetail(Diary diary) {
        System.out.println("\n========================================");
        System.out.println("제목: " + diary.getTitle());
        System.out.println("날짜: " + diary.getCreatedAt());
        System.out.println("========================================");
        System.out.println(diary.getContent());
        System.out.println("========================================");
    }
    
    /**
     * 로그아웃
     */
    private static void logout() {
        System.out.println("\n로그아웃 되었습니다. 안녕히 가세요!");
        currentUser = null;
    }
}