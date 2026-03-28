# Chess.Com (for poor users)

터미널에서 동작하는 텍스트 기반 체스 게임입니다.  
표준 체스 규칙(캐슬링, 앙파상, 프로모션, 체크/체크메이트/스테일메이트, 50수 무승부, 3회 반복, 기물 부족 무승부)을 모두 지원합니다.

---

## 프로젝트 다운로드

### Git으로 클론

```powershell
git clone https://github.com/liebeym/chess_game.git
cd chess_game
```

### ZIP으로 다운로드

1. [https://github.com/liebeym/chess_game](https://github.com/liebeym/chess_game) 접속
2. 우측 상단 **Code** 버튼 클릭 → **Download ZIP** 선택
3. 압축 해제 후 `chess_game` 폴더로 이동

---

## 사전 요구 사항

### Chess_Com.java 실행 조건

JDK 11 이상이 설치되어 있어야 합니다. 아래 명령으로 버전을 확인하세요.

```powershell
PS> java -version
PS> javac -version
```

JDK가 없다면 아래 중 하나를 설치합니다:

- **Microsoft OpenJDK** (권장): [https://learn.microsoft.com/ko-kr/java/openjdk/download](https://learn.microsoft.com/ko-kr/java/openjdk/download)
- **Oracle JDK**: [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)
- **Adoptium Temurin**: [https://adoptium.net/](https://adoptium.net/)

설치 후 환경 변수 `JAVA_HOME` 및 `PATH`에 JDK 경로가 등록되어 있어야 합니다.

---

## 게임 실행 방법

```powershell
PS> javac Chess_Com.java
PS> java Chess_Com
```

---

## 테스트 실행 방법 (JUnit 5)

### 1. JUnit 라이브러리 다운로드

Maven Central에서 JUnit Platform Console Standalone JAR을 다운로드합니다.  
프로젝트 루트에 `lib` 폴더를 만들고 그 안에 저장합니다.

```powershell
PS> mkdir lib
PS> curl -o lib\junit-platform-console-standalone.jar "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar"
```

> ⚠️ `curl`은 **PowerShell 7.4 이상**에서만 정상 동작합니다.  
> 버전 확인: `curl --version`  
> PowerShell 버전 확인: `$PSVersionTable.PSVersion`  
> 구버전(5.x)이라면 브라우저에서 직접 다운로드하세요:  
> [junit-platform-console-standalone-1.11.4.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar)

다운로드 후 디렉토리 구조:

```text
chess_game/
├── Chess_Com.java
├── Chess_ComTest.java
├── lib/
│   └── junit-platform-console-standalone.jar
└── README.md
```

### 2. 컴파일

```powershell
PS> javac -cp lib\junit-platform-console-standalone.jar Chess_Com.java Chess_ComTest.java
```

### 3. 테스트 실행

```powershell
PS> java -jar lib\junit-platform-console-standalone.jar --class-path . --select-class Chess_ComTest
```

### 4. 정상 실행 결과 예시

```text
Test run finished after XXX ms
[  95 tests found     ]
[   0 tests skipped   ]
[  95 tests started   ]
[  95 tests successful]
[   0 tests failed    ]
```

---

## 입력 표기법 (Algebraic Notation)

| 입력 예시 | 설명 |
| --------- | ---- |
| `e4` | 폰을 e4로 전진 |
| `Nf3` | 나이트를 f3로 이동 |
| `Nxe5` 또는 `Ne5` | 나이트로 e5 기물 잡기 |
| `exd5` | e파일 폰으로 d5 잡기 |
| `0-0` 또는 `O-O` | 킹사이드 캐슬링 |
| `0-0-0` 또는 `O-O-O` | 퀸사이드 캐슬링 |
| `Rae1` | a파일 룩을 e1으로 (모호한 이동 지정) |
| `N3f4` | 3번째 행 나이트를 f4로 (모호한 이동 지정) |
| `resign` | 기권 |
| `draw` | 무승부 제안 |

---

## 기물 숫자 코드 (내부 표현)

| 숫자 | 기물 |
| ---- | ---- |
| 0 | 빈 칸 |
| 1 | 폰 (White) |
| 2 | 나이트 (White) |
| 3 | 비숍 (White) |
| 4 | 룩 (White) |
| 5 | 킹 (White) |
| 6 | 퀸 (White) |
| 11~16 | 흑 기물 (+10) |

---

## 클래스 및 메소드 설명

### `GameState` (내부 클래스)

게임의 모든 상태 변수를 하나의 객체로 묶어 관리합니다.

| 필드 | 설명 |
| ---- | ---- |
| `ChessBoard` | 8×8 체스판 배열. 각 칸의 숫자로 기물 종류와 색을 표현 |
| `whiteToMove` | 현재 백의 차례인지 여부 |
| `enPassantRow/Colunm` | 앙파상 가능한 목표 좌표 |
| `enPassantMoveCount` | 폰이 2칸 전진한 직후 카운터 (1이면 앙파상 가능) |
| `enPassantAble` | 앙파상 가능 여부 플래그 |
| `whiteShortCastle` / `whiteLongCastle` | 백의 킹사이드/퀸사이드 캐슬링 권리 |
| `blackShortCastle` / `blackLongCastle` | 흑의 킹사이드/퀸사이드 캐슬링 권리 |
| `fiftyMoveDrawCount` | 50수 무승부 카운터 (폰 이동·기물 잡기 시 0으로 초기화) |
| `isWhiteResigned` / `isBlackResigned` | 기권 여부 |
| `drawOffer` / `drawAccepted` | 무승부 제안 및 수락 여부 |
| `positionHistory` | 반복 판정을 위한 보드 상태 문자열 이력 |
| `moveHistory` | 화면에 표시할 이동 기록 리스트 |

---

### 메소드 동작 방식

---

#### `main(String[] args)`

- 프로그램 진입점. 로비 화면을 반복 출력하며 `1`(플레이) 또는 `2`(도움말)을 입력받습니다.
- `1` 선택 시: `initGame()` → `runGame()` → `displayGameResult()` 순서로 호출합니다.
- `2` 선택 시: `showHelp()`를 호출합니다.

---

#### `initGame()`

- 새로운 `GameState` 객체를 생성하여 반환합니다.
- 체스판을 초기 배치로 세팅하고 모든 게임 변수를 기본값으로 초기화합니다.

---

#### `runGame(Scanner, GameState)`

- **게임 전체 루프**를 담당합니다.
- 아래 조건 중 하나라도 만족하면 루프를 종료합니다:
  - 체크메이트 또는 스테일메이트 (`whatKindOfMate` 반환값이 0이 아님)
  - 50수 무승부 (`fiftyMoveDrawCount >= 100`)
  - 3회 반복 (`isThreefoldRepetition`)
  - 기물 부족 무승부 (`isInsufficientMaterial`)
  - 기권 또는 합의 무승부
- 루프 내에서 매 턴 `handleTurn()`을 호출합니다.

---

#### `handleTurn(Scanner, GameState)`

- **한 턴 전체**를 처리합니다. 유효한 이동이 입력될 때까지 반복합니다.
- 동작 순서:
  1. 보드 스냅샷(`backupBoard`) 저장
  2. 보드와 에러 메시지 출력
  3. 무승부 제안 처리 또는 이동 입력 받기
  4. 앙파상 카운터 업데이트
  5. 입력에 따라 알맞은 핸들러 메소드 호출:
     - 2글자 → `handlePawnMove()`
     - `0-0` → `handleShortCastle()`
     - `0-0-0` → `handleLongCastle()`
     - 대문자 시작 3~5글자 → `handlePieceMove()`
     - 소문자+`x` 4글자 → `handlePawnCapture()`
  6. `handleCheckValidation()`으로 이동 후 체크 여부 검증 (체크 상태면 이동 되돌리기)
  7. `handlePromotion()`으로 폰 프로모션 처리
  8. 50수 카운터 및 위치 이력 업데이트
  9. `updateMoveHistory()`로 이동 기록 저장
  10. 플레이어 교체

---

#### `handlePawnMove(String move, GameState gs)` → `int[]{validMove, alreadyWrong}`

- 폰의 일반 전진 이동(1칸 또는 2칸)을 처리합니다.
- 1칸 전진: 바로 앞이 비어 있고 자신의 폰이 그 뒤에 있을 때 이동
- 2칸 전진: 초기 위치(백 6행, 흑 1행)에서 경로가 비어 있을 때 이동. 이때 `enPassantMoveCount`를 증가시켜 다음 턴 앙파상을 허용

---

#### `handleShortCastle(GameState gs)` → `int[]{validMove, alreadyWrong}`

- 킹사이드 캐슬링(`O-O`)을 처리합니다.
- 조건: 킹과 룩 사이가 비어 있고, 킹이 지나가는 경로가 공격받지 않으며, 캐슬링 권리가 남아있을 때
- 성공 시 킹과 룩을 이동하고 양쪽 캐슬링 권리를 제거합니다.

---

#### `handleLongCastle(GameState gs)` → `int[]{validMove, alreadyWrong}`

- 퀸사이드 캐슬링(`O-O-O`)을 처리합니다.
- `handleShortCastle`과 동일한 방식이며 좌측 3칸 경로를 확인합니다.

---

#### `handlePieceMove(String move, GameState gs)` → `int[]{validMove, alreadyWrong, ambiguousMove}`

- 나이트(N), 비숍(B), 룩(R), 킹(K), 퀸(Q)의 이동을 처리합니다.
- 입력 형식 분류:
  - `Nf3` (3글자): 기물 + 목적지
  - `Nxe5` (4글자, x 포함): 기물 잡기
  - `Rad1` / `N3f4` (4글자, 좌표 지정): 모호한 이동
  - `Raxd1` / `N5xf7` (5글자): 모호한 잡기
- 각 기물별로 해당 위치에서 이동 가능한 같은 기물을 탐색 후 이동시킵니다.
- 2개 이상 발견 시 `resolveAmbiguous()`를 호출합니다.

---

#### `resolveAmbiguous(char specify, ...)` → `int[]{validMove, alreadyWrong, ambiguousMove}`

- 같은 종류의 기물이 2개 이상 같은 칸으로 이동 가능할 때 어느 기물을 움직일지 결정합니다.
- `specify`가 `a`~`h`이면 열(column)로, `1`~`8`이면 행(row)으로 구분합니다.
- 해당 조건에 맞는 기물이 정확히 1개일 때만 이동을 허용합니다.

---

#### `updateRookCastleFlags(GameState gs, int rookCol)`

- 룩이 이동한 후 해당 방향의 캐슬링 권리를 제거합니다.
- `rookCol == 7`이면 킹사이드, `rookCol == 0`이면 퀸사이드 캐슬링 권리를 해제합니다.

---

#### `handlePawnCapture(String move, GameState gs)` → `int[]{validMove, alreadyWrong}`

- 폰 잡기(`exd5` 형식)와 앙파상을 처리합니다.
- 일반 잡기: 대각선 앞에 적 기물이 있을 때
- 앙파상: `enPassantAble`이 true이고 목표 좌표가 앙파상 좌표와 일치할 때. 목적지는 비어있고 지나친 폰을 별도로 제거합니다.

---

#### `handleCheckValidation(GameState gs, int[][] backupBoard)` → `int[]{0 또는 1}`

- 이동 후 자신의 킹이 여전히 체크 상태인지 검사합니다.
- 체크 상태라면 `backupBoard`로 보드를 되돌리고 `0`을 반환합니다.
- 안전하다면 `1`을 반환합니다.

---

#### `handlePromotion(Scanner, GameState)`

- 폰이 끝줄에 도달했을 때 호환을 선택받습니다.
- Q(퀸), R(룩), B(비숍), N(나이트) 중 하나를 입력받아 해당 기물로 교체합니다.

---

#### `getMoveDestRow(String move, int[][] board)` / `getMoveDestCol(String move, int[][] board)`

- 이동 문자열에서 목적지 행/열 좌표를 파싱하여 배열 인덱스로 반환합니다.
- 입력 길이(2~5글자)에 따라 파싱 위치를 다르게 처리합니다.

---

#### `updateMoveHistory(String move, GameState gs, int[][] backupBoard)`

- 이동 기록을 게임 로그에 추가합니다.
- 잡기 이동인데 `x`가 없으면 자동으로 `x`를 삽입합니다 (예: `Ne5` → `Nxe5`)
- 체크 시 `+`, 체크메이트 시 `#`을 이동 표기 끝에 자동으로 붙입니다.

---

#### `displayGameResult(GameState gs)`

- 게임 종료 후 최종 보드 상태와 결과를 출력합니다.
- 종료 유형별 메시지:
  - 기권 → "Resigned!"
  - 합의 무승부 → "Draw by agreement!"
  - 50수 무승부 → "Draw by 50-move Rule!"
  - 기물 부족 → "Draw by Insuficient Material!"
  - 3회 반복 → "Draw by Threefold Repetition!"
  - 체크메이트 → "Checkmate!" + 승자 표시
  - 스테일메이트 → "Stalemate!"

---

#### `showHelp(Scanner)`

- 도움말 화면을 출력합니다.
- 기물 이름, 이동 입력 방법, 캐슬링, 앙파상, 프로모션, 모호한 이동 처리, 특수 커맨드를 안내합니다.
- `x` 입력 시 로비로 돌아갑니다.

---

## 기존 유틸리티 메소드

| 메소드 | 설명 |
| ------ | ---- |
| `display(board, moveHistory)` | 체스판과 게임 로그를 터미널에 출력 |
| `numToPiece(num)` | 숫자를 기물 문자(`K`, `Q`, `R` 등)로 변환 |
| `alphaToNum(alpha)` | 열 문자(`a`~`h`)를 숫자(1~8)로 변환 |
| `PieceToNum(alpha)` | 기물 문자(`N`, `B`, `R` 등)를 내부 숫자로 변환 |
| `clearTerminal()` | 터미널 화면 초기화 |
| `isThisAndThatEnemyPiece(target, your)` | 두 기물이 서로 적인지 판단 |
| `isThisSquareUnderAttack(row, col, attackByWhite, board)` | 특정 칸이 공격받고 있는지 검사 (체크/캐슬링 안전 경로 검증에 사용) |
| `whatKindOfMate(whiteToMove, board)` | 0=진행중, 1=체크메이트, 2=스테일메이트 반환 |
| `isLegalMove(...)` | 특정 기물이 해당 칸으로 합법적으로 이동 가능한지 검사 |
| `isPathClear(board, ...)` | 두 좌표 사이 경로가 비어있는지 검사 |
| `boardToString(board)` | 보드 상태를 문자열로 직렬화 (3회 반복 판정용) |
| `isThreefoldRepetition(positionHistory, board)` | 동일 포지션이 3회 이상 반복됐는지 검사 |
| `isInsufficientMaterial(board)` | 기물 부족 무승부 조건 판단 (KK, KNK, KBK, 동색 비숍 KBKB) |
| `getSquareColor(row, col)` | 해당 칸의 색(0=백, 1=흑)을 반환 (비숍 색 판별용) |
