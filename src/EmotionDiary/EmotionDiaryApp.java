package EmotionDiary;

import java.util.List;
import java.util.Scanner;

public class EmotionDiaryApp {
    
    private static Scanner scanner = new Scanner(System.in);
    private static UserDAO userDAO = new UserDAO();
    private static DiaryDAO diaryDAO = new DiaryDAO();
    private static User currentUser = null;
    private static DatabaseHealthChecker healthChecker;
    
    public static void main(String[] args) {
        displayWelcome();
        
        healthChecker = new DatabaseHealthChecker(30); // 30초마다 체크
        healthChecker.start();
        
        try {
            while (true) {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            }
        } finally {
            if (healthChecker != null) {
                healthChecker.stopChecker();
            }
            scanner.close();
        }
    }
    
    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     ✨ 감정일기 시스템 ✨");
        System.out.println("     상속, 예외처리, 쓰레드 적용 버전");
        System.out.println("=".repeat(60) + "\n");
    }
    
    // ==================== 로그인/회원가입 메뉴 ====================
    
    private static void showLoginMenu() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("  로그인/회원가입");
        System.out.println("─".repeat(40));
        System.out.println("  1. 로그인");
        System.out.println("  2. 회원가입");
        System.out.println("  3. 종료");
        System.out.println("─".repeat(40));
        System.out.print("  선택: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("\n 프로그램을 종료합니다. 안녕히 가세요!");
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 선택입니다.");
        }
    }
    
    private static void login() {
        System.out.println("\n" + "━".repeat(40));
        System.out.println("  로그인");
        System.out.println("━".repeat(40));
        
        try {
            System.out.print("  사용자 이름: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("  비밀번호: ");
            String password = scanner.nextLine();
            
            // 로그인 시도 (예외처리 포함)
            User user = userDAO.loginUser(username, password);
            
            currentUser = user;
            System.out.println("\n로그인 성공!");
            System.out.println("  환영합니다, " + username + "님!\n");
            
        } catch (AuthenticationException e) {
            System.out.println("\n " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n데이터베이스 오류: " + e.getMessage());
            System.out.println("  오류 코드: " + e.getErrorCode());
        }
    }
    
    private static void register() {
        System.out.println("\n" + "━".repeat(40));
        System.out.println("  회원가입");
        System.out.println("━".repeat(40));
        
        try {
            System.out.print("  사용자 이름 (3자 이상): ");
            String username = scanner.nextLine().trim();
            
            System.out.print("  비밀번호 (4자 이상): ");
            String password = scanner.nextLine();
            
            System.out.print("  이메일 (선택, Enter로 건너뛰기): ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                email = null;
            }
            
            // 회원가입 시도 (예외처리 포함)
            User user = userDAO.registerUser(username, password, email);
            
            System.out.println("\n회원가입 완료!");
            System.out.println("  " + username + "님, 환영합니다!");
            System.out.println("  이제 로그인해주세요.\n");
            
        } catch (ValidationException e) {
            System.out.println("\n유효성 검증 실패: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n데이터베이스 오류: " + e.getMessage());
            System.out.println("  오류 코드: " + e.getErrorCode());
        }
    }
    
    // ==================== 메인 메뉴 ====================
    
    private static void showMainMenu() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("  메인 메뉴 (" + currentUser.getUsername() + "님)");
        System.out.println("─".repeat(40));
        System.out.println("  1. 일기 쓰기");
        System.out.println("  2. 내 일기 보기");
        System.out.println("  3. 일기 검색");
        System.out.println("  4. 일기 통계 (쓰레드)");
        System.out.println("  5. 일기 백업 (쓰레드)");
        System.out.println("  6. 로그아웃");
        System.out.println("─".repeat(40));
        System.out.print("  선택: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                writeDiary();
                break;
            case "2":
                viewDiaries();
                break;
            case "3":
                searchDiaries();
                break;
            case "4":
                calculateStats();
                break;
            case "5":
                backupDiaries();
                break;
            case "6":
                logout();
                break;
            default:
                System.out.println("잘못된 선택입니다.");
        }
    }
    
    // ==================== 일기 작성 ====================
    
    private static void writeDiary() {
        System.out.println("\n" + "━".repeat(40));
        System.out.println("  일기 쓰기");
        System.out.println("━".repeat(40));
        
        try {
            System.out.print("  제목 (예: 행복한 하루): ");
            String title = scanner.nextLine().trim();
            
            System.out.println("  내용 (여러 줄 가능, 빈 줄 입력 시 종료):");
            StringBuilder content = new StringBuilder();
            
            while (true) {
                String line = scanner.nextLine();
                if (line.isEmpty()) break;
                content.append(line).append("\n");
            }
            
            // 일기 저장 (예외처리 포함)
            Diary diary = diaryDAO.createDiary(
                currentUser.getId(),
                title,
                content.toString().trim()
            );
            
            System.out.println("\n일기가 저장되었습니다!");
            System.out.println("  일기 ID: " + diary.getDiaryId());
            
        } catch (ValidationException e) {
            System.out.println("\n유효성 검증 실패: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n데이터베이스 오류: " + e.getMessage());
        }
    }
    
    // ==================== 일기 목록 보기 ====================
    
    private static void viewDiaries() {
        System.out.println("\n" + "━".repeat(40));
        System.out.println("  내 일기 목록");
        System.out.println("━".repeat(40));
        
        try {
            List<Diary> diaries = diaryDAO.getDiariesByUserId(currentUser.getId());
            
            if (diaries.isEmpty()) {
                System.out.println("  작성된 일기가 없습니다.");
                return;
            }
            
            for (int i = 0; i < diaries.size(); i++) {
                Diary diary = diaries.get(i);
                System.out.println("\n  [" + (i + 1) + "] " + diary.getTitle());
                System.out.println("      " + diary.getCreatedAt());
                System.out.println("      " + 
                    (diary.getContent().length() > 50 ? 
                     diary.getContent().substring(0, 50) + "..." : 
                     diary.getContent()));
            }
            
            System.out.print("\n  자세히 볼 번호 (0: 돌아가기): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice > 0 && choice <= diaries.size()) {
                    viewDiaryDetail(diaries.get(choice - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다.");
            }
            
        } catch (DatabaseException e) {
            System.out.println("\n데이터베이스 오류: " + e.getMessage());
        }
    }
    
    private static void viewDiaryDetail(Diary diary) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + diary.getTitle());
        System.out.println(" " + diary.getCreatedAt());
        System.out.println("═".repeat(60));
        System.out.println(diary.getContent());
        System.out.println("═".repeat(60));
        
        System.out.println("\n  1. 수정");
        System.out.println("  2. 삭제");
        System.out.println("  0. 돌아가기");
        System.out.print("  선택: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                editDiary(diary);
                break;
            case "2":
                deleteDiary(diary);
                break;
        }
    }
    
    private static void editDiary(Diary diary) {
        System.out.println("\n━━━ 일기 수정 ━━━");
        
        try {
            System.out.print("새 제목 (현재: " + diary.getTitle() + "): ");
            String newTitle = scanner.nextLine().trim();
            if (newTitle.isEmpty()) newTitle = diary.getTitle();
            
            System.out.println("새 내용 (여러 줄 가능, 빈 줄 입력 시 종료):");
            StringBuilder newContent = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if (line.isEmpty()) break;
                newContent.append(line).append("\n");
            }
            
            String contentStr = newContent.toString().trim();
            if (contentStr.isEmpty()) contentStr = diary.getContent();
            
            diaryDAO.updateDiary(diary.getDiaryId(), newTitle, contentStr);
            System.out.println("\n일기가 수정되었습니다!");
            
        } catch (ValidationException | DatabaseException e) {
            System.out.println("\n오류: " + e.getMessage());
        }
    }
    
    private static void deleteDiary(Diary diary) {
        System.out.print("\n정말 삭제하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            try {
                diaryDAO.deleteDiary(diary.getDiaryId());
                System.out.println("\n일기가 삭제되었습니다.");
            } catch (DatabaseException e) {
                System.out.println("\n삭제 실패: " + e.getMessage());
            }
        }
    }
    
    // ==================== 일기 검색 ====================
    
    private static void searchDiaries() {
        System.out.println("\n━━━ 일기 검색 ━━━");
        System.out.print("검색어를 입력하세요: ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("검색어를 입력해주세요.");
            return;
        }
        
        try {
            List<Diary> results = diaryDAO.searchDiaries(currentUser.getId(), keyword);
            
            if (results.isEmpty()) {
                System.out.println("검색 결과가 없습니다.");
                return;
            }
            
            System.out.println("\n검색 결과: " + results.size() + "개");
            for (int i = 0; i < results.size(); i++) {
                Diary diary = results.get(i);
                System.out.println("\n  [" + (i + 1) + "] " + diary.getTitle());
                System.out.println("      " + diary.getCreatedAt());
            }
            
        } catch (DatabaseException e) {
            System.out.println("\n검색 중 오류: " + e.getMessage());
        }
    }
    
    // ==================== 쓰레드 기능 ====================
    
    private static void calculateStats() {
        System.out.println("\n일기 통계를 계산하는 중...");
        
        // Runnable 쓰레드 실행
        DiaryStatsCalculator calculator = new DiaryStatsCalculator(currentUser);
        Thread statsThread = new Thread(calculator, "StatsThread");
        statsThread.start();
        
        try {
            statsThread.join(); // 계산 완료 대기
        } catch (InterruptedException e) {
            System.err.println("통계 계산이 중단되었습니다.");
        }
    }
    
    private static void backupDiaries() {
        System.out.println("\n일기 백업을 시작합니다...");
        System.out.print("백업 폴더 경로 (Enter: 현재 폴더의 backup/): ");
        String backupPath = scanner.nextLine().trim();
        
        if (backupPath.isEmpty()) {
            backupPath = "./backup";
        }
        
        // Thread 쓰레드 실행 (백그라운드)
        AsyncDiaryBackup backupThread = new AsyncDiaryBackup(currentUser, backupPath);
        backupThread.start();
        
        System.out.println("백업이 백그라운드에서 진행됩니다.");
        System.out.println("   (메뉴로 돌아가도 백업은 계속됩니다)");
    }
    
    // ==================== 로그아웃 ====================
    
    private static void logout() {
        System.out.println("\n로그아웃 되었습니다. 안녕히 가세요, " + currentUser.getUsername() + "님!");
        currentUser = null;
    }
}