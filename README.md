# CheckMate

### AI 기반 요구사항 자동 검증 웹 서비스
URL: https://checkmate-front.onrender.com

---
## 1. 소개

CheckMate는 요구사항 문서와 제출물을 자동으로 비교하여 
각 요구사항이 충족 / 부분 충족 / 미충족인지 즉시 판단하는 AI 기반 검증 서비스입니다.

사용자는 두 개의 파일을 업로드하면 요구사항별 분석 결과와 근거 문장을 받을 수 있습니다.

---
## 2. 주요 기능

- 요구사항 문서 자동 분석
- LLM 기반 요구사항/제출물 비교
- 요구사항별 충족 / 부분충족 / 미충족 판정
- 근거 문장 및 평가 이유 제공
- TXT 리포트 다운로드 지원
- PDF/DOCX/TXT 다중 파일 형식 지원

---
## 3. 서비스 구조
### 1) 파일 업로드
   - 요구사항 PDF/DOCX/TXT
   - 제출물 PDF/DOCX/TXT

   →  MIME + Binary Header 검증 적용


### 2) 문서 파싱
   - PDF: PDFBox
   - DOCX: Apache POI
   - TXT: Raw Read

   → 모든 문서를 텍스트로 정규화


### 3) 요구사항 분리
   - 번호·리스트 패턴 자동 감지
   - 항목별 개별 분석 구조 생성


### 4) GPT 기반 자동 분석
   - 요구사항과 제출물 의미 비교
   - 점수화 + 근거 문장 생성
   - OpenAI API 응답 JSON 스키마 강제


### 5) 결과 반환
   - 요구사항별 분석 결과 Summary Json 반환
   - 프론트엔드에서 표 형태로 시각화
   - .txt 파일로 다운로드 가능

---

## 4. 기술 스택
   
### Frontend

- React + Vite
- JavaScript / JSX
- Custom CSS

### Backend
- Spring Boot 3
- Java 17
- PDFBox / Apache POI
- OpenAI GPT API
- Gradle / Lombok

---
## 5. 실행 방법
### Frontend
```
   > cd CheckMate/frontend
   > npm install
   > npm run dev
```

### Backend
```
   > cd CheckMate/checkmate
   > ./gradlew bootRun
```

---
## TEAM GMBBD

- 권도훈: dhkwon0901@gamil.com
- 김건우: luckdaniel@naver.com
- 오경훈: ohkyounghun@naver.com
- 임동현: ehdgus4173@gmail.com

---

test commit by dohun

