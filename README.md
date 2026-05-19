# Emotion Diary (감정일기)

2025 2학기 자바 수행평가 프로젝트  
사용 언어: **Java** (60%), **HTML** (40%)

## 소개

Emotion Diary는 사용자별로 일기를 작성하고 감정 상태를 기록, 조회, 관리할 수 있는 데스크탑 기반 감정 일기장입니다.  
자바의 객체지향, 예외처리, 스레드 활용 등 고급 개념이 적용되어 있으며, 일부 기능은 아름다운 HTML 인터페이스와 연동도 가능합니다.

---

## 주요 기능

- **회원가입 및 로그인**  
  - 사용자별 계정 생성/로그인
  - 비밀번호 검증, 이메일(선택) 등록

- **일기 쓰기/조회/수정/삭제**  
  - 감정과 내용 입력
  - 내 일기 목록 조회, 상세 보기, 수정, 삭제

- **일기 검색 및 통계**  
  - 키워드 검색
  - 일기 통계(별도 스레드에서 처리)

- **백업/복원**  
  - 작성한 일기 파일로 백업 가능 (스레드 활용, 비동기)
- **에러 및 예외 처리**  
  - 계정, 일기, DB 관련 예외를 명확하게 안내

---

## 파일/폴더 구조

```
Emotion_Diary/
├── src/
│   └── EmotionDiary/
│       ├── AsyncDiaryBackup.java
│       ├── BaseDAO.java
│       ├── DBConnection.java
│       ├── DatabaseException.java
│       ├── Diary.java
│       ├── DiaryDAO.java
│       ├── EmotionDiaryApp.java      # 콘솔 기반 메인 실행 파일
│       ├── User.java
│       ├── UserDAO.java
│       └── index.html                # HTML 인터페이스 예시
├── bin/
├── .project
├── .classpath
└── README.md
```

---

## 실행 방법

### 1. Java 콘솔 프로그램

- 메인 진입점:  
  `src/EmotionDiary/EmotionDiaryApp.java`
- IDE(Eclipse/IntelliJ 등)에서 해당 파일을 실행  
- 콘솔에서 회원가입/로그인 후 일기를 작성/검색/수정/삭제 기능 사용 가능

### 2. 웹 인터페이스 (샘플)

- `src/EmotionDiary/index.html` 파일을 브라우저에서 열면,  
  감정선택/일기작성/목록확인 등 HTML 기반 UI 체험 가능  
- (실제 DB 연동은 콘솔 Java 측에 구현되어 있으며, 
  HTML은 프론트엔드 UI 참고용)

---

## 사용 기술

- **Java**:  
  - 객체지향 구조, DAO 패턴, 예외/유효성 검사, 쓰레드(통계, 백업)
  - 콘솔 기반 CRUD(생성, 조회, 수정, 삭제) UI
- **HTML/CSS/JS**:  
  - 감성적인 UI/UX  
  - 감정 아이콘, 일기 폼, 반응형 레이아웃

---

## 프로젝트 핵심 클래스

- `EmotionDiaryApp.java`  
  : 메인 프로그램 로직 (로그인, 메뉴, 일기 관리 등)
- `UserDAO.java`, `DiaryDAO.java`  
  : DB/파일 저장 로직
- `DiaryStatsCalculator`, `AsyncDiaryBackup`  
  : 쓰레드를 활용한 통계, 백업 기능
- `index.html`  
  : 직관적이고 아름다운 웹 UI 샘플

---

## 기여/문의

- 본 프로젝트는 자바 학습 및 실습을 위한 참고 예제입니다.
- 코드, UI 개선은 언제든 PR/이슈 환영합니다
- 문의: [프로젝트 깃허브 이슈](https://github.com/sangyeon08/Emotion_Diary/issues)

---
