import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Chess_Com{
    /* 
        <numbers in ChessBoard 2d array definitions>
        0-empty
        1-pawn
        2-knight
        3-bishop
        4-rook
        5-king
        6-queen
        add 10 for black pieces
        straight forward init?
    */
    
//====================================MAIN==========================================

    // ============ GameState: holds all mutable game state ============
    static class GameState {
        int[][] ChessBoard = {
            {14,12,13,16,15,13,12,14},
            {11,11,11,11,11,11,11,11},
            {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
            {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
            {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
            {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
            {1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 },
            {4 ,2 ,3 ,6 ,5 ,3 ,2 ,4 }
        };
        boolean whiteToMove = true;
        int enPassantRow = 0;
        int enPassantColunm = 0;
        int enPassantMoveCount = 0;
        boolean enPassantAble = false;
        boolean whiteShortCastle = true;
        boolean whiteLongCastle = true;
        boolean blackShortCastle = true;
        boolean blackLongCastle = true;
        int fiftyMoveDrawCount = 0;
        boolean isWhiteResigned = false;
        boolean isBlackResigned = false;
        boolean drawOffer = false;
        boolean drawAccepted = false;
        ArrayList<String> positionHistory = new ArrayList<>();
        ArrayList<String> moveHistory = new ArrayList<>();
    }

    public static void main(String[] args) {

        Scanner myScanner = new Scanner(System.in);

        while (true){
            //Lobby screen
            clearTerminal();
            System.out.println("[Chess.Com for poor users]");
            System.out.println("enter 1 to play");
            System.out.println("enter 2 to learn how to play");
            int respond = myScanner.nextInt();

            //play
            if (respond == 1){
                clearTerminal();
                GameState gs = initGame();
                runGame(myScanner, gs);
                displayGameResult(gs);
            }
            else if (respond == 2){
                showHelp(myScanner);
            }
        }
    }

    // ============ initGame: initialize and return a fresh GameState ============
    public static GameState initGame() {
        return new GameState();
    }

    // ============ runGame: main game loop ============
    public static void runGame(Scanner myScanner, GameState gs) {
        while (whatKindOfMate(gs.whiteToMove, gs.ChessBoard) == 0
                && gs.fiftyMoveDrawCount < 100
                && !isThreefoldRepetition(gs.positionHistory, gs.ChessBoard)
                && !isInsufficientMaterial(gs.ChessBoard)
                && !gs.isWhiteResigned
                && !gs.isBlackResigned
                && !gs.drawAccepted) {

            handleTurn(myScanner, gs);
            clearTerminal();
        }
    }

    // ============ handleTurn: process one full turn until a valid move is made ============
    public static void handleTurn(Scanner myScanner, GameState gs) {
        String move = "";
        boolean validMove = false;
        boolean alreadyWrong = false;
        boolean ambiguousMove = false;
        boolean inCheck = false;

        while (!validMove) {
            // snapshot of the board to check if its in check
            int[][] backupBoard = new int[8][8];
            for (int i = 0; i < 8; i++) {
                backupBoard[i] = Arrays.copyOf(gs.ChessBoard[i], 8);
            }

            // draw ChessBoard
            clearTerminal();
            display(gs.ChessBoard, gs.moveHistory);
            System.out.println("______________________________________");

            if (ambiguousMove) {
                System.out.println("ERROR: Ambiguous move, please specify the piece. (eg: Rae5 / Nbd2 / Nf3d4)");
            } else if (inCheck) {
                System.out.println("ERROR: You are still in check");
            } else if (alreadyWrong) {
                System.out.println("ERROR: Illegal move, enter again.");
            }

            // draw offer handling
            if (gs.drawOffer) {
                System.out.println("Draw offered (y / n): ");
                String answer = myScanner.next();
                if (answer.equals("y")) {
                    gs.drawAccepted = true;
                    validMove = true;
                } else {
                    gs.drawOffer = false;
                    move = "";
                    continue;
                }
            } else if (gs.whiteToMove) {
                System.out.println("White to Move");
                System.out.print("Enter a move: ");
                move = myScanner.next();
            } else {
                System.out.println("Black to Move");
                System.out.print("Enter a move: ");
                move = myScanner.next();
            }

            // enPassant counter
            if (gs.enPassantMoveCount >= 1) {
                gs.enPassantAble = true;
                gs.enPassantMoveCount = 0;
            } else {
                gs.enPassantAble = false;
                gs.enPassantRow = -1;
                gs.enPassantColunm = -1;
            }

            // resign / draw commands
            if (move.equals("resign") || move.equals("Resign")) {
                if (gs.whiteToMove) gs.isWhiteResigned = true;
                else gs.isBlackResigned = true;
                validMove = true;
            } else if (move.equals("draw") || move.equals("Draw")) {
                gs.drawOffer = true;
                continue;
            }
            // pawn move (e4, d5, ...)
            else if (move.length() == 2) {
                int[] result = handlePawnMove(move, gs);
                validMove = result[0] == 1;
                alreadyWrong = result[1] == 1;
            }
            // short castle
            else if (move.equals("0-0") || move.equals("O-O")) {
                int[] result = handleShortCastle(gs);
                validMove = result[0] == 1;
                alreadyWrong = result[1] == 1;
            }
            // long castle
            else if (move.equals("0-0-0") || move.equals("O-O-O")) {
                int[] result = handleLongCastle(gs);
                validMove = result[0] == 1;
                alreadyWrong = result[1] == 1;
            }
            // piece moves (Nf3, Qxe5, Rad1, ...)
            else if (move.length() == 3
                    || move.length() == 4 && Character.isUpperCase(move.charAt(0))
                    || move.length() == 5 && Character.isUpperCase(move.charAt(0))) {
                int[] result = handlePieceMove(move, gs);
                validMove    = result[0] == 1;
                alreadyWrong = result[1] == 1;
                ambiguousMove = result[2] == 1;
            }
            // pawn capture (exd5, en passant)
            else if (move.length() == 4 && Character.isLowerCase(move.charAt(0)) && move.charAt(1) == 'x') {
                int[] result = handlePawnCapture(move, gs);
                validMove    = result[0] == 1;
                alreadyWrong = result[1] == 1;
            }
            else {
                validMove = false;
                alreadyWrong = true;
            }

            // check validation
            if (validMove) {
                int[] result = handleCheckValidation(gs, backupBoard);
                if (result[0] == 0) {
                    validMove = false;
                    inCheck = true;
                }
            }

            // pawn promotion
            if (validMove) {
                handlePromotion(myScanner, gs);
            }

            // move history / 50-move count update
            if (validMove) {
                int row = -1, colunm = -1;
                // find destination square from move string for 50-move count
                row    = getMoveDestRow(move, gs.ChessBoard);
                colunm = getMoveDestCol(move, gs.ChessBoard);
                boolean isCapture = (row >= 0 && colunm >= 0 && backupBoard[row][colunm] != 0);
                boolean isPawnMove2 = (row >= 0 && colunm >= 0 && gs.ChessBoard[row][colunm] % 10 == 1);
                if (isCapture || isPawnMove2) gs.fiftyMoveDrawCount = 0;
                else gs.fiftyMoveDrawCount++;
                gs.positionHistory.add(boardToString(gs.ChessBoard));
                updateMoveHistory(move, gs, backupBoard);
            }

            // change player
            if (validMove) {
                gs.whiteToMove = !gs.whiteToMove;
            }
        }
    }

    // ============ handlePawnMove: handle simple pawn push (e4, d5, ...) ============
    // returns int[]{validMove, alreadyWrong}  (1=true, 0=false)
    public static int[] handlePawnMove(String move, GameState gs) {
        int colunm = alphaToNum(move.charAt(0)) - 1;
        int row    = 8 - Character.getNumericValue(move.charAt(1));

        if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && gs.ChessBoard[row][colunm] == 0) {
            int toAdd = gs.whiteToMove ? 1 : -1;
            int whosePiece = gs.whiteToMove ? 1 : 11;

            // one square forward
            if ((row + toAdd >= 0) && (row + toAdd < 8) && (gs.ChessBoard[row + toAdd][colunm] == whosePiece)) {
                gs.ChessBoard[row + toAdd][colunm] = 0;
                gs.ChessBoard[row][colunm] = whosePiece;
                return new int[]{1, 0};
            }
            // two squares forward (starter boost)
            else if ((row + toAdd*2 >= 0) && (row + toAdd*2 < 8)
                    && (gs.ChessBoard[row + (toAdd*2)][colunm] == whosePiece)
                    && (gs.ChessBoard[row + toAdd][colunm] == 0)
                    && ((row + (toAdd * 2) == 1) || (row + (toAdd * 2) == 6))) {
                gs.ChessBoard[row + (toAdd*2)][colunm] = 0;
                gs.ChessBoard[row][colunm] = whosePiece;
                gs.enPassantMoveCount += 1;
                gs.enPassantRow = row + toAdd;
                gs.enPassantColunm = colunm;
                return new int[]{1, 0};
            }
            else {
                return new int[]{0, 1};
            }
        }
        return new int[]{0, 1};
    }

    // ============ handleShortCastle: handle O-O / 0-0 ============
    // returns int[]{validMove, alreadyWrong}
    public static int[] handleShortCastle(GameState gs) {
        int whoseKing = gs.whiteToMove ? 5 : 15;
        int whoseRook = gs.whiteToMove ? 4 : 14;
        int tempRow   = gs.whiteToMove ? 7 : 0;

        if (gs.ChessBoard[tempRow][7] != whoseRook) {
            if (gs.whiteToMove) gs.whiteShortCastle = false;
            else gs.blackShortCastle = false;
            return new int[]{0, 1};
        }
        if ((gs.ChessBoard[tempRow][5] == 0 && gs.ChessBoard[tempRow][6] == 0
                && !isThisSquareUnderAttack(tempRow, 4, !gs.whiteToMove, gs.ChessBoard)
                && !isThisSquareUnderAttack(tempRow, 5, !gs.whiteToMove, gs.ChessBoard)
                && !isThisSquareUnderAttack(tempRow, 6, !gs.whiteToMove, gs.ChessBoard))
                && ((gs.whiteToMove && gs.whiteShortCastle) || (!gs.whiteToMove && gs.blackShortCastle))) {
            gs.ChessBoard[tempRow][4] = 0;
            gs.ChessBoard[tempRow][5] = whoseRook;
            gs.ChessBoard[tempRow][6] = whoseKing;
            gs.ChessBoard[tempRow][7] = 0;
            if (gs.whiteToMove) { gs.whiteShortCastle = false; gs.whiteLongCastle = false; }
            else                { gs.blackShortCastle = false; gs.blackLongCastle = false; }
            return new int[]{1, 0};
        }
        return new int[]{0, 1};
    }

    // ============ handleLongCastle: handle O-O-O / 0-0-0 ============
    // returns int[]{validMove, alreadyWrong}
    public static int[] handleLongCastle(GameState gs) {
        int whoseKing = gs.whiteToMove ? 5 : 15;
        int whoseRook = gs.whiteToMove ? 4 : 14;
        int tempRow   = gs.whiteToMove ? 7 : 0;

        if (gs.ChessBoard[tempRow][0] != whoseRook) {
            if (gs.whiteToMove) gs.whiteLongCastle = false;
            else gs.blackLongCastle = false;
            return new int[]{0, 1};
        }
        if ((gs.ChessBoard[tempRow][3] == 0 && gs.ChessBoard[tempRow][2] == 0 && gs.ChessBoard[tempRow][1] == 0
                && !isThisSquareUnderAttack(tempRow, 4, !gs.whiteToMove, gs.ChessBoard)
                && !isThisSquareUnderAttack(tempRow, 3, !gs.whiteToMove, gs.ChessBoard)
                && !isThisSquareUnderAttack(tempRow, 2, !gs.whiteToMove, gs.ChessBoard))
                && ((gs.whiteToMove && gs.whiteLongCastle) || (!gs.whiteToMove && gs.blackLongCastle))) {
            gs.ChessBoard[tempRow][4] = 0;
            gs.ChessBoard[tempRow][3] = whoseRook;
            gs.ChessBoard[tempRow][2] = whoseKing;
            gs.ChessBoard[tempRow][0] = 0;
            if (gs.whiteToMove) { gs.whiteShortCastle = false; gs.whiteLongCastle = false; }
            else                { gs.blackShortCastle = false; gs.blackLongCastle = false; }
            return new int[]{1, 0};
        }
        return new int[]{0, 1};
    }

    // ============ handlePieceMove: handle piece moves (Nf3, Qxe5, Rad1, ...) ============
    // returns int[]{validMove, alreadyWrong, ambiguousMove}
    public static int[] handlePieceMove(String move, GameState gs) {
        boolean takes = false;
        char specify = ' ';
        boolean successfulMove = true;
        int piece = 0;
        int colunm = 0;
        int row = 0;

        if (move.length() == 3) {
            piece  = PieceToNum(move.charAt(0));
            colunm = alphaToNum(move.charAt(1)) - 1;
            row    = 8 - Character.getNumericValue(move.charAt(2));
        }
        else if (move.length() == 4 && move.charAt(1) == 'x'
                && (move.charAt(2) >= 'a' && move.charAt(2) <= 'h')
                && (move.charAt(3) >= '1' && move.charAt(3) <= '8')) {
            piece  = PieceToNum(move.charAt(0));
            colunm = alphaToNum(move.charAt(2)) - 1;
            row    = 8 - Character.getNumericValue(move.charAt(3));
            takes  = true;
        }
        else if (move.length() == 4
                && ((move.charAt(1) >= 'a' && move.charAt(1) <= 'h') || (move.charAt(1) >= '1' && move.charAt(1) <= '8'))
                && (move.charAt(2) >= 'a' && move.charAt(2) <= 'h')
                && (move.charAt(3) >= '1' && move.charAt(3) <= '8')) {
            piece   = PieceToNum(move.charAt(0));
            colunm  = alphaToNum(move.charAt(2)) - 1;
            row     = 8 - Character.getNumericValue(move.charAt(3));
            specify = move.charAt(1);
        }
        else if (move.length() == 5 && move.charAt(2) == 'x'
                && ((move.charAt(1) >= 'a' && move.charAt(1) <= 'h') || (move.charAt(1) >= '1' && move.charAt(1) <= '8'))
                && (move.charAt(3) >= 'a' && move.charAt(3) <= 'h')
                && (move.charAt(4) >= '1' && move.charAt(4) <= '8')) {
            piece   = PieceToNum(move.charAt(0));
            colunm  = alphaToNum(move.charAt(3)) - 1;
            row     = 8 - Character.getNumericValue(move.charAt(4));
            specify = move.charAt(1);
            takes   = true;
        }
        else {
            successfulMove = false;
        }

        if (takes && row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && gs.ChessBoard[row][colunm] == 0) {
            return new int[]{0, 1, 0};
        }

        if (!successfulMove) return new int[]{0, 1, 0};

        int whosePiece = piece;
        if (!gs.whiteToMove) whosePiece += 10;

        //================================The Knight==================================
        if (piece == 2) {
            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8
                    && (gs.ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(gs.ChessBoard[row][colunm], whosePiece))) {
                int[] knightJumpVertical   = {+2, +2, +1, +1, -1, -1, -2, -2};
                int[] knightJumpHorizontal = {+1, -1, +2, -2, +2, -2, +1, -1};
                int knightCount = 0;
                ArrayList<Integer> knightColumn = new ArrayList<>();
                ArrayList<Integer> knightRow    = new ArrayList<>();

                for (int i = 0; i < 8; i++) {
                    if (((colunm + knightJumpHorizontal[i] >= 0) && (colunm + knightJumpHorizontal[i] <= 7))
                            && ((row + knightJumpVertical[i] >= 0) && (row + knightJumpVertical[i] <= 7))) {
                        if (gs.ChessBoard[row + knightJumpVertical[i]][colunm + knightJumpHorizontal[i]] == whosePiece) {
                            knightCount++;
                            knightColumn.add(colunm + knightJumpHorizontal[i]);
                            knightRow.add(row + knightJumpVertical[i]);
                        }
                    }
                }
                if (knightCount >= 1) {
                    if (knightCount == 1) {
                        gs.ChessBoard[knightRow.get(0)][knightColumn.get(0)] = 0;
                        gs.ChessBoard[row][colunm] = whosePiece;
                        return new int[]{1, 0, 0};
                    } else if (specify != ' ') {
                        int[] res = resolveAmbiguous(specify, knightRow, knightColumn, knightCount, row, colunm, whosePiece, gs.ChessBoard);
                        return res;
                    } else {
                        return new int[]{0, 0, 1};
                    }
                } else {
                    return new int[]{0, 1, 0};
                }
            }
            return new int[]{0, 1, 0};
        }

        //============================The Bishop================================
        if (piece == 3) {
            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8
                    && (gs.ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(gs.ChessBoard[row][colunm], whosePiece))) {
                int[] bishopMoveVertical   = {+1, +1, -1, -1};
                int[] bishopMoveHorizontal = {+1, -1, -1, +1};
                int bishopCount = 0;
                ArrayList<Integer> bishopColumn = new ArrayList<>();
                ArrayList<Integer> bishopRow    = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    int magnitude = 1;
                    while ((row + (bishopMoveVertical[i] * magnitude) >= 0) && (colunm + (bishopMoveHorizontal[i] * magnitude) >= 0)
                            && (row + (bishopMoveVertical[i] * magnitude) <= 7) && (colunm + (bishopMoveHorizontal[i] * magnitude) <= 7)) {
                        int tr = row + bishopMoveVertical[i] * magnitude;
                        int tc = colunm + bishopMoveHorizontal[i] * magnitude;
                        if (gs.ChessBoard[tr][tc] == whosePiece) {
                            bishopCount++;
                            bishopColumn.add(tc);
                            bishopRow.add(tr);
                            break;
                        } else if (gs.ChessBoard[tr][tc] == 0) {
                            magnitude++;
                        } else {
                            break;
                        }
                    }
                }
                if (bishopCount >= 1) {
                    if (bishopCount == 1) {
                        gs.ChessBoard[bishopRow.get(0)][bishopColumn.get(0)] = 0;
                        gs.ChessBoard[row][colunm] = whosePiece;
                        return new int[]{1, 0, 0};
                    } else if (specify != ' ') {
                        return resolveAmbiguous(specify, bishopRow, bishopColumn, bishopCount, row, colunm, whosePiece, gs.ChessBoard);
                    } else {
                        return new int[]{0, 0, 1};
                    }
                } else {
                    return new int[]{0, 1, 0};
                }
            }
            return new int[]{0, 1, 0};
        }

        //===============================THE ROOK================================
        if (piece == 4) {
            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8
                    && (gs.ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(gs.ChessBoard[row][colunm], whosePiece))) {
                int[] rookMoveVertical   = {1, 0, -1, 0};
                int[] rookMoveHorizontal = {0, -1, 0, 1};
                int rookCount = 0;
                ArrayList<Integer> rookColumn = new ArrayList<>();
                ArrayList<Integer> rookRow    = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    int magnitude = 1;
                    while ((row + (rookMoveVertical[i] * magnitude) >= 0) && (colunm + (rookMoveHorizontal[i] * magnitude) >= 0)
                            && (row + (rookMoveVertical[i] * magnitude) <= 7) && (colunm + (rookMoveHorizontal[i] * magnitude) <= 7)) {
                        int tr = row + rookMoveVertical[i] * magnitude;
                        int tc = colunm + rookMoveHorizontal[i] * magnitude;
                        if (gs.ChessBoard[tr][tc] == whosePiece) {
                            rookCount++;
                            rookColumn.add(tc);
                            rookRow.add(tr);
                            break;
                        } else if (gs.ChessBoard[tr][tc] == 0) {
                            magnitude++;
                        } else {
                            break;
                        }
                    }
                }
                if (rookCount >= 1) {
                    if (rookCount == 1) {
                        int rCol = rookColumn.get(0), rRow = rookRow.get(0);
                        gs.ChessBoard[rRow][rCol] = 0;
                        gs.ChessBoard[row][colunm] = whosePiece;
                        updateRookCastleFlags(gs, rCol);
                        return new int[]{1, 0, 0};
                    } else if (specify != ' ') {
                        int[] res = resolveAmbiguous(specify, rookRow, rookColumn, rookCount, row, colunm, whosePiece, gs.ChessBoard);
                        if (res[0] == 1) updateRookCastleFlags(gs, getMoveSourceCol(specify, rookRow, rookColumn, rookCount));
                        return res;
                    } else {
                        return new int[]{0, 0, 1};
                    }
                } else {
                    return new int[]{0, 1, 0};
                }
            }
            return new int[]{0, 1, 0};
        }

        //================================The King====================================
        if (piece == 5) {
            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8
                    && (gs.ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(gs.ChessBoard[row][colunm], whosePiece))) {
                int[] kingMoveVertical   = {1, 1, 0, -1, -1, -1, 0, 1};
                int[] kingMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};
                boolean isKingMoved = false;

                for (int i = 0; i < 8; i++) {
                    if ((colunm + kingMoveHorizontal[i] >= 0) && (colunm + kingMoveHorizontal[i] <= 7)
                            && (row + kingMoveVertical[i] >= 0) && (row + kingMoveVertical[i] <= 7)) {
                        if (gs.ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] == whosePiece) {
                            gs.ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] = 0;
                            if (isThisSquareUnderAttack(row, colunm, !gs.whiteToMove, gs.ChessBoard)) {
                                gs.ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] = whosePiece;
                                break;
                            } else {
                                gs.ChessBoard[row][colunm] = whosePiece;
                                isKingMoved = true;
                                break;
                            }
                        }
                    }
                }
                if (isKingMoved) {
                    if (gs.whiteToMove) { gs.whiteLongCastle = false; gs.whiteShortCastle = false; }
                    else                { gs.blackLongCastle = false; gs.blackShortCastle = false; }
                    return new int[]{1, 0, 0};
                }
                return new int[]{0, 1, 0};
            }
            return new int[]{0, 1, 0};
        }

        //==================================The Queen==============================
        if (piece == 6) {
            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8
                    && (gs.ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(gs.ChessBoard[row][colunm], whosePiece))) {
                int[] queenMoveVertical   = {1, 1, 0, -1, -1, -1, 0, 1};
                int[] queenMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};
                int queenCount = 0;
                ArrayList<Integer> queenColumn = new ArrayList<>();
                ArrayList<Integer> queenRow    = new ArrayList<>();

                for (int i = 0; i < 8; i++) {
                    int magnitude = 1;
                    while ((row + (queenMoveVertical[i] * magnitude) >= 0) && (colunm + (queenMoveHorizontal[i] * magnitude) >= 0)
                            && (row + (queenMoveVertical[i] * magnitude) <= 7) && (colunm + (queenMoveHorizontal[i] * magnitude) <= 7)) {
                        int tr = row + queenMoveVertical[i] * magnitude;
                        int tc = colunm + queenMoveHorizontal[i] * magnitude;
                        if (gs.ChessBoard[tr][tc] == whosePiece) {
                            queenCount++;
                            queenColumn.add(tc);
                            queenRow.add(tr);
                            break;
                        } else if (gs.ChessBoard[tr][tc] == 0) {
                            magnitude++;
                        } else {
                            break;
                        }
                    }
                }
                if (queenCount >= 1) {
                    if (queenCount == 1) {
                        gs.ChessBoard[queenRow.get(0)][queenColumn.get(0)] = 0;
                        gs.ChessBoard[row][colunm] = whosePiece;
                        return new int[]{1, 0, 0};
                    } else if (specify != ' ') {
                        return resolveAmbiguous(specify, queenRow, queenColumn, queenCount, row, colunm, whosePiece, gs.ChessBoard);
                    } else {
                        return new int[]{0, 0, 1};
                    }
                } else {
                    return new int[]{0, 1, 0};
                }
            }
            return new int[]{0, 1, 0};
        }

        return new int[]{0, 1, 0};
    }

    // ============ resolveAmbiguous: resolve ambiguous piece move ============
    // returns int[]{validMove, alreadyWrong, ambiguousMove}
    public static int[] resolveAmbiguous(char specify, ArrayList<Integer> pieceRow, ArrayList<Integer> pieceColumn,
            int pieceCount, int destRow, int destCol, int whosePiece, int[][] board) {
        int countHasToBe1 = 0;
        int pCol = 0, pRow = 0;
        if (specify >= 'a' && specify <= 'h') {
            for (int i = 0; i < pieceCount; i++) {
                if (pieceColumn.get(i) == alphaToNum(specify) - 1) {
                    pCol = pieceColumn.get(i);
                    pRow = pieceRow.get(i);
                    countHasToBe1++;
                }
            }
            if (countHasToBe1 == 1) {
                board[pRow][pCol] = 0;
                board[destRow][destCol] = whosePiece;
                return new int[]{1, 0, 0};
            } else {
                return new int[]{0, 0, 1};
            }
        } else if (specify >= '1' && specify <= '8') {
            for (int i = 0; i < pieceCount; i++) {
                if (pieceRow.get(i) == (8 - Character.getNumericValue(specify))) {
                    pCol = pieceColumn.get(i);
                    pRow = pieceRow.get(i);
                    countHasToBe1++;
                }
            }
            if (countHasToBe1 == 1) {
                board[pRow][pCol] = 0;
                board[destRow][destCol] = whosePiece;
                return new int[]{1, 0, 0};
            } else {
                return new int[]{0, 0, 1};
            }
        }
        return new int[]{0, 1, 0};
    }

    // ============ updateRookCastleFlags: disable castling right after rook moves ============
    public static void updateRookCastleFlags(GameState gs, int rookCol) {
        if (gs.whiteToMove) {
            if (rookCol == 7) gs.whiteShortCastle = false;
            else if (rookCol == 0) gs.whiteLongCastle = false;
        } else {
            if (rookCol == 7) gs.blackShortCastle = false;
            else if (rookCol == 0) gs.blackLongCastle = false;
        }
    }

    // helper: get source column of an ambiguous piece
    public static int getMoveSourceCol(char specify, ArrayList<Integer> pieceRow, ArrayList<Integer> pieceColumn, int pieceCount) {
        if (specify >= 'a' && specify <= 'h') {
            for (int i = 0; i < pieceCount; i++) {
                if (pieceColumn.get(i) == alphaToNum(specify) - 1) return pieceColumn.get(i);
            }
        } else if (specify >= '1' && specify <= '8') {
            for (int i = 0; i < pieceCount; i++) {
                if (pieceRow.get(i) == (8 - Character.getNumericValue(specify))) return pieceColumn.get(i);
            }
        }
        return -1;
    }

    // ============ handlePawnCapture: handle pawn captures and en passant ============
    // returns int[]{validMove, alreadyWrong}
    public static int[] handlePawnCapture(String move, GameState gs) {
        if ((move.charAt(2) >= 'a' && move.charAt(2) <= 'h')
                && (move.charAt(3) >= '1' && move.charAt(3) <= '8')
                && move.charAt(0) >= 'a' && move.charAt(0) <= 'h') {
            int pawn   = alphaToNum(move.charAt(0)) - 1;
            int colunm = alphaToNum(move.charAt(2)) - 1;
            int row    = 8 - Character.getNumericValue(move.charAt(3));
            int whosePiece = gs.whiteToMove ? 1 : 11;
            int toAdd  = gs.whiteToMove ? 1 : -1;

            // normal capture
            if ((row + toAdd >= 0) && (row + toAdd < 8)
                    && (gs.ChessBoard[row][colunm] != 0)
                    && (gs.ChessBoard[row + toAdd][pawn] == whosePiece)
                    && (colunm + 1 == pawn || colunm - 1 == pawn)) {
                gs.ChessBoard[row][colunm] = whosePiece;
                gs.ChessBoard[row + toAdd][pawn] = 0;
                return new int[]{1, 0};
            }
            // en passant
            else if (gs.enPassantAble && row == gs.enPassantRow && colunm == gs.enPassantColunm
                    && (gs.ChessBoard[row + toAdd][pawn] == whosePiece)
                    && (colunm + 1 == pawn || colunm - 1 == pawn)
                    && (gs.ChessBoard[row][colunm] == 0)) {
                gs.ChessBoard[row][colunm] = whosePiece;
                gs.ChessBoard[row + toAdd][pawn] = 0;
                gs.ChessBoard[row + toAdd][colunm] = 0;
                return new int[]{1, 0};
            }
            return new int[]{0, 1};
        }
        return new int[]{0, 1};
    }

    // ============ handleCheckValidation: undo move if king is still in check ============
    // returns int[]{1} if king is safe, int[]{0} if still in check
    public static int[] handleCheckValidation(GameState gs, int[][] backupBoard) {
        int myKing = gs.whiteToMove ? 5 : 15;
        int kingRow = -1, kingCol = -1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gs.ChessBoard[i][j] == myKing) {
                    kingRow = i;
                    kingCol = j;
                    break;
                }
            }
        }
        if (isThisSquareUnderAttack(kingRow, kingCol, !gs.whiteToMove, gs.ChessBoard)) {
            for (int i = 0; i < 8; i++) {
                gs.ChessBoard[i] = Arrays.copyOf(backupBoard[i], 8);
            }
            return new int[]{0};
        }
        return new int[]{1};
    }

    // ============ handlePromotion: handle pawn promotion ============
    public static void handlePromotion(Scanner myScanner, GameState gs) {
        int promotionRow   = gs.whiteToMove ? 0 : 7;
        int promotingPawn  = gs.whiteToMove ? 1 : 11;

        for (int col = 0; col < 8; col++) {
            if (gs.ChessBoard[promotionRow][col] == promotingPawn) {
                clearTerminal();
                display(gs.ChessBoard, gs.moveHistory);
                System.out.println("______________________________________");

                char choice = ' ';
                boolean correctPiece = true;
                while (choice != 'Q' && choice != 'R' && choice != 'B' && choice != 'N') {
                    if (correctPiece) {
                        correctPiece = false;
                        System.out.print("Choose piece (Q/R/B/N): ");
                    } else {
                        System.out.print("Invalid. Please choose among these (Q/R/B/N): ");
                    }
                    choice = myScanner.next().toUpperCase().charAt(0);
                }
                int promotedPiece = 0;
                if      (choice == 'Q') promotedPiece = 6;
                else if (choice == 'R') promotedPiece = 4;
                else if (choice == 'B') promotedPiece = 3;
                else if (choice == 'N') promotedPiece = 2;
                if (!gs.whiteToMove) promotedPiece += 10;
                gs.ChessBoard[promotionRow][col] = promotedPiece;
            }
        }
    }

    // ============ getMoveDestRow/Col: extract destination from move string ============
    public static int getMoveDestRow(String move, int[][] board) {
        try {
            if (move.length() == 2) return 8 - Character.getNumericValue(move.charAt(1));
            if (move.length() == 3) return 8 - Character.getNumericValue(move.charAt(2));
            if (move.length() == 4) return 8 - Character.getNumericValue(move.charAt(3));
            if (move.length() == 5) return 8 - Character.getNumericValue(move.charAt(4));
        } catch (Exception e) {}
        return -1;
    }

    public static int getMoveDestCol(String move, int[][] board) {
        try {
            if (move.length() == 2) return alphaToNum(move.charAt(0)) - 1;
            if (move.length() == 3) return alphaToNum(move.charAt(1)) - 1;
            if (move.length() == 4 && move.charAt(1) == 'x') return alphaToNum(move.charAt(2)) - 1;
            if (move.length() == 4) return alphaToNum(move.charAt(2)) - 1;
            if (move.length() == 5) return alphaToNum(move.charAt(3)) - 1;
        } catch (Exception e) {}
        return -1;
    }

    // ============ updateMoveHistory: append move notation to history ============
    public static void updateMoveHistory(String move, GameState gs, int[][] backupBoard) {
        int row    = getMoveDestRow(move, gs.ChessBoard);
        int colunm = getMoveDestCol(move, gs.ChessBoard);
        boolean isCapture = (row >= 0 && colunm >= 0 && backupBoard[row][colunm] != 0);

        String moveLog = move;
        if (move.equals("O-O"))       moveLog = "0-0";
        else if (move.equals("O-O-O")) moveLog = "0-0-0";
        else {
            if (isCapture && move.length() == 3 && Character.isUpperCase(move.charAt(0))) {
                moveLog = move.charAt(0) + "x" + move.substring(1);
            }
            int enemyKing = gs.whiteToMove ? 15 : 5;
            int enemyKingRow = 0, enemyKingCol = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (gs.ChessBoard[i][j] == enemyKing) {
                        enemyKingRow = i;
                        enemyKingCol = j;
                        break;
                    }
                }
            }
            if (isThisSquareUnderAttack(enemyKingRow, enemyKingCol, gs.whiteToMove, gs.ChessBoard)) {
                if (whatKindOfMate(!gs.whiteToMove, gs.ChessBoard) == 1) moveLog += "#";
                else moveLog += "+";
            }
        }
        gs.moveHistory.add(moveLog);
    }

    // ============ displayGameResult: print game result after game ends ============
    public static void displayGameResult(GameState gs) {
        display(gs.ChessBoard, gs.moveHistory);
        System.out.println("______________________________________");

        if (gs.isBlackResigned || gs.isWhiteResigned) {
            System.out.println("Resigned!");
            if (gs.isWhiteResigned) System.out.println("Black wins!!  (0 - 1)");
            else System.out.println("White WIns!!  (1 - 0)");
        } else if (gs.drawAccepted) {
            System.out.println("Draw by agreement!");
            System.out.println("(1/2 - 1/2)");
        } else if (gs.fiftyMoveDrawCount >= 100) {
            System.out.println("Draw by 50-move Rule!");
            System.out.println("(1/2 - 1/2)");
        } else if (isInsufficientMaterial(gs.ChessBoard)) {
            System.out.println("Draw by Insuficient Material!");
            System.out.println("(1/2 - 1/2)");
        } else if (isThreefoldRepetition(gs.positionHistory, gs.ChessBoard)) {
            System.out.println("Draw by Threefold Repetition!");
            System.out.println("(1/2 - 1/2)");
        } else if (whatKindOfMate(gs.whiteToMove, gs.ChessBoard) == 1) {
            System.out.println("Checkmate!");
            if (gs.whiteToMove) System.out.println("Black wins!!  (0 - 1)");
            else System.out.println("White wins!!  (1 - 0)");
        } else {
            System.out.println("Stalemate!");
            System.out.println("(1/2 - 1/2)");
        }
    }

    // ============ showHelp: print help / tutorial screen ============
    public static void showHelp(Scanner myScanner) {
        clearTerminal();
        System.out.print("""
                ===========================================
                HELP
                ===========================================
                PIECE NAMES:
                K = King   Q = Queen   R = Rook
                B = Bishop N = Knight  Pawn (no letter)

                HOW TO INPUT YOUR MOVE:

                Pawn move
                    Type the column and row you want to move to.
                    Example: e4 (move pawn to e4)

                Piece move
                    Type the piece letter + destination.
                    Example: Nf3 (move Knight to f3)

                Capture
                    You can type with or without 'x'.
                    Example: Nxe5 or Ne5 (both work)

                Pawn capture
                    Type your pawn's column + x + destination.
                    Example: exd5 (pawn on e captures on d5)

                Castling
                    0-0 or O-O      : Castle kingside (short castle)
                    0-0-0 or O-O-O  : Castle queenside (long castle)

                En Passant
                    Type one square behind the pawn you want to capture.
                    If enemy pawn on d7 moves to d5 and you have pawn on e5,
                    Example: exd6

                Promotion
                    Move your pawn to the last row
                    Example: e8
                    then, you can choose a piece.

                Ambiguous move
                    If two or more same pieces can go to the same square,
                    specify which one by column or row.
                    Example: Rae1 (Rook on column a moves to e1)
                            N3f4 (Knight on row 3 moves to f4)
                            N5xf7 (Knight on row 5 captures on f4)

                SPECIAL COMMANDS:
                resign  : Give up the game
                draw    : Offer a draw to your opponent
                ===========================================
                Enter x to go back...
                ===========================================
                """);
        String understand = myScanner.next();
        while (!understand.equals("x")) {
            understand = myScanner.next();
        }
    }







