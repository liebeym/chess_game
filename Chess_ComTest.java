import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for Chess_Com
 *
 * 컴파일:
 *   javac -cp lib/junit-platform-console-standalone.jar Chess_Com.java Chess_ComTest.java
 *
 * 실행:
 *   java -jar lib/junit-platform-console-standalone.jar --class-path . --select-class Chess_ComTest
 */
public class Chess_ComTest {

    // =====================================================================
    // Helper: create empty chess board
    // =====================================================================
    private static int[][] emptyBoard() {
        return new int[8][8];
    }

    // =====================================================================
    // 1. alphaToNum
    // =====================================================================
    // 1. alphaToNum
    // =====================================================================
    @Nested
    @DisplayName("alphaToNum: column char to int")
    class AlphaToNumTests {

        @ParameterizedTest(name = "''{0}'' -> {1}")
        @CsvSource({"a,1", "b,2", "c,3", "d,4", "e,5", "f,6", "g,7", "h,8"})
        void validColumns(char col, int expected) {
            assertEquals(expected, Chess_Com.alphaToNum(col));
        }

        @Test
        @DisplayName("out-of-range char returns 9")
        void outOfRange() {
            assertEquals(9, Chess_Com.alphaToNum('z'));
            assertEquals(9, Chess_Com.alphaToNum('A'));
        }
    }

    // =====================================================================
    // 2. PieceToNum
    // =====================================================================
    @Nested
    @DisplayName("PieceToNum: piece char to int")
    class PieceToNumTests {

        @ParameterizedTest(name = "''{0}'' -> {1}")
        @CsvSource({"N,2", "B,3", "R,4", "K,5", "Q,6"})
        void validPieces(char piece, int expected) {
            assertEquals(expected, Chess_Com.PieceToNum(piece));
        }

        @Test
        @DisplayName("unknown char returns 7")
        void unknownPiece() {
            assertEquals(7, Chess_Com.PieceToNum('X'));
            assertEquals(7, Chess_Com.PieceToNum('P'));
        }
    }

    // =====================================================================
    // 3. numToPiece
    // =====================================================================
    @Nested
    @DisplayName("numToPiece: int to piece char")
    class NumToPieceTests {

        @Test
        @DisplayName("empty square returns '    '")
        void emptySquare() {
            assertEquals("    ", Chess_Com.numToPiece(0));
        }

        @ParameterizedTest(name = "{0} -> 2nd char ''{1}''")
        @CsvSource({"1,o", "2,N", "3,B", "4,R", "5,K", "6,Q"})
        void whitePieces(int num, char expected) {
            String result = Chess_Com.numToPiece(num);
            assertEquals(expected, result.charAt(1));
            // 백 기물은 프레임이 '    ' 형태
            assertEquals(' ', result.charAt(0));
        }

        @ParameterizedTest(name = "{0} -> 2nd char ''{1}'' (black)")
        @CsvSource({"11,o", "12,N", "13,B", "14,R", "15,K", "16,Q"})
        void blackPieces(int num, char expected) {
            String result = Chess_Com.numToPiece(num);
            assertEquals(expected, result.charAt(1));
            // 흑 기물은 프레임이 '= ==' 형태
            assertEquals('=', result.charAt(0));
        }
    }

    // =====================================================================
    // 4. getSquareColor
    // =====================================================================
    @Nested
    @DisplayName("getSquareColor: square color detection")
    class GetSquareColorTests {

        @Test
        @DisplayName("(0,0) = white(0)")
        void topLeftIsWhite() {
            assertEquals(0, Chess_Com.getSquareColor(0, 0));
        }

        @Test
        @DisplayName("(0,1) = black(1)")
        void topSecondIsBlack() {
            assertEquals(1, Chess_Com.getSquareColor(0, 1));
        }

        @Test
        @DisplayName("(1,0) = black(1)")
        void secondRowFirstColIsBlack() {
            assertEquals(1, Chess_Com.getSquareColor(1, 0));
        }

        @Test
        @DisplayName("(7,7) = white(0)")
        void bottomRightIsWhite() {
            assertEquals(0, Chess_Com.getSquareColor(7, 7));
        }
    }

    // =====================================================================
    // 5. isThisAndThatEnemyPiece
    // =====================================================================
    @Nested
    @DisplayName("isThisAndThatEnemyPiece: enemy piece detection")
    class IsEnemyPieceTests {

