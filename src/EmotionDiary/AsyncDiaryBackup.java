package EmotionDiary;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AsyncDiaryBackup extends Thread {
    private User user;
    private DiaryDAO diaryDAO;
    private String backupDirectory;
    
    public AsyncDiaryBackup(User user, String backupDirectory) {
        this.user = user;
        this.diaryDAO = new DiaryDAO();
        this.backupDirectory = backupDirectory;
        this.setName("DiaryBackupThread-" + user.getId());
    }
    
    @Override
    public void run() {
        System.out.println("[" + getName() + "] 백업 시작...");
        
        try {
            // 사용자의 모든 일기 조회
            List<Diary> diaries = diaryDAO.getDiariesByUserId(user.getId());
            
            if (diaries.isEmpty()) {
                System.out.println("백업할 일기가 없습니다.");
                return;
            }
            
            // 백업 디렉토리 생성
            File backupDir = new File(backupDirectory);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            // 파일명 생성 (예: backup_sangyeon_20250119_143022.txt)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("backup_%s_%s.txt", user.getUsername(), timestamp);
            File backupFile = new File(backupDir, filename);
            
            // 파일 쓰기
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
                writer.write("=" .repeat(60));
                writer.newLine();
                writer.write("감정일기 백업 파일");
                writer.newLine();
                writer.write("사용자: " + user.getUsername());
                writer.newLine();
                writer.write("백업 일시: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.newLine();
                writer.write("총 일기 개수: " + diaries.size() + "개");
                writer.newLine();
                writer.write("=".repeat(60));
                writer.newLine();
                writer.newLine();
                
                for (Diary diary : diaries) {
                    writer.write("━".repeat(60));
                    writer.newLine();
                    writer.write("제목: " + diary.getTitle());
                    writer.newLine();
                    writer.write("작성일: " + diary.getCreatedAt());
                    writer.newLine();
                    writer.write("━".repeat(60));
                    writer.newLine();
                    writer.write(diary.getContent());
                    writer.newLine();
                    writer.newLine();
                }
                
                writer.write("=".repeat(60));
                writer.newLine();
                writer.write("백업 완료");
                writer.newLine();
            }
            
            System.out.println("[" + getName() + "] 백업 완료: " + backupFile.getAbsolutePath());
            System.out.println("총 " + diaries.size() + "개의 일기를 백업했습니다.");
            
        } catch (DatabaseException e) {
            System.err.println("[" + getName() + "] 데이터베이스 오류: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[" + getName() + "] 파일 쓰기 오류: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[" + getName() + "] 알 수 없는 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/* 일기 통계 계산 쓰레드 */

class DiaryStatsCalculator implements Runnable {
    private User user;
    private DiaryDAO diaryDAO;
    
    public DiaryStatsCalculator(User user) {
        this.user = user;
        this.diaryDAO = new DiaryDAO();
    }
    
    @Override
    public void run() {
        System.out.println("[통계 쓰레드] 일기 통계 계산 중...");
        
        try {
            List<Diary> diaries = diaryDAO.getDiariesByUserId(user.getId());
            
            if (diaries.isEmpty()) {
                System.out.println("통계를 계산할 일기가 없습니다.");
                return;
            }
            
            // 통계 계산
            int totalDiaries = diaries.size();
            int totalWords = 0;
            int longestDiary = 0;
            String longestTitle = "";
            
            for (Diary diary : diaries) {
                int wordCount = diary.getContent().length();
                totalWords += wordCount;
                
                if (wordCount > longestDiary) {
                    longestDiary = wordCount;
                    longestTitle = diary.getTitle();
                }
            }
            
            double avgWords = totalWords / (double) totalDiaries;
            
            // 결과 출력
            System.out.println("\n" + "=".repeat(50));
            System.out.println(user.getUsername() + "님의 일기 통계");
            System.out.println("=".repeat(50));
            System.out.println("총 일기 개수: " + totalDiaries + "개");
            System.out.println("총 글자 수: " + totalWords + "자");
            System.out.println("평균 글자 수: " + String.format("%.1f", avgWords) + "자");
            System.out.println("가장 긴 일기: " + longestTitle + " (" + longestDiary + "자)");
            System.out.println("=".repeat(50) + "\n");
            
        } catch (DatabaseException e) {
            System.err.println("통계 계산 중 오류: " + e.getMessage());
        }
    }
}

/* 주기적으로 연결 상태 확인*/

class DatabaseHealthChecker extends Thread {
    private int intervalSeconds;
    private volatile boolean running = true;
    
    public DatabaseHealthChecker(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        this.setDaemon(true); // 데몬 쓰레드로 설정
        this.setName("DBHealthChecker");
    }
    
    @Override
    public void run() {
        System.out.println("[DB 헬스체커] 시작 (주기: " + intervalSeconds + "초)");
        
        while (running) {
            try {
                // DB 연결 테스트
                java.sql.Connection conn = DBConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    System.out.println("[DB 헬스체커] 데이터베이스 연결 정상");
                    conn.close();
                } else {
                    System.err.println("[DB 헬스체커] 데이터베이스 연결 이상");
                }
                
                // 대기
                Thread.sleep(intervalSeconds * 1000);
                
            } catch (Exception e) {
                System.err.println("[DB 헬스체커] 오류: " + e.getMessage());
            }
        }
    }
    
    public void stopChecker() {
        running = false;
        this.interrupt();
    }
}