//================================================================================================================
//================================================================================================================
//================================================================================================================
//================================================================================================================
//================================================================================================================
//==============================================Code End==========================================================
//================================================================================================================
//================================================================================================================
//================================================================================================================
//================================================================================================================
//================================================================================================================








//==================================Methods========================================

    //Chess Board drawing function
    public static void display(int[][] board, ArrayList<String> moveHistory){

        int start = Math.max(0, moveHistory.size() - 32);
        if (start % 2 != 0) start++;

        System.out.println  ("    a    b    c    d    e    f    g    h                |=======Game Log=======|");
        System.out.print    ("  -----------------------------------------             |     White   Black    |");

        for (int row = 0; row < 8; row ++){
            System.out.println(" ");
            System.out.print((8-row) + " |");
            for (int colunm = 0; colunm < 8; colunm ++){
                System.out.print(numToPiece(board[row][colunm]) + "|");
            }
            System.out.print(" " + (8-row));

            //Game Log print
            int whiteIndex = start + row * 4;
            int blackIndex = start + row * 4 + 1;
            int moveNum = (start / 2) + row * 2 + 1;
            if (whiteIndex < moveHistory.size()){
                String white = moveHistory.get(whiteIndex);
                String black = blackIndex < moveHistory.size() ? moveHistory.get(blackIndex) : "";
                System.out.printf("\t\t| %-4d%-8s%-9s|", moveNum, white, black);
            }
            System.out.println();

            System.out.print("  |----|----|----|----|----|----|----|----|");
            if (whiteIndex + 2 < moveHistory.size()){
                String white = moveHistory.get(whiteIndex + 2);
                String black = (blackIndex + 2) < moveHistory.size() ? moveHistory.get(blackIndex + 2) : "";
                System.out.printf("\t\t| %-4d%-8s%-9s|", moveNum + 1, white, black);
            }
        }
        System.out.print("\r");
        System.out.println("  -----------------------------------------");
        System.out.println("    a    b    c    d    e    f    g    h");
    }


    //Individual piece drawing function
    public static String numToPiece(int num){

        String piece = "    ";

        //Frame set
        if (num / 10 == 1){    //detect if its black or white piece
            piece = "= ==";
        }

        //Piece set
        if      (num % 10 == 1 || num == 1){         //Pawn
            piece = piece.substring(0, 1) + "o" + piece.substring(2);  //take first index, add "o", add index 2
        }
        else if (num % 10 == 2 || num == 2){    //Knight
            piece = piece.substring(0, 1) + "N" + piece.substring(2);  //same
        }
        else if (num % 10 == 3 || num == 3){    //Bishop
            piece = piece.substring(0, 1) + "B" + piece.substring(2);  //same
        }
        else if (num % 10 == 4 || num == 4){    //The ROOK
            piece = piece.substring(0, 1) + "R" + piece.substring(2);  //same
        }
        else if (num % 10 == 5 || num == 5){    //King
            piece = piece.substring(0, 1) + "K" + piece.substring(2);  //same
        }
        else if (num % 10 == 6 || num == 6){    //Queen
            piece = piece.substring(0, 1) + "Q" + piece.substring(2);  //same
        }

        //Return the piece
        return piece;

    }

    //change the column alphabet to number coordination function
    public static int alphaToNum(char alpha){
        if (alpha == 'a'){
            return 1;
        }
        else if (alpha == 'b'){
            return 2;
        }
        else if (alpha == 'c'){
            return 3;
        }
        else if (alpha == 'd'){
            return 4;
        }
        else if (alpha == 'e'){
            return 5;
        }
        else if (alpha == 'f'){
            return 6;
        }
        else if (alpha == 'g'){
            return 7;
        }
        else if (alpha == 'h'){
            return 8;
        }
        else{
            return 9; //out of chess board
        }
    }

    //change the piece input into numeric piece value
    public static int PieceToNum(char alpha){
        if (alpha == 'N'){
            return 2;
        }
        else if (alpha == 'B'){
            return 3;
        }
        else if (alpha == 'R'){
            return 4;
        }
        else if (alpha == 'K'){
            return 5;
        }
        else if (alpha == 'Q'){
            return 6;
        }
        else{
            return 7; //tis ain't a chess piece duh
        }
    }


    //clear terminal function
    public static void clearTerminal() {
        System.out.print("\033[H\033[2J\033[3J");     //tis wieerd ass thing is erase the whole thing in terminal and move the curser back to top
        System.out.flush();                             //so you can start printing stuff from the beginning.
    }

    public static boolean isThisAndThatEnemyPiece(int targetPiece, int yourPiece){
        //empti spacuhe
        if (targetPiece == 0){
            return false;
        }
        //get colour of target and you
        boolean targetIsWhite = targetPiece < 10;
        boolean youAreWhite = yourPiece < 10;

        //if both are different, its enemy so capture
        return targetIsWhite != youAreWhite;
    }

    public static boolean isThisSquareUnderAttack(int row, int col, boolean attackByWhite, int[][] board){
        int enemyPawn = 11;
        int enemyKnight = 12;
        int enemyBishop = 13;
        int enemyRook = 14;
        int enemyQueen = 16;
        int enemyKing = 15;
        int pawnDirection = -1;
        if (attackByWhite){
            pawnDirection = 1;
            enemyPawn -= 10;
            enemyKnight -= 10;
            enemyBishop -= 10;
            enemyRook -= 10;
            enemyQueen -= 10;
            enemyKing -= 10;
        }

        //pawnCHeck
        if (row + pawnDirection >= 0 && row + pawnDirection <= 7){
            if (col + 1 <= 7 && board[row + pawnDirection][col + 1] == enemyPawn) return true;
            if (col - 1 >= 0 && board[row + pawnDirection][col - 1] == enemyPawn) return true;
        }
        //KnightCheck
        int[] knightJumpVertical =   {+2, +2, +1, +1, -1, -1, -2, -2};
        int[] knightJumpHorizontal = {+1, -1, +2, -2, +2, -2, +1, -1};
        for (int i = 0; i < 8; i ++){
            if (((col + knightJumpHorizontal[i] >= 0) && (col + knightJumpHorizontal[i] <= 7)) && ((row + knightJumpVertical[i] >= 0) && (row + knightJumpVertical[i] <= 7))){
                if(board[row + knightJumpVertical[i]][col + knightJumpHorizontal[i]] == enemyKnight){
                    return true;
                }
            }
        }

        //Diagonal check (Bishop or Queen)
        int[] diagonalVertical = {+1, +1, -1, -1};
        int[] diagonalHorizontal = {+1, -1, -1, +1};
        for (int i = 0; i < 4; i ++){
            int magnitude = 1;
            //go all the way until it is out of board or hit a piece
            while (true){
                int tempRow = row + (diagonalVertical[i] * magnitude);
                int tempCol = col + (diagonalHorizontal[i] * magnitude);

                //if rowcols are outof bound
                if ((tempRow < 0 || tempRow > 7 || tempCol < 0 || tempCol > 7)){
                    break;
                }
                //if it detect a queen or bishop
                else if (board[tempRow][tempCol] == enemyBishop || board[tempRow][tempCol] == enemyQueen){
                    return true;
                }
                //if it hit other piece
                else if (board[tempRow][tempCol] != 0){
                    break;
                }
                magnitude += 1;
            }
        }
        //Straight check (Rook or Queen)
        int[] straightVertical = {1, 0, -1, 0};
        int[] straightHorizontal = {0, -1, 0, 1};
        for (int j = 0; j < 4; j ++){

            int magnitude = 1;
            //go all the way until it is out of board
            while (true){
                int tempRow = row + (straightVertical[j] * magnitude);
                int tempCol = col + (straightHorizontal[j] * magnitude);
                //if there is nothing, keep going
                if ((tempRow < 0 || tempRow > 7 || tempCol < 0 || tempCol > 7)){
                    break;
                }
                //if there is rook or queen, store in rook list.
                else if (board[tempRow][tempCol] == enemyRook || board[tempRow][tempCol] == enemyQueen){
                    return true;
                }
                else if (board[tempRow][tempCol] != 0){
                    break;
                }
                magnitude += 1;
            }
        }

        int[] kMoveVertical = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] kMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};
        for (int i = 0; i < 8; i ++){
            if (((col + kMoveHorizontal[i] >= 0) && (col + kMoveHorizontal[i] <= 7)) && ((row + kMoveVertical[i] >= 0) && (row + kMoveVertical[i] <= 7))){
                // Make sure to use 'board' here!
                if(board[row + kMoveVertical[i]][col + kMoveHorizontal[i]] == enemyKing){
                    return true;
                }
            }
        }

        return false;
    }


    //0 keep going
    //1 checkmate
    //2 stalemate
    public static int whatKindOfMate(boolean whiteToMove, int[][] board) {
        int myKing = 5;
        int kingRow = -1;
        int kingCol = -1;
        if (!whiteToMove) myKing = 15;

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){   
                if (board[i][j] == myKing) {
                    kingRow = i;
                    kingCol = j;
                }
            }   
        }
        int[] kingMoveVertical = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] kingMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};

        //check all direction near the king
        for (int i = 0; i < 8; i ++){
            //check if its inside the board 
            if ((kingCol + kingMoveHorizontal[i] >= 0) && (kingCol + kingMoveHorizontal[i] <= 7) && (kingRow + kingMoveVertical[i] >= 0) && (kingRow + kingMoveVertical[i] <= 7)){
                int target = board[kingRow + kingMoveVertical[i]][kingCol + kingMoveHorizontal[i]];
                //if its enemy piece or empty piece and not under attack
                if (target == 0 || isThisAndThatEnemyPiece(target, board[kingRow][kingCol])){
                    board[kingRow][kingCol] = 0;
                    if (!isThisSquareUnderAttack(kingRow + kingMoveVertical[i], kingCol + kingMoveHorizontal[i], !whiteToMove, board)){
                        board[kingRow][kingCol] = myKing;
                        return 0;
                    }
                    board[kingRow][kingCol] = myKing;
                }  
            }
        }
        //if theres no available move of king
        //see if other piece can block/capture
        //GodDaymn THisis HARD WORK RRAAA HOW tf I SHoulD CHEKK EVRY POSIBLL MOVE RRRRAAAAHHHHWWRRRRR
        for (int row = 0; row < 8; row ++){
            for (int col = 0; col < 8; col ++){

                int piece = board[row][col];
                //skip the empty space
                if (piece == 0) continue;
                boolean isWhitePiece = piece < 10;
                //skip the enemy piece
                if (whiteToMove != isWhitePiece) continue;
                //skip king
                if (piece == myKing) continue;

                //try to move the piece to every square
                for (int pieceMoveRow = 0; pieceMoveRow < 8; pieceMoveRow ++){
                    for (int pieceMoveCol = 0; pieceMoveCol < 8; pieceMoveCol ++){
                        //skip if its ally piece
                        int pieceCheck = board[pieceMoveRow][pieceMoveCol];
                        if (pieceCheck != 0 && !isThisAndThatEnemyPiece(pieceCheck, piece)) continue;

                        //if that square is in legal move square
                        if (isLegalMove(pieceMoveRow, pieceMoveCol, row, col, whiteToMove, piece, board)){
                            //move the piece on temp board
                            int[][] tempBoard = new int[8][8];
                            for (int i = 0; i < 8; i++) {
                                tempBoard[i] = Arrays.copyOf(board[i], 8);

                            }
                            tempBoard[pieceMoveRow][pieceMoveCol] = piece;
                            tempBoard[row][col] = 0;

                            //if king is not under attack, it means there is possible blocking move.
                            if (!isThisSquareUnderAttack(kingRow, kingCol, !whiteToMove, tempBoard)) return 0;

                        }
                    }
                }


            }
        }
        if (isThisSquareUnderAttack(kingRow, kingCol, !whiteToMove, board)){
            return 1;
        }
        else{
            return 2;
        }

    }   

    public static boolean isLegalMove(int arriveRow, int arriveCol, int startingRow, int startingCol, boolean whiteToMove, int piece, int[][] board){
        piece = piece % 10;

        //pawn
        //straight, start boost, capture
        if (piece == 1){
            int direction = 1;
            int startRow = 1;
            if (whiteToMove) {direction = -1; startRow = 6;}

            if (arriveCol == startingCol && arriveRow == startingRow + direction && board[arriveRow][arriveCol] == 0) return true;
            if (arriveCol == startingCol && startingRow == startRow && arriveRow == startingRow + direction * 2 && board[startingRow + direction][startingCol] == 0 && board[arriveRow][arriveCol] == 0)    return true;
            if (Math.abs(arriveCol - startingCol) == 1 && arriveRow == startingRow + direction && board[arriveRow][arriveCol] != 0)   return true;
            return false;
        }
        
        //knigth
        // high l or lower l shape
        if (piece == 2){
            int rowDiff = Math.abs(startingRow - arriveRow);
            int colDiff = Math.abs(startingCol - arriveCol);
            //if its l shape whatever
            if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))   return true;
            return false;
        }

        //Bishop
        //if absolute value of row and col are same (diagonal)
        if (piece == 3){
            if (Math.abs(startingRow - arriveRow) == Math.abs(startingCol - arriveCol) && (isPathClear(board, startingRow, startingCol, arriveCol, arriveRow))) return true;
            return false;
        }

        //Rook
        //if one of row or col is same(straight)
        if (piece == 4){
            if ((startingRow == arriveRow || startingCol == arriveCol) && isPathClear(board, startingRow, startingCol, arriveCol, arriveRow)) return true;
            return false;
        }

        //Queen
        //rook + bishop
        if (piece == 6){
            if (((startingRow == arriveRow || startingCol == arriveCol) || Math.abs(startingRow - arriveRow) == Math.abs(startingCol - arriveCol)) && isPathClear(board, startingRow, startingCol, arriveCol, arriveRow)) return true;
            return false;
        }
        return false;
    }

    public static boolean isPathClear(int[][] board, int startingRow, int startingCol, int arriveCol, int arriveRow){

        //set the unit vectors
        int rowVector = 0;
        int colVector = 0;
        if (startingRow < arriveRow) rowVector = 1;
        else if (startingRow > arriveRow) rowVector = -1;
        if (startingCol < arriveCol) colVector = 1;
        else if (startingCol > arriveCol) colVector = -1;

        //check in between blocks
        int row = startingRow + rowVector;
        int col = startingCol + colVector;
        while (row != arriveRow || col != arriveCol){
            if (board[row][col] != 0) return false;
            row += rowVector;
            col += colVector;
        }
        return true;
    }   

    //convert board piece number into gigantic strings like 1,2,3,4,0,0,0,0,1,2,3,00,0,0,,0,0,0 ykwim   
    public static String boardToString(int[][] board) {
        String result = "";
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                result += board[i][j] + ",";
        return result;
    }

    // go thrhough every positionhistory array and check if there are more than three same position
    public static boolean isThreefoldRepetition(ArrayList<String> positionHistory, int[][] board) {
        String current = boardToString(board);
        int count = 0;
        for (int i = 0; i < positionHistory.size(); i++){
            if (positionHistory.get(i).equals(current)) count++;
        }
        return count >= 3;
    }
    
    //Insufficient Material Draw detector
    //if K vs K
    //if KN vs K
    //if KB vs K
    //if KB vs KB same colour bishop
    //HOWDOI detect BISHIP COLOUR UURRAAA
    public static boolean isInsufficientMaterial(int[][] board){
        ArrayList<Integer> whitePieces = new ArrayList<>();
        ArrayList<Integer> blackPieces = new ArrayList<>();

        //colour 0=white 1=black 2=none
        int whiteBishopColor = 2;
        int blackBishipColour = 2;

        //set arrays
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int p = board[i][j];

                //if empty skip
                if (p == 0) continue;

                //if white piece add to white piece list
                if (p < 10) {
                    whitePieces.add(p);
                    if (p == 3) whiteBishopColor = getSquareColor(i, j);
                }
                //if black piece add to black piece list
                else {
                    blackPieces.add(p);
                    if (p == 13) blackBishipColour = getSquareColor(i, j);
                }
            }
        }
        //KK DRAW
        if (whitePieces.size() == 1 && blackPieces.size() == 1) return true;

        //KNK / KBK DRAW
        if (whitePieces.size() == 2 && blackPieces.size() == 1){
            if (whitePieces.contains(2) || whitePieces.contains(3)){
                return true;
            }
        }
        //KKN / KKB DRAW
        if (whitePieces.size() == 1 && blackPieces.size() == 2){
            if (blackPieces.contains(12) || blackPieces.contains(13)){
                return true;
            }
        }

        //KBKB SAME COLOUR DRAW
        if (whitePieces.size() == 2 && blackPieces.size() == 2){
            if (whitePieces.contains(3) && blackPieces.contains(13)){
                if (whiteBishopColor == blackBishipColour){
                    return true;
                }
            }
        }

        return false;

    }

    //detect if the square is dark or white 
    //0 = white
    //1 = black
    public static int getSquareColor(int row, int col) {
        if (row % 2 == 0) {
            if (col % 2 == 0) return 0;
            else return 1;
        }
        else {
            if (col % 2 == 0) return 1; 
            else return 0;
        }
    }

    /*
    Scribibible

        Chess Piece Skins??muhehe

        4x4 size i think i think ts is more fire

        Possible piece skins
        System.out.println("-----------------------------------------");        Game Log
        System.out.println("|=R]=|=N^=|=B)=|=Q*=|=K+=|=B)=|=N^=|=R]=|");    1.  e4  e5
        System.out.println("|----|----|----|----|----|----|----|----|");    2.  Nf3 Ne6
        System.out.println("|=oo=|=oo=|=oo=|=oo=|=oo=|=oo=|=oo=|=oo=|");    3
        System.out.println("|----|----|----|----|----|----|----|----|");    4
        System.out.println("|    |    |    |    |    |    |    |    |");    5
        System.out.println("|----|----|----|----|----|----|----|----|");    6
        System.out.println("|    |    |    |    |    |    |    |    |");    7
        System.out.println("|----|----|----|----|----|----|----|----|");    8
        System.out.println("|    |    |    |    |    |    |    |    |");    9
        System.out.println("|----|----|----|----|----|----|----|----|");    10
        System.out.println("|    |    |    |    |    |    |    |    |");    11
        System.out.println("|----|----|----|----|----|----|----|----|");    12
        System.out.println("| <> | <> | <> | <> | <> | <> | <> | <> |");    13
        System.out.println("|----|----|----|----|----|----|----|----|");    14
        System.out.println("| [] | ^{ | () | WW | #] | () | ^{ | [] |");    15
        System.out.println("-----------------------------------------");    16



        ====THINGS TO ADD====
        Help screen
        ===========================================
                  HELP
        ===========================================
        PIECE NAMES:
        K = King   Q = Queen   R = Rook
        B = Bishop N = Knight  Pawn (no letter)

        HOW TO INPUT YOUR MOVE:

        Pawn move
            Type the column and row you want to move to.
            Example: e4 (move pawn to e4)

        Piece move
            Type the piece letter + destination.
            Example: Nf3 (move Knight to f3)

        Capture
            You can type with or without 'x'.
            Example: Nxe5 or Ne5 (both work)

        Pawn capture
            Type your pawn's column + x + destination.
            Example: exd5 (pawn on e captures on d5)

        Castling
            0-0 or O-O      : Castle kingside (short castle)
            0-0-0 or O-O-O  : Castle queenside (long castle)

        En Passant
            Type one square behind the pawn you want to capture.
            If enemy pawn on d7 moves to d5 and you have pawn on e5,
            Example: exd6

        Promotion
            Move your pawn to the last row
            Example: e8
            then, you can choose a piece.

        Ambiguous move
            If two or more same pieces can go to the same square,
            specify which one by column or row.
            Example: Rae1 (Rook on column a moves to e1)
                     N3f4 (Knight on row 3 moves to f4)
                     N5xf7 (Knight on row 5 captures on f4)

        SPECIAL COMMANDS:
        resign  : Give up the game
        draw    : Offer a draw to your opponent
        ===========================================
        Press Enter to go back...
        ===========================================

    */
}