        @Test
        @DisplayName("empty square(0) is not enemy")
        void emptySquareIsNotEnemy() {
            assertFalse(Chess_Com.isThisAndThatEnemyPiece(0, 1));
        }

        @Test
        @DisplayName("same-color piece is not enemy")
        void sameColorIsNotEnemy() {
            assertFalse(Chess_Com.isThisAndThatEnemyPiece(2, 1));   // 백 나이트 vs 백 폰
            assertFalse(Chess_Com.isThisAndThatEnemyPiece(12, 11)); // 흑 나이트 vs 흑 폰
        }

        @Test
        @DisplayName("different-color piece is enemy")
        void differentColorIsEnemy() {
            assertTrue(Chess_Com.isThisAndThatEnemyPiece(11, 1));  // 흑 폰 vs 백 폰
            assertTrue(Chess_Com.isThisAndThatEnemyPiece(1, 11));  // 백 폰 vs 흑 폰
        }
    }

    // =====================================================================
    // 6. boardToString / isThreefoldRepetition
    // =====================================================================
    @Nested
    @DisplayName("boardToString & isThreefoldRepetition")
    class BoardStateTests {

        @Test
        @DisplayName("boardToString: empty board -> 64 '0,' tokens")
        void emptyBoardString() {
            int[][] board = emptyBoard();
            String result = Chess_Com.boardToString(board);
            assertEquals("0,".repeat(64), result);
        }

        @Test
        @DisplayName("isThreefoldRepetition: fewer than 3 repetitions -> false")
        void notThreefold() {
            int[][] board = emptyBoard();
            board[7][4] = 5;  // 백 킹
            ArrayList<String> history = new ArrayList<>();
            history.add(Chess_Com.boardToString(board));
            history.add(Chess_Com.boardToString(board));
            assertFalse(Chess_Com.isThreefoldRepetition(history, board));
        }

        @Test
        @DisplayName("isThreefoldRepetition: 3 repetitions -> true")
        void threefoldRepetition() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            ArrayList<String> history = new ArrayList<>();
            String s = Chess_Com.boardToString(board);
            history.add(s);
            history.add(s);
            history.add(s);
            assertTrue(Chess_Com.isThreefoldRepetition(history, board));
        }
    }

    // =====================================================================
    // 7. isInsufficientMaterial
    // =====================================================================
    @Nested
    @DisplayName("isInsufficientMaterial: insufficient material draw detection")
    class InsufficientMaterialTests {

        @Test
        @DisplayName("KK -> true")
        void kingVsKing() {
            int[][] board = emptyBoard();
            board[7][4] = 5;   // 백 킹
            board[0][4] = 15;  // 흑 킹
            assertTrue(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KNK -> true (white knight)")
        void kingKnightVsKing() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[7][3] = 2;   // 백 나이트
            board[0][4] = 15;
            assertTrue(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KBK -> true (white bishop)")
        void kingBishopVsKing() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[7][3] = 3;   // 백 비숍
            board[0][4] = 15;
            assertTrue(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KKN -> true (black knight)")
        void kingVsKingKnight() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[0][4] = 15;
            board[0][3] = 12;  // 흑 나이트
            assertTrue(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KBKB same-color bishops -> true")
        void kingBishopVsKingBishopSameColor() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[7][2] = 3;   // 백 비숍 (row7, col2): getSquareColor(7,2)=1 → 검은칸
            board[0][4] = 15;
            board[0][1] = 13;  // 흑 비숍 (row0, col1): getSquareColor(0,1)=1 → 검은칸 (같은 색)
            assertTrue(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KBKB different-color bishops -> false")
        void kingBishopVsKingBishopDifferentColor() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[7][2] = 3;   // 백 비숍 (row7, col2): getSquareColor(7,2)=1 → 검은칸
            board[0][4] = 15;
            board[0][2] = 13;  // 흑 비숍 (row0, col2): getSquareColor(0,2)=0 → 흰칸 (다른 색)
            assertFalse(Chess_Com.isInsufficientMaterial(board));
        }

        @Test
        @DisplayName("KQK -> false (queen is sufficient material)")
        void queenIsSufficient() {
            int[][] board = emptyBoard();
            board[7][4] = 5;
            board[7][3] = 6;   // 백 퀸
            board[0][4] = 15;
            assertFalse(Chess_Com.isInsufficientMaterial(board));
        }
    }

    // =====================================================================
    // 8. isPathClear
    // =====================================================================
    @Nested
    @DisplayName("isPathClear: path-clear detection")
    class IsPathClearTests {

        @Test
        @DisplayName("clear straight path returns true")
        void clearStraightPath() {
            int[][] board = emptyBoard();
            // 룩이 (7,0)에서 (7,4)로 이동 - 경로 비어있음
            assertTrue(Chess_Com.isPathClear(board, 7, 0, 4, 7));
        }

        @Test
        @DisplayName("blocked straight path returns false")
        void blockedPath() {
            int[][] board = emptyBoard();
            board[7][2] = 3; // 비숍이 경로 막음
            assertFalse(Chess_Com.isPathClear(board, 7, 0, 4, 7));
        }

        @Test
        @DisplayName("clear diagonal path returns true")
        void clearDiagonalPath() {
            int[][] board = emptyBoard();
            // (7,0)에서 (4,3)으로 대각선
            assertTrue(Chess_Com.isPathClear(board, 7, 0, 3, 4));
        }

        @Test
        @DisplayName("blocked diagonal path returns false")
        void blockedDiagonalPath() {
            int[][] board = emptyBoard();
            board[6][1] = 1; // 경로 막음
            assertFalse(Chess_Com.isPathClear(board, 7, 0, 3, 4));
        }
    }

    // =====================================================================
    // 9. isThisSquareUnderAttack
    // =====================================================================
    @Nested
    @DisplayName("isThisSquareUnderAttack: square attack detection")
    class UnderAttackTests {

        @Test
        @DisplayName("black pawn diagonal attack - square is under attack")
        void pawnAttack() {
            int[][] board = emptyBoard();
            board[2][3] = 11; // 흑 폰 (row2, col3)
            // (3, 4)는 흑 폰의 대각선 공격 대상 (흑 폰은 아래로 이동, 즉 row 증가)
            // 흑 폰이 row2에 있으면 row3 col2, col4를 공격
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 4, false, board)); // 백이 공격하는게 아님, 흑이 공격
            // 올바른 파라미터: attackByWhite=false → 흑이 공격자
            // 흑 폰(11)은 pawnDirection=-1로 row-1을 공격 → 흑 폰 row2이면 row1 col2, col4 공격
            // 다시 정리: 흑 폰은 아래(row 증가 방향)로 움직이므로 row2 -> row3 방향
            // isThisSquareUnderAttack(row, col, attackByWhite, board)
            // 흑이 (1,2) 위치의 칸을 공격하는지: 흑 폰이 row2, col1 또는 col3에 있으면 row1 공격
        }

        @Test
        @DisplayName("black pawn at (row2,col3) attacks (row3,col2) and (row3,col4)")
        void blackPawnAttacksCorrectSquares() {
            int[][] board = emptyBoard();
            board[2][3] = 11; // 흑 폰 at row2, col3
            // isThisSquareUnderAttack(row, col, false, board)에서 pawnDirection=-1
            // board[row-1][col±1]에 흑 폰(11)이 있는지 확인
            // 즉 (row3, col2) → board[3-1][2-1]=board[2][2]=0, board[3-1][2+1]=board[2][3]=11 ✓
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 2, false, board));
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 4, false, board));
            assertFalse(Chess_Com.isThisSquareUnderAttack(3, 3, false, board)); // 직선은 공격 아님
        }

        @Test
        @DisplayName("white pawn at (row5,col3) attacks (row4,col2) and (row4,col4)")
        void whitePawnAttacksCorrectSquares() {
            int[][] board = emptyBoard();
            board[5][3] = 1; // 백 폰 at row5, col3
            // isThisSquareUnderAttack(row, col, true, board)에서 pawnDirection=+1
            // board[row+1][col±1]에 백 폰(1)이 있는지 확인
            // 즉 (row4, col2) → board[4+1][2+1]=board[5][3]=1 ✓
            assertTrue(Chess_Com.isThisSquareUnderAttack(4, 2, true, board));
            assertTrue(Chess_Com.isThisSquareUnderAttack(4, 4, true, board));
        }

        @Test
        @DisplayName("knight attacks target square")
        void knightAttack() {
            int[][] board = emptyBoard();
            board[5][4] = 2; // 백 나이트 at row5, col4
            // 백 나이트가 (3, 3)을 공격하는지
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 3, true, board));
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 5, true, board));
            assertTrue(Chess_Com.isThisSquareUnderAttack(4, 2, true, board));
        }

        @Test
        @DisplayName("rook attacks along rank and file")
        void rookAttack() {
            int[][] board = emptyBoard();
            board[7][0] = 4; // 백 룩 at row7, col0
            assertTrue(Chess_Com.isThisSquareUnderAttack(7, 5, true, board));  // 같은 행
            assertTrue(Chess_Com.isThisSquareUnderAttack(3, 0, true, board));  // 같은 열
            assertFalse(Chess_Com.isThisSquareUnderAttack(5, 5, true, board)); // 대각선 - 공격 못함
        }

        @Test
        @DisplayName("bishop attacks diagonally")
        void bishopAttack() {
            int[][] board = emptyBoard();
            board[4][4] = 3; // 백 비숍
            assertTrue(Chess_Com.isThisSquareUnderAttack(6, 6, true, board));
            assertTrue(Chess_Com.isThisSquareUnderAttack(2, 2, true, board));
            assertFalse(Chess_Com.isThisSquareUnderAttack(4, 6, true, board)); // 직선 - 공격 못함
        }

        @Test
        @DisplayName("piece blocking path prevents attack")
        void blockedAttack() {
            int[][] board = emptyBoard();
            board[7][0] = 4; // 백 룩
            board[7][3] = 1; // 백 폰이 경로 차단
            assertFalse(Chess_Com.isThisSquareUnderAttack(7, 5, true, board)); // 차단됨
        }
    }

    // =====================================================================
    // 10. whatKindOfMate
    // =====================================================================
    @Nested
    @DisplayName("whatKindOfMate: checkmate/stalemate detection")
    class WhatKindOfMateTests {

        @Test
        @DisplayName("initial board -> 0 (game continues)")
        void initialBoardIsNotMate() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertEquals(0, Chess_Com.whatKindOfMate(true, gs.ChessBoard));
        }

        @Test
        @DisplayName("king cornered checkmate -> 1 (checkmate)")
        void foolsMate() {
            int[][] board = emptyBoard();
            // 확실한 체크메이트 포지션:
            // 백 킹 a1(7,0), 흑 퀸 a3(5,0)이 수직 체크, 흑 룩 b8(0,1)이 b파일 봉쇄
            // 백 킹 이동: a2(6,0) → 흑 퀸 수직 공격, b1(7,1) → 흑 룩 수직 공격, b2(6,1) → 흑 퀸 대각선 공격
            board[7][0] = 5;  // 백 킹 a1
            board[5][0] = 16; // 흑 퀸 a3 (수직으로 a1 직접 체크)
            board[0][1] = 14; // 흑 룩 b8 (b파일 전체 공격)
            board[0][7] = 15; // 흑 킹 h8

            assertEquals(1, Chess_Com.whatKindOfMate(true, board));
        }

        @Test
        @DisplayName("stalemate position -> 2")
        void stalemate() {
            int[][] board = emptyBoard();
            // 흑 킹만 a8에, 백이 스테일메이트 상황 만듦
            board[0][0] = 15; // 흑 킹 a8
            board[2][1] = 6;  // 백 퀸 b6 (코드=6, 흑 퀸은 16)
            board[1][7] = 5;  // 백 킹 h7
            // 흑 킹 이동 가능 칸 (0,1),(1,0),(1,1) 모두 백 퀸 공격범위
            // 흑 킹 자신은 체크받지 않음 → 스테일메이트
            assertEquals(2, Chess_Com.whatKindOfMate(false, board));
        }
    }

    // =====================================================================
    // 11. initGame
    // =====================================================================
    @Nested
    @DisplayName("initGame: game initialization")
    class InitGameTests {

        @Test
        @DisplayName("initial board row 1: black pieces placement")
        void initialBlackPieces() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertArrayEquals(new int[]{14, 12, 13, 16, 15, 13, 12, 14}, gs.ChessBoard[0]);
        }

        @Test
        @DisplayName("initial board row 2: black pawns placement")
        void initialBlackPawns() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertArrayEquals(new int[]{11, 11, 11, 11, 11, 11, 11, 11}, gs.ChessBoard[1]);
        }

        @Test
        @DisplayName("initial board row 7: white pawns placement")
        void initialWhitePawns() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1, 1, 1}, gs.ChessBoard[6]);
        }

        @Test
        @DisplayName("initial board row 8: white pieces placement")
        void initialWhitePieces() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertArrayEquals(new int[]{4, 2, 3, 6, 5, 3, 2, 4}, gs.ChessBoard[7]);
        }

        @Test
        @DisplayName("initial state: white to move, all castling rights available")
        void initialFlags() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            assertTrue(gs.whiteToMove);
            assertTrue(gs.whiteShortCastle);
            assertTrue(gs.whiteLongCastle);
            assertTrue(gs.blackShortCastle);
            assertTrue(gs.blackLongCastle);
            assertFalse(gs.isWhiteResigned);
            assertFalse(gs.isBlackResigned);
            assertFalse(gs.drawAccepted);
            assertEquals(0, gs.fiftyMoveDrawCount);
        }
    }

    // =====================================================================
    // 12. handlePawnMove
    // =====================================================================
    @Nested
    @DisplayName("handlePawnMove: pawn advance")
    class HandlePawnMoveTests {

        @Test
        @DisplayName("white pawn one square advance (e2 -> e3)")
        void whitePawnOneSquare() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            int[] result = Chess_Com.handlePawnMove("e3", gs);
            assertEquals(1, result[0]); // validMove
            assertEquals(0, result[1]); // alreadyWrong
            assertEquals(1, gs.ChessBoard[5][4]); // e3(row5, col4)에 백 폰
            assertEquals(0, gs.ChessBoard[6][4]); // e2 비어있음
        }

        @Test
        @DisplayName("white pawn two square advance (e2 -> e4)")
        void whitePawnTwoSquares() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            int[] result = Chess_Com.handlePawnMove("e4", gs);
            assertEquals(1, result[0]);
            assertEquals(1, gs.ChessBoard[4][4]); // e4
            assertEquals(1, gs.enPassantMoveCount); // 앙파상 가능 표시
        }

        @Test
        @DisplayName("black pawn one square advance (e7 -> e6)")
        void blackPawnOneSquare() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.whiteToMove = false;
            int[] result = Chess_Com.handlePawnMove("e6", gs);
            assertEquals(1, result[0]);
            assertEquals(11, gs.ChessBoard[2][4]); // e6(row2, col4)에 흑 폰
        }

        @Test
        @DisplayName("blocked pawn cannot advance")
        void blockedPawn() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[5][4] = 1; // e3에 이미 백 폰
            int[] result = Chess_Com.handlePawnMove("e3", gs);
            assertEquals(0, result[0]); // 이동 불가
            assertEquals(1, result[1]); // 에러
        }

        @Test
        @DisplayName("occupied destination prevents pawn advance")
        void destinationOccupied() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[5][4] = 11; // e3에 흑 폰 (방해)
            int[] result = Chess_Com.handlePawnMove("e3", gs);
            assertEquals(0, result[0]);
        }

        @Test
        @DisplayName("two-square advance blocked when not on starting row")
        void twoSquaresFromNonStartRow() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // e3에 폰 배치 (초기 위치 아님)
            gs.ChessBoard[6][4] = 0;
            gs.ChessBoard[5][4] = 1;
            int[] result = Chess_Com.handlePawnMove("e5", gs);
            assertEquals(0, result[0]); // 2칸 이동 불가
        }
    }

    // =====================================================================
    // 13. handleShortCastle
    // =====================================================================
    @Nested
    @DisplayName("handleShortCastle: kingside castling")
    class HandleShortCastleTests {

        @Test
        @DisplayName("white kingside castling succeeds")
        void whiteShortCastleSuccess() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // f1, g1 비우기
            gs.ChessBoard[7][5] = 0;
            gs.ChessBoard[7][6] = 0;
            int[] result = Chess_Com.handleShortCastle(gs);
            assertEquals(1, result[0]);
            assertEquals(5, gs.ChessBoard[7][6]); // 킹 g1
            assertEquals(4, gs.ChessBoard[7][5]); // 룩 f1
            assertEquals(0, gs.ChessBoard[7][4]); // e1 비어있음
            assertEquals(0, gs.ChessBoard[7][7]); // h1 비어있음
            assertFalse(gs.whiteShortCastle);
            assertFalse(gs.whiteLongCastle);
        }

        @Test
        @DisplayName("blocked path prevents kingside castling")
        void blockedShortCastle() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // f1에 비숍이 있음 (초기 상태 그대로)
            int[] result = Chess_Com.handleShortCastle(gs);
            assertEquals(0, result[0]);
        }

        @Test
        @DisplayName("no castling right prevents kingside castling")
        void noCastlingRight() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[7][5] = 0;
            gs.ChessBoard[7][6] = 0;
            gs.whiteShortCastle = false;
            int[] result = Chess_Com.handleShortCastle(gs);
            assertEquals(0, result[0]);
        }

        @Test
        @DisplayName("black kingside castling succeeds")
        void blackShortCastleSuccess() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.whiteToMove = false;
            gs.ChessBoard[0][5] = 0;
            gs.ChessBoard[0][6] = 0;
            int[] result = Chess_Com.handleShortCastle(gs);
            assertEquals(1, result[0]);
            assertEquals(15, gs.ChessBoard[0][6]); // 흑 킹 g8
            assertEquals(14, gs.ChessBoard[0][5]); // 흑 룩 f8
        }
    }

    // =====================================================================
    // 14. handleLongCastle
    // =====================================================================
    @Nested
    @DisplayName("handleLongCastle: queenside castling")
    class HandleLongCastleTests {

        @Test
        @DisplayName("white queenside castling succeeds")
        void whiteLongCastleSuccess() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // b1, c1, d1 비우기
            gs.ChessBoard[7][1] = 0;
            gs.ChessBoard[7][2] = 0;
            gs.ChessBoard[7][3] = 0;
            int[] result = Chess_Com.handleLongCastle(gs);
            assertEquals(1, result[0]);
            assertEquals(5, gs.ChessBoard[7][2]); // 킹 c1
            assertEquals(4, gs.ChessBoard[7][3]); // 룩 d1
            assertEquals(0, gs.ChessBoard[7][0]); // a1 비어있음
        }

        @Test
        @DisplayName("blocked path prevents queenside castling")
        void blockedLongCastle() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // 초기 상태: b1, c1, d1에 기물 있음
            int[] result = Chess_Com.handleLongCastle(gs);
            assertEquals(0, result[0]);
        }
    }

    // =====================================================================
    // 15. handlePawnCapture
    // =====================================================================
    @Nested
    @DisplayName("handlePawnCapture: pawn capture & en passant")
    class HandlePawnCaptureTests {

        @Test
        @DisplayName("white pawn captures black piece (exd5)")
        void whitePawnCapture() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[6][4] = 0; // e2 비우기
            // handlePawnCapture에서 백 toAdd=1: gs.ChessBoard[row + 1][pawn] == whosePiece
            // 'exd5' → row=8-5=3, pawn=col(e)=4. 폰은 board[3+1][4]=board[4][4]에 있어야 함
            gs.ChessBoard[4][4] = 1; // 백 폰 e4 (row4, col4)
            gs.ChessBoard[3][3] = 11; // 흑 폰 d5 (잡힐 기물, row3, col3)
            int[] result = Chess_Com.handlePawnCapture("exd5", gs);
            assertEquals(1, result[0]);
            assertEquals(1, gs.ChessBoard[3][3]); // d5에 백 폰
            assertEquals(0, gs.ChessBoard[4][4]); // e4 비어있음
        }

        @Test
        @DisplayName("capture of empty square is invalid")
        void captureEmptySquare() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[6][4] = 0;
            gs.ChessBoard[3][4] = 1; // 백 폰 e5
            // d5는 비어있음
            int[] result = Chess_Com.handlePawnCapture("exd5", gs);
            assertEquals(0, result[0]);
        }

        @Test
        @DisplayName("en passant succeeds")
        void enPassant() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard[6][4] = 0;
            gs.ChessBoard[3][4] = 1;  // 백 폰 e5
            gs.ChessBoard[3][3] = 11; // 흑 폰 d5 (방금 2칸 이동)
            gs.enPassantAble = true;
            gs.enPassantRow = 3;
            gs.enPassantColunm = 3;

            // 앙파상: exd6 (d5에 있는 흑 폰을 지나쳐서 d6로)
            // row 6-Character.getNumericValue('6') = 8-6 = 2 → row=2, col=3 (d)
            // 백 폰 e5(row3, col4), 앙파상 목표 (row2, col3=d6)
            gs.enPassantRow = 2;   // 앙파상 목표 칸 row
            gs.enPassantColunm = 3; // 앙파상 목표 칸 col

            // 흑 폰은 (row3, col3)에 있고 목적지 (row2, col3)은 비어있어야 함
            gs.ChessBoard[2][3] = 0; // d6 비어있음

            int[] result = Chess_Com.handlePawnCapture("exd6", gs);
            assertEquals(1, result[0]);
            assertEquals(1, gs.ChessBoard[2][3]);  // 백 폰이 d6로
            assertEquals(0, gs.ChessBoard[3][3]);  // d5 흑 폰 제거
            assertEquals(0, gs.ChessBoard[3][4]);  // e5 비어있음
        }
    }

    // =====================================================================
    // 16. handleCheckValidation
    // =====================================================================
    @Nested
    @DisplayName("handleCheckValidation: check state validation")
    class HandleCheckValidationTests {

        @Test
        @DisplayName("king safe after move -> valid")
        void kingSafeAfterMove() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            int[][] backup = new int[8][8];
            for (int i = 0; i < 8; i++) backup[i] = Arrays.copyOf(gs.ChessBoard[i], 8);
            int[] result = Chess_Com.handleCheckValidation(gs, backup);
            assertEquals(1, result[0]);
        }

        @Test
        @DisplayName("king in check after move -> invalid and board restored")
        void kingInCheckAfterMove() {
            int[][] board = emptyBoard();
            board[7][4] = 5;  // 백 킹 e1
            board[7][7] = 4;  // 백 룩 h1
            board[0][4] = 14; // 흑 룩 e8 → e파일에서 백 킹 체크

            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard = board;
            gs.whiteToMove = true;

            // 백 룩을 어딘가 이동시킨 척 (보드는 이미 체크 상태)
            int[][] backup = emptyBoard();
            backup[7][4] = 5;
            backup[7][7] = 4;
            backup[0][4] = 14;

            int[] result = Chess_Com.handleCheckValidation(gs, backup);
            assertEquals(0, result[0]); // 체크 → 무효
            // 보드가 backup으로 복원됐는지 확인
            assertEquals(5, gs.ChessBoard[7][4]);
            assertEquals(4, gs.ChessBoard[7][7]);
        }
    }

    // =====================================================================
    // 17. handlePieceMove (나이트 기본 이동)
    // =====================================================================
    @Nested
    @DisplayName("handlePieceMove: piece move")
    class HandlePieceMoveTests {

        @Test
        @DisplayName("white knight Nf3 move succeeds")
        void knightMoveNf3() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // 초기 배치에서 g1 나이트가 f3으로 이동
            gs.ChessBoard[6][5] = 0; // f2 폰 제거 (경로 불필요하지만 클리어)
            int[] result = Chess_Com.handlePieceMove("Nf3", gs);
            assertEquals(1, result[0]);
            assertEquals(2, gs.ChessBoard[5][5]); // f3(row5,col5)에 백 나이트
            assertEquals(0, gs.ChessBoard[7][6]); // g1 비어있음
        }

        @Test
        @DisplayName("move to unreachable square is invalid")
        void knightInvalidDestination() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            int[] result = Chess_Com.handlePieceMove("Nd4", gs);
            assertEquals(0, result[0]); // 나이트가 d4로 갈 수 없음
        }

        @Test
        @DisplayName("white queen move succeeds (clear path)")
        void queenMove() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            // 빈 보드에 퀸 하나만 배치
            gs.ChessBoard = emptyBoard();
            gs.ChessBoard[7][4] = 5; // 백 킹
            gs.ChessBoard[7][3] = 6; // 백 퀸 d1
            int[] result = Chess_Com.handlePieceMove("Qd5", gs);
            assertEquals(1, result[0]);
            assertEquals(6, gs.ChessBoard[3][3]); // d5(row3,col3)에 퀸
        }

        @Test
        @DisplayName("knight capture Nxe5 succeeds")
        void knightCapture() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.ChessBoard = emptyBoard();
            gs.ChessBoard[7][4] = 5;  // 백 킹
            // e5(row3,col4)에서 나이트 역점프: (row±2,col±1) or (row±1,col±2)
            // f3(row5,col5) → e5(row3,col4): 행차=2, 열차=1 ✓
            gs.ChessBoard[5][5] = 2;  // 백 나이트 f3 (row5, col5)
            gs.ChessBoard[3][4] = 11; // 흑 폰 e5 (잡힐 기물)
            int[] result = Chess_Com.handlePieceMove("Nxe5", gs);
            assertEquals(1, result[0]);
            assertEquals(2, gs.ChessBoard[3][4]); // e5에 백 나이트
        }
    }

    // =====================================================================
    // 18. getMoveDestRow / getMoveDestCol
    // =====================================================================
    @Nested
    @DisplayName("getMoveDestRow / getMoveDestCol: move destination parsing")
    class MoveDestTests {

        @Test
        @DisplayName("2-char pawn move (e4) -> row=4, col=4")
        void pawnMove() {
            int[][] board = emptyBoard();
            assertEquals(4, Chess_Com.getMoveDestRow("e4", board));
            assertEquals(4, Chess_Com.getMoveDestCol("e4", board));
        }

        @Test
        @DisplayName("3-char piece move (Nf3) -> row=5, col=5")
        void pieceMove() {
            int[][] board = emptyBoard();
            assertEquals(5, Chess_Com.getMoveDestRow("Nf3", board));
            assertEquals(5, Chess_Com.getMoveDestCol("Nf3", board));
        }

        @Test
        @DisplayName("4-char capture (Nxe5) -> row=3, col=4")
        void pieceCapture() {
            int[][] board = emptyBoard();
            assertEquals(3, Chess_Com.getMoveDestRow("Nxe5", board));
            assertEquals(4, Chess_Com.getMoveDestCol("Nxe5", board));
        }

        @Test
        @DisplayName("4-char ambiguous move (Rad1) -> row=7, col=3")
        void ambiguousMove() {
            int[][] board = emptyBoard();
            assertEquals(7, Chess_Com.getMoveDestRow("Rad1", board));
            assertEquals(3, Chess_Com.getMoveDestCol("Rad1", board));
        }
    }

    // =====================================================================
    // 19. updateRookCastleFlags
    // =====================================================================
    @Nested
    @DisplayName("updateRookCastleFlags: castling right removal after rook move")
    class UpdateRookCastleFlagsTests {

        @Test
        @DisplayName("white rook moves on h-file (col7) -> removes kingside right")
        void whiteKingsideRookMoves() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            Chess_Com.updateRookCastleFlags(gs, 7);
            assertFalse(gs.whiteShortCastle);
            assertTrue(gs.whiteLongCastle); // 퀸사이드는 유지
        }

        @Test
        @DisplayName("white rook moves on a-file (col0) -> removes queenside right")
        void whiteQueensideRookMoves() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            Chess_Com.updateRookCastleFlags(gs, 0);
            assertFalse(gs.whiteLongCastle);
            assertTrue(gs.whiteShortCastle);
        }

        @Test
        @DisplayName("black rook moves on h-file -> removes black kingside right")
        void blackKingsideRookMoves() {
            Chess_Com.GameState gs = Chess_Com.initGame();
            gs.whiteToMove = false;
            Chess_Com.updateRookCastleFlags(gs, 7);
            assertFalse(gs.blackShortCastle);
            assertTrue(gs.blackLongCastle);
        }
    }

    // =====================================================================
    // 20. resolveAmbiguous
    // =====================================================================
    @Nested
    @DisplayName("resolveAmbiguous: ambiguous move resolution")
    class ResolveAmbiguousTests {

        @Test
        @DisplayName("resolve by column specifier succeeds")
        void resolveByColumn() {
            int[][] board = emptyBoard();
            board[7][4] = 5; // 백 킹
            ArrayList<Integer> rows = new ArrayList<>(Arrays.asList(7, 7));
            ArrayList<Integer> cols = new ArrayList<>(Arrays.asList(0, 7)); // a파일과 h파일 룩

            // 'a' 지정 → a파일(col0) 룩 선택
            int[] result = Chess_Com.resolveAmbiguous('a', rows, cols, 2, 7, 3, 4, board);
            assertEquals(1, result[0]); // 성공
            assertEquals(4, board[7][3]); // d1에 룩 이동
            assertEquals(0, board[7][0]); // a1 비어있음
        }

        @Test
        @DisplayName("resolve by row specifier succeeds")
        void resolveByRow() {
            int[][] board = emptyBoard();
            board[7][4] = 5; // 백 킹
            ArrayList<Integer> rows = new ArrayList<>(Arrays.asList(7, 0));
            ArrayList<Integer> cols = new ArrayList<>(Arrays.asList(0, 0));

            // '1' 지정 → 1번째 행(row7) 룩 선택
            int[] result = Chess_Com.resolveAmbiguous('1', rows, cols, 2, 7, 3, 4, board);
            assertEquals(1, result[0]);
        }

        @Test
        @DisplayName("no piece in specified column -> fails")
        void noRookInSpecifiedColumn() {
            int[][] board = emptyBoard();
            ArrayList<Integer> rows = new ArrayList<>(Arrays.asList(7, 7));
            ArrayList<Integer> cols = new ArrayList<>(Arrays.asList(0, 7));

            // 'c' 지정 → c파일에 룩 없음
            int[] result = Chess_Com.resolveAmbiguous('c', rows, cols, 2, 7, 3, 4, board);
            assertEquals(0, result[0]);
            assertEquals(1, result[2]); // ambiguousMove
        }
    }
}
