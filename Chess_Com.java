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
                //Chess board piece
                //=======INITIAL POSITION===========
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

                //========CUSTOM POSITION============
                // int[][] ChessBoard = {
                //     {15,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,2 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,0 ,11,0 ,0 ,0 },
                //     {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,2 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                //     {0 ,0 ,0 ,5 ,0 ,0 ,0 ,0 }
                // };

                //empty board copy and paste
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 },
                    // {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 }

                Boolean whiteToMove = true;

                //enPassantAble
                int enPassantRow = 0;
                int enPassantColunm = 0;
                int enPassantMoveCount = 0;
                boolean enPassantAble = false;

                // 0-0 / 0-0-0Able
                boolean whiteShortCastle = true;
                boolean whiteLongCastle = true;
                boolean blackShortCastle = true;
                boolean blackLongCastle = true;

                //50 move draw
                int fiftyMoveDrawCount = 0;

                //resign
                boolean isWhiteResigned = false;
                boolean isBlackResigned = false;
                //draw
                boolean drawOffer = false;
                boolean drawAccepted = false;

                //History
                ArrayList<String> positionHistory = new ArrayList<>();  
                ArrayList<String> moveHistory = new ArrayList<>();

                






                //=========================Gameplay===========================
                //repeat until one user wins OR 50 move draw rule OR not Threefold REpetitiomn OR not insufficientMaterial OR resign
                while (whatKindOfMate(whiteToMove, ChessBoard) == 0 && fiftyMoveDrawCount < 100 && !isThreefoldRepetition(positionHistory, ChessBoard) && !isInsufficientMaterial(ChessBoard) && !isWhiteResigned && !isBlackResigned && !drawAccepted){

                    String move = "";
                    int piece = 0;
                    int row = 0;
                    int colunm = 0;
                    boolean validMove = false;
                    boolean alreadyWrong = false;
                    boolean ambiguousMove = false;
                    boolean inCheck = false;

                    
                    // repeat until user input the correct move notation
                    while (!validMove){


                        // snapshot of the board to check if its in check
                        int[][] backupBoard = new int[8][8];
                        for (int i = 0; i < 8; i++) {
                            backupBoard[i] = Arrays.copyOf(ChessBoard[i], 8);
                        }


                        //draw ChessBoard
                        clearTerminal();
                        display(ChessBoard, moveHistory);
                        System.out.println("______________________________________");

                        // if user have inputed an invalid notation, print this
                        if (ambiguousMove){
                            System.out.println("ERROR: Ambiguous move, please specify the piece. (eg: Rae5 / Nbd2 / Nf3d4)");
                        }
                        else if (inCheck){
                            System.out.println("ERROR: You are still in check");
                        }
                        else if (alreadyWrong){
                            System.out.println("ERROR: Illegal move, enter again.");
                        }

                        //enter a move
                        if (drawOffer){
                            System.out.println("Draw offered (y / n): ");
                            String answer = myScanner.next();
                            if (answer.equals("y")){
                                drawAccepted = true;
                                validMove = true;
                            }
                            else{
                                drawOffer = false;
                                move = "";
                                continue;
                            }
                        }
                        else if (whiteToMove){
                            System.out.println("White to Move");
                            System.out.print("Enter a move: ");
                            move = myScanner.next();
                        }
                        else{
                            System.out.println("Black to Move");
                            System.out.print("Enter a move: ");
                            move = myScanner.next();
                        }


                        if (enPassantMoveCount >= 1){
                            enPassantAble = true;
                            enPassantMoveCount = 0;
                        }
                        else{
                            enPassantAble = false;
                            enPassantRow = -1;
                            enPassantColunm = -1;
                        }
                        //resign and draw

                        if (move.equals("resign") || move.equals("Resign")){
                            if (whiteToMove) isWhiteResigned = true;
                            else isBlackResigned = true;
                            validMove = true;
                        }
                        else if (move.equals("draw") || move.equals("Draw")){
                            drawOffer = true;
                            continue;
                        }


                        //actually moves
                        //======================================Pawn moves BOOOM===========================================
                        //only possible for 2 character long is pawn move, like e4, d5, a6, etc...
                        else if (move.length() == 2){

                            //store move info
                            colunm = alphaToNum(move.charAt(0)) - 1;
                            row = 8 - Character.getNumericValue(move.charAt(1));

                            //if arrive block is empty, the move is inside the chess board
                            if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && ChessBoard[row][colunm] == 0){

                                //check which pawn to move
                                int toAdd = 0;
                                if (whiteToMove) toAdd = 1;
                                if (!whiteToMove) toAdd = -1;

                                //whosePiece is used to determine if its white or black's piece.
                                int whosePiece = 1;
                                if (!whiteToMove) whosePiece += 10;

                                //pawn forward 1 block: if pawn is right behind this block and there is no piece in front, this works
                                if ((row + toAdd >= 0) && (row + toAdd < 8) && (ChessBoard[row + toAdd][colunm] == whosePiece)){
                                    validMove = true;
                                    alreadyWrong = false;
                                    ChessBoard[row + toAdd][colunm] = 0;
                                    ChessBoard[row][colunm] = whosePiece;
                                }

                                //pawn forward 2 block (starter b   oost): if pawn is in starting position, and there is no piece blocking the way, this works
                                else if ((row + toAdd*2 >= 0) && (row + toAdd*2 < 8) && (ChessBoard[row + (toAdd*2)][colunm] == whosePiece) && (ChessBoard[row + toAdd][colunm] == 0) && ((row + (toAdd * 2) == 1) || (row + (toAdd * 2) == 6))){
                                    validMove = true;
                                    alreadyWrong = false;
                                    ChessBoard[row + (toAdd*2)][colunm] = 0;
                                    ChessBoard[row][colunm] = whosePiece;
                                    enPassantMoveCount += 1;
                                    enPassantRow = row + toAdd;
                                    enPassantColunm = colunm;
                                }
                                else{  //invalid input
                                    validMove = false;
                                    alreadyWrong = true;
                                }

                            }
                            else{ //invalid input
                                validMove = false;
                                alreadyWrong = true;
                            }
                        }

                        //===================================ShortCastling(0-0)===============================================
                        else if((move.equals("0-0") || move.equals("O-O"))){
                            int whoseKing = 5;
                            int whoseRook = 4;
                            int tempRow = 7;
                            if(!whiteToMove){
                                tempRow = 0;
                                whoseRook += 10;
                                whoseKing += 10;
                            }
                            if (!(ChessBoard[tempRow][7] == whoseRook)){
                                if (whiteToMove){
                                    whiteShortCastle = false;
                                }
                                else{
                                    blackShortCastle = false;
                                }
                                validMove = false;
                                alreadyWrong = true;
                            }
                            //if all the way to castel is all safe square and empty
                            else if ((ChessBoard[tempRow][5] == 0 && ChessBoard[tempRow][6] == 0 && !isThisSquareUnderAttack(tempRow, 4, !whiteToMove, ChessBoard) && !isThisSquareUnderAttack(tempRow, 5, !whiteToMove, ChessBoard) && !isThisSquareUnderAttack(tempRow, 6, !whiteToMove, ChessBoard)) && ((whiteToMove && whiteShortCastle) || (!whiteToMove && blackShortCastle))){
                                ChessBoard[tempRow][4] = 0;
                                ChessBoard[tempRow][5] = whoseRook;
                                ChessBoard[tempRow][6] = whoseKing;
                                ChessBoard[tempRow][7] = 0;
                                validMove = true;
                                alreadyWrong = false;
                                if (whiteToMove){
                                    whiteShortCastle = false;
                                    whiteLongCastle = false;
                                }
                                else{
                                    blackShortCastle = false;
                                    blackLongCastle = false;
                                }
                            }
                            else{
                                validMove = false;
                                alreadyWrong = true;
                            }
                        }
                        //==========================================LongCastling(0-0-0)=============================================
                        else if((move.equals("0-0-0") || move.equals("O-O-O"))){
                            int whoseKing = 5;
                            int whoseRook = 4;
                            int tempRow = 7;
                            if(!whiteToMove){ 
                                tempRow = 0;
                                whoseRook += 10;
                                whoseKing += 10;
                            }

                            if (!(ChessBoard[tempRow][0] == whoseRook)){
                                if (whiteToMove){
                                    whiteLongCastle = false;
                                }
                                else{
                                    blackLongCastle = false;
                                }
                                validMove = false;
                                alreadyWrong = true;
                            }
                            //if all the way to castel is all safe square and empty
                            else if ((ChessBoard[tempRow][3] == 0 && ChessBoard[tempRow][2] == 0 && ChessBoard[tempRow][1] == 0 && !isThisSquareUnderAttack(tempRow, 4, !whiteToMove, ChessBoard) && !isThisSquareUnderAttack(tempRow, 3, !whiteToMove, ChessBoard) && !isThisSquareUnderAttack(tempRow, 2, !whiteToMove, ChessBoard)) && ((whiteToMove && whiteLongCastle) || (!whiteToMove && blackLongCastle))){
                                ChessBoard[tempRow][4] = 0;
                                ChessBoard[tempRow][3] = whoseRook;
                                ChessBoard[tempRow][2] = whoseKing;
                                ChessBoard[tempRow][0] = 0;
                                validMove = true;
                                alreadyWrong = false;
                                if (whiteToMove){
                                    whiteShortCastle = false;
                                    whiteLongCastle = false;
                                }
                                else{
                                    blackShortCastle = false;
                                    blackLongCastle = false;
                                }
                            }
                            else{
                                validMove = false;
                                alreadyWrong = true;
                            }
                        }
                        



                        //=========================Minor/Major piece move==========================
                        //=============================Takes=Takes=Takes=Takes=Takes=Here=There=Takes=Takes=yea im winning -Hikaru-=============================
                        
                        //2026-02-23[Update]: i combined Movelength 4 here so i dont have to copy and paste the gigantic piece moving thing in movelength 4.
                        //                    i will think about ambiguous moves later uugh
                        //2026-02-24[Update]: i will change the else if statement right below it and add som if statement for Ambiguous moves
                        else if(move.length() == 3 || move.length() == 4 && Character.isUpperCase(move.charAt(0)) || move.length() == 5 && Character.isUpperCase(move.charAt(0))){
                            
                            boolean takes = false;
                            char specify = ' ';
                            boolean successfulMove = true;

                            //length 3 only possible like Nf3, Qe4, Bb3, etc.
                            if (move.length() == 3){
                                piece = PieceToNum(move.charAt(0));
                                colunm = alphaToNum(move.charAt(1)) - 1;
                                row = 8 - Character.getNumericValue(move.charAt(2));
                            }
                            // Piece Takes Piece (eg. Nxe5, Qxg7) / Pawn takes piece (eg. exd5, axb3)+ EN PASSANT / Ambiguous moves (eg. Rad1, R2e7) how tf i mak tis hell naw
                            //here i only considered Piece takes Piece.
                            else if (move.length() == 4 && move.charAt(1) == 'x' && (move.charAt(2) >= 'a' && move.charAt(2) <= 'h') && (move.charAt(3) >= '1' && move.charAt(3) <= '8')){
                                piece = PieceToNum(move.charAt(0));
                                colunm = alphaToNum(move.charAt(2)) - 1;
                                row = 8 - Character.getNumericValue(move.charAt(3));
                                takes = true;
                            }
                            //if 4 digit and second is among abcdefgh or 12345678 then its ambiguoussie movvie muhehe
                // explain below    |-----4digit------| AND |------------------if 2nd is among abcdefgh-----------------| OR |-------------------if 2nd is among 12345678-----------------| AND |--------------------------------if the 3rd letter is abcdefgh and 4nd is 12345678--------------------------------------|
                            else if (move.length() == 4 && ((move.charAt(1) >= 'a' && move.charAt(1) <= 'h') || (move.charAt(1) >= '1' && move.charAt(1) <= '8')) && (move.charAt(2) >= 'a' && move.charAt(2) <= 'h') && (move.charAt(3) >= '1' && move.charAt(3) <= '8')){
                                piece = PieceToNum(move.charAt(0));
                                colunm = alphaToNum(move.charAt(2)) - 1;
                                row = 8 - Character.getNumericValue(move.charAt(3));
                                specify = move.charAt(1);
                            }
                            else if (move.length() == 5 && move.charAt(2) == 'x' && ((move.charAt(1) >= 'a' && move.charAt(1) <= 'h') || (move.charAt(1) >= '1' && move.charAt(1) <= '8')) && (move.charAt(3) >= 'a' && move.charAt(3) <= 'h') && (move.charAt(4) >= '1' && move.charAt(4) <= '8')){
                                piece = PieceToNum(move.charAt(0));
                                colunm = alphaToNum(move.charAt(3)) - 1;
                                row = 8 - Character.getNumericValue(move.charAt(4));
                                specify = move.charAt(1);
                                takes = true;
                            }
                            else{
                                successfulMove = false;
                            }
                            //if its a capturing move, and there is nothing to capture, invalid move
                            if (takes && ChessBoard[row][colunm] == 0){
                                alreadyWrong = true;
                                validMove = false;
                                successfulMove = false;
                            }

                            //below, there will be each piece code, in order of Knight-> Bishop-> Rook-> King-> Queen.
                            if (successfulMove){

                                //determine white or black's piece
                                int whosePiece = piece;
                                if (!whiteToMove) whosePiece += 10;

                                //================================The knight==================================
                                if (piece == 2){
                                    
                                    //if arrive is inside the board, and its empty place or enemy piece
                                    if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && (ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(ChessBoard[row][colunm], whosePiece))){
                                        
                                        // if you add the same index of verti and hori from certain location, you will get every possible knight move.
                                        // index: (0=NE, 1=NW, 2=EN, 3=WN, 4=ES, 5=WS, 6=SE, 7=SW) top to bot zigzag
                                        int[] knightJumpVertical =   {+2, +2, +1, +1, -1, -1, -2, -2};
                                        int[] knightJumpHorizontal = {+1, -1, +2, -2, +2, -2, +1, -1};

                                        //used when there is 1 or more knight can move to this block
                                        int knightCount = 0;
                                        ArrayList<Integer> knightColumn = new ArrayList<>();
                                        ArrayList<Integer> knightRow = new ArrayList<>();

                                        // check every possible knight location from the input block
                                        for (int i = 0; i < 8; i ++){
                                            
                                            //check if the knight move is not out of bound(prevent error)
                                            if (((colunm + knightJumpHorizontal[i] >= 0) && (colunm + knightJumpHorizontal[i] <= 7)) && ((row + knightJumpVertical[i] >= 0) && (row + knightJumpVertical[i] <= 7))){

                                                //check if there is a knight
                                                if(ChessBoard[row + knightJumpVertical[i]][colunm + knightJumpHorizontal[i]] == whosePiece){
                                                    knightCount += 1;
                                                    //store the knight's coordinate
                                                    knightColumn.add(colunm + knightJumpHorizontal[i]);
                                                    knightRow.add(row + knightJumpVertical[i]);
                                                }
                                            }
                                        }

                                        //if there is an available knight
                                        if (knightCount >= 1){
                                            if (knightCount == 1){
                                                validMove = true;
                                                alreadyWrong = false;
                                                ChessBoard[knightRow.get(0)][knightColumn.get(0)] = 0;
                                                ChessBoard[row][colunm] = whosePiece;
                                            }
                                            else if (specify != ' '){ //if there are more than 1 knight available AMBIGUOUS move
                                                //temporary variables
                                                int countHasToBe1 = 0;
                                                int kCol = 0;
                                                int kRow = 0;
                                                //if the ambiguous input is telling about column
                                                if (specify >= 'a' && specify <= 'h'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < knightCount; i ++){
                                                        if (knightColumn.get(i) == alphaToNum(specify) - 1){
                                                            kCol = knightColumn.get(i);
                                                            kRow = knightRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece on that column
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[kRow][kCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                // if the ambiguous input is telling about row
                                                else if (specify >= '1' && specify <= '8'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < knightCount; i ++){
                                                        if (knightRow.get(i) == (8 - Character.getNumericValue(specify))){
                                                            kCol = knightColumn.get(i);
                                                            kRow = knightRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece in that row
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[kRow][kCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                else{
                                                    validMove = false;
                                                    alreadyWrong = true;
                                                }
                                            }
                                            else{  //if input didnt specify ambiguous then its invalid move 
                                                validMove = false;
                                                ambiguousMove = true;
                                            }
                                        }
                                        else{  //if there is no knight its invalid
                                            validMove = false;
                                            alreadyWrong = true;
                                        }

                                    }
                                    else{   //invalid move
                                        validMove = false;
                                        alreadyWrong = true;
                                    }

                                }

                                //============================The Bishop================================
                                else if (piece == 3){
                                    
                                    //if arrive is empty, and inside the board
                                    if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && (ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(ChessBoard[row][colunm], whosePiece))){

                                        //add the same index to get diagonal movement, multiply by an integer to get long movement.
                                        //index: (0 = NE, 1 = NW, 2 = SW, 3 = SE) counterclockwise
                                        int[] bishopMoveVertical = {+1, +1, -1, -1};
                                        int[] bishopMoveHorizontal = {+1, -1, -1, +1};

                                        int bishopCount = 0;
                                        ArrayList<Integer> bishopColumn = new ArrayList<>();
                                        ArrayList<Integer> bishopRow = new ArrayList<>();

                                        // get the maximum possible range on each direction.
                                        for (int i = 0; i < 4; i ++){

                                            int magnitude = 1;
                                            //go all the way until it is out of board or hit a piece
                                            while ((row + (bishopMoveVertical[i] * magnitude) >= 0) && (colunm + (bishopMoveHorizontal[i] * magnitude) >= 0) && (row + (bishopMoveVertical[i] * magnitude) <= 7) && (colunm + (bishopMoveHorizontal[i] * magnitude) <= 7)){
                                                //if there is bishop, store in bishop.
                                                if (ChessBoard[row + bishopMoveVertical[i] * magnitude][colunm + (bishopMoveHorizontal[i] * magnitude)] == whosePiece){
                                                    bishopCount += 1;
                                                    bishopColumn.add(colunm + (bishopMoveHorizontal[i] * magnitude));
                                                    bishopRow.add(row + (bishopMoveVertical[i] * magnitude));
                                                    break;
                                                }
                                                //if there is nothing, keep going
                                                else if (ChessBoard[row + bishopMoveVertical[i] * magnitude][colunm + (bishopMoveHorizontal[i] * magnitude)] == 0){
                                                    magnitude += 1;
                                                }
                                                else{ //if there is piece, bishop cant go so break
                                                    break;
                                                }
                                            }

                                        }
                                        if (bishopCount >= 1){
                                            if (bishopCount == 1){
                                                validMove = true;
                                                alreadyWrong = false;
                                                ChessBoard[bishopRow.get(0)][bishopColumn.get(0)] = 0;
                                                ChessBoard[row][colunm] = whosePiece;
                                            }
                                            else if (specify != ' '){ //if there are more than 1 bishop available AMBIGUOUS move
                                                //temporary variables
                                                int countHasToBe1 = 0;
                                                int bCol = 0;
                                                int bRow = 0;
                                                //if the ambiguous input is telling about column
                                                if (specify >= 'a' && specify <= 'h'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < bishopCount; i ++){
                                                        if (bishopColumn.get(i) == alphaToNum(specify) - 1){
                                                            bCol = bishopColumn.get(i);
                                                            bRow = bishopRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece on that column
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[bRow][bCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                // if the ambiguous input is telling about row
                                                else if (specify >= '1' && specify <= '8'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < bishopCount; i ++){
                                                        if (bishopRow.get(i) == (8 - Character.getNumericValue(specify))){
                                                            bCol = bishopColumn.get(i);
                                                            bRow = bishopRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece in that row
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[bRow][bCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                else{
                                                    validMove = false;
                                                    alreadyWrong = true;
                                                }
                                            }
                                            else{
                                                validMove = false;
                                                ambiguousMove = true;
                                            }
                                        }
                                        else{
                                            validMove = false;
                                            alreadyWrong = true;
                                        }

                                    }
                                    else{
                                        validMove = false;
                                        alreadyWrong = true;
                                    }
                                }
                                //===============================THE ROOOOOOOOOK================================
                                else if (piece == 4){

                                    //if arrive is empty, and inside the board
                                    if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && (ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(ChessBoard[row][colunm], whosePiece))){

                                        //add the same index to get straight movement, multiply by an integer to get long movement.
                                        //index: (0 = N, 1 = W, 2 = S, 3 = E) counterclockwise
                                        int[] rookMoveVertical = {1, 0, -1, 0};
                                        int[] rookMoveHorizontal = {0, -1, 0, 1};

                                        int rookCount = 0;
                                        ArrayList<Integer> rookColumn = new ArrayList<>();
                                        ArrayList<Integer> rookRow = new ArrayList<>();

                                        for (int i = 0; i < 4; i ++){

                                            int magnitude = 1;
                                            //go all the way until it is out of board
                                            while ((row + (rookMoveVertical[i] * magnitude) >= 0) && (colunm + (rookMoveHorizontal[i] * magnitude) >= 0) && (row + (rookMoveVertical[i] * magnitude) <= 7) && (colunm + (rookMoveHorizontal[i] * magnitude) <= 7)){
                                                //if there is rook, store in rook list.
                                                if (ChessBoard[row + rookMoveVertical[i] * magnitude][colunm + (rookMoveHorizontal[i] * magnitude)] == whosePiece){
                                                    rookCount += 1;
                                                    rookColumn.add(colunm + (rookMoveHorizontal[i] * magnitude));
                                                    rookRow.add(row + (rookMoveVertical[i] * magnitude));
                                                    break;
                                                }
                                                //if there is nothing, keep going
                                                else if (ChessBoard[row + rookMoveVertical[i] * magnitude][colunm + (rookMoveHorizontal[i] * magnitude)] == 0){
                                                    magnitude += 1;
                                                }
                                                else{ // rook cant go so break
                                                    break;
                                                }
                                            }
                                        }
                                        if (rookCount >= 1){
                                            if (rookCount == 1){
                                                validMove = true;
                                                alreadyWrong = false;
                                                ChessBoard[rookRow.get(0)][rookColumn.get(0)] = 0;
                                                ChessBoard[row][colunm] = whosePiece;

                                                //disable casteling
                                                if (whiteToMove){
                                                    if (rookColumn.get(0) == 7){
                                                        whiteShortCastle = false;
                                                    }  
                                                    else if (rookColumn.get(0) == 0){
                                                        whiteLongCastle = false;
                                                    }
                                                }
                                                else{
                                                    if (rookColumn.get(0) == 7){
                                                        blackShortCastle = false;
                                                    }  
                                                    else if (rookColumn.get(0) == 0){
                                                        blackLongCastle = false;
                                                    }
                                                }
                                            }
                                            else if (specify != ' '){ //if there are more than 1 rook available AMBIGUOUS move
                                                //temporary variables
                                                int countHasToBe1 = 0;
                                                int rCol = 0;
                                                int rRow = 0;
                                                //if the ambiguous input is telling about column
                                                if (specify >= 'a' && specify <= 'h'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < rookCount; i ++){
                                                        if ( rookColumn.get(i) == alphaToNum(specify) - 1){
                                                            rCol =  rookColumn.get(i);
                                                            rRow = rookRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece on that column
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[rRow][rCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;

                                                        //disable casteling
                                                        if (whiteToMove){
                                                            if (rCol == 7){
                                                                whiteShortCastle = false;
                                                            }  
                                                            else if (rCol == 0){
                                                                whiteLongCastle = false;
                                                            }
                                                        }
                                                        else{
                                                            if (rCol == 7){
                                                                blackShortCastle = false;
                                                            }  
                                                            else if (rCol == 0){
                                                                blackLongCastle = false;
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                // if the ambiguous input is telling about row
                                                else if (specify >= '1' && specify <= '8'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < rookCount; i ++){
                                                        if (rookRow.get(i) == (8 - Character.getNumericValue(specify))){
                                                            rCol =  rookColumn.get(i);
                                                            rRow = rookRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece in that row
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[rRow][rCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;

                                                        //disable casteling
                                                        if (whiteToMove){
                                                            if (rCol == 7){
                                                                whiteShortCastle = false;
                                                            }  
                                                            else if (rCol == 0){
                                                                whiteLongCastle = false;
                                                            }
                                                        }
                                                        else{
                                                            if (rCol == 7){
                                                                blackShortCastle = false;
                                                            }  
                                                            else if (rCol == 0){
                                                                blackLongCastle = false;
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                else{
                                                    validMove = false;
                                                    alreadyWrong = true;
                                                }
                                            }
                                            else{
                                                validMove = false;
                                                ambiguousMove = true;
                                            }
                                        }
                                        else{
                                            validMove = false;
                                            alreadyWrong = true;
                                        }
                                    }
                                    else{
                                        validMove = false;
                                        alreadyWrong = true;
                                    }
                                }

                                //================================The King====================================
                                else if (piece == 5){

                                    //if arrive is empty, and inside the board
                                    if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && (ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(ChessBoard[row][colunm], whosePiece))){

                                        //add the same index to get straight movement, multiply by an integer to get long movement.
                                        //index: (N-NW-W-SW-S-SE-E-NE) counterclockwise
                                        int[] kingMoveVertical = {1, 1, 0, -1, -1, -1, 0, 1};
                                        int[] kingMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};
                                        boolean isKingMoved = false;
                                        // dont need kingcount since there is only 1 king on game

                                        //check all direction
                                        for (int i = 0; i < 8; i ++){

                                            if ((colunm + kingMoveHorizontal[i] >= 0) && (colunm + kingMoveHorizontal[i] <= 7) && (row + kingMoveVertical[i] >= 0) && (row + kingMoveVertical[i] <= 7)){
                                                
                                                //if there is a king, move the king and break the loop since theres only 1 king
                                                if(ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] == whosePiece){
                                                    ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] = 0;

                                                    if (isThisSquareUnderAttack(row, colunm, !whiteToMove, ChessBoard)){
                                                        ChessBoard[row + kingMoveVertical[i]][colunm + kingMoveHorizontal[i]] = whosePiece;
                                                        break;
                                                    }
                                                    else{
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        isKingMoved = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (isKingMoved){
                                            validMove = true;
                                            alreadyWrong = false;
                                            if (whiteToMove){
                                                whiteLongCastle = false;
                                                whiteShortCastle = false;
                                            }
                                            else{
                                                blackLongCastle = false;
                                                blackShortCastle = false;
                                            }
                                        }
                                        else{
                                            validMove = false;
                                            alreadyWrong = true;
                                        }
                                    }
                                    else{
                                        validMove = false;
                                        alreadyWrong = true;
                                    }

                                }

                                //==================================The Queen==============================
                                else if (piece == 6){

                                    //if arrive is empty, and inside the board
                                    if (row >= 0 && row < 8 && colunm >= 0 && colunm < 8 && (ChessBoard[row][colunm] == 0 || isThisAndThatEnemyPiece(ChessBoard[row][colunm], whosePiece))){

                                        //add the same index to get straight movement, multiply by an integer to get long movement.
                                        //index: (N-NW-W-SW-S-SE-E-NE) counterclockwis Same as king
                                        int[] queenMoveVertical = {1, 1, 0, -1, -1, -1, 0, 1};
                                        int[] queenMoveHorizontal = {0, -1, -1, -1, 0, 1, 1, 1};

                                        int queenCount = 0;
                                        ArrayList<Integer> queenColumn = new ArrayList<>();
                                        ArrayList<Integer> queenRow = new ArrayList<>();

                                        //queen has 8 direction so check all 8 direction
                                        for (int i = 0; i < 8; i ++){

                                            int magnitude = 1;
                                            //go all the way until it is out of board or hit a piece
                                            while ((row + (queenMoveVertical[i] * magnitude) >= 0) && (colunm + (queenMoveHorizontal[i] * magnitude) >= 0) && (row + (queenMoveVertical[i] * magnitude) <= 7) && (colunm + (queenMoveHorizontal[i] * magnitude) <= 7)){
                                                //if there is queen, store the coordination in queen list.
                                                if (ChessBoard[row + queenMoveVertical[i] * magnitude][colunm + (queenMoveHorizontal[i] * magnitude)] == whosePiece){
                                                    queenCount += 1;
                                                    queenColumn.add(colunm + (queenMoveHorizontal[i] * magnitude));
                                                    queenRow.add(row + (queenMoveVertical[i] * magnitude));
                                                    break;
                                                }
                                                //if there is nothing, keep going
                                                else if (ChessBoard[row + queenMoveVertical[i] * magnitude][colunm + (queenMoveHorizontal[i] * magnitude)] == 0){
                                                    magnitude += 1;
                                                }
                                                else{ //if there is piece, queen cant go over it so break
                                                    break;
                                                }
                                            }
                                        }
                                        if (queenCount >= 1){
                                            if (queenCount == 1){
                                                validMove = true;
                                                alreadyWrong = false;
                                                ChessBoard[queenRow.get(0)][queenColumn.get(0)] = 0;
                                                ChessBoard[row][colunm] = whosePiece;
                                            }
                                            else if (specify != ' '){ //if there are more than 1 queen available AMBIGUOUS move
                                                //temporary variables
                                                int countHasToBe1 = 0;
                                                int qCol = 0;
                                                int qRow = 0;
                                                //if the ambiguous input is telling about column
                                                if (specify >= 'a' && specify <= 'h'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < queenCount; i ++){
                                                        if ( queenColumn.get(i) == alphaToNum(specify) - 1){
                                                            qCol =  queenColumn.get(i);
                                                            qRow = queenRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece on that column
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[qRow][qCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                // if the ambiguous input is telling about row
                                                else if (specify >= '1' && specify <= '8'){
                                                    //check all the ambiguous lists
                                                    for (int i = 0; i < queenCount; i ++){
                                                        if (queenRow.get(i) == (8 - Character.getNumericValue(specify))){
                                                            qCol =  queenColumn.get(i);
                                                            qRow = queenRow.get(i);
                                                            countHasToBe1 += 1;
                                                        }
                                                    }
                                                    //if there is only one piece in that row
                                                    if (countHasToBe1 == 1){
                                                        ChessBoard[qRow][qCol] = 0;
                                                        ChessBoard[row][colunm] = whosePiece;
                                                        validMove = true;
                                                        alreadyWrong = false;
                                                    }
                                                    else{
                                                        validMove = false;
                                                        ambiguousMove = true;
                                                    }
                                                }
                                                else{
                                                    validMove = false;
                                                    alreadyWrong = true;
                                                }
                                            }
                                            else{
                                                validMove = false;
                                                ambiguousMove = true;
                                            }
                                        }
                                        else{
                                            validMove = false;
                                            alreadyWrong = true;
                                        }
                                    }
                                    else{
                                        validMove = false;
                                        alreadyWrong = true;
                                    }

                                }
                                // ###NeedToADD###### Casteling like if input has 0 inside it do the casteling

                                else{ //idk what u input but you moved something else outside the chessboard like how?
                                    validMove = false;
                                    alreadyWrong = true;
                                }

                            }
                            else{
                                validMove = false;
                                alreadyWrong = true;
                            }
                            
                        }

                        //==========================================Pawn Takes========================================
                        //if first is lowercase(a~f) and second is 'x' capture, and 4 length long, axaa kind of comb woudl possibl
                        //i wil filture the wrong ones inside this else if thingie
                        else if (move.length() == 4 && Character.isLowerCase(move.charAt(0)) && move.charAt(1) == 'x'){

                            //variable set
                            //if da last coordinate thingie is inside chessboard
                            if (move.length() == 4 && (move.charAt(2) >= 'a' && move.charAt(2) <= 'h') && (move.charAt(3) >= '1' && move.charAt(3) <= '8') && move.charAt(0) >= 'a' && move.charAt(0) <= 'h'){
                                int pawn = alphaToNum(move.charAt(0)) - 1;
                                colunm = alphaToNum(move.charAt(2)) - 1;
                                row = 8 - Character.getNumericValue(move.charAt(3));

                                //whose pawn is this
                                int whosePiece = 1;
                                if (!whiteToMove) whosePiece += 10;

                                //pawn direction setting
                                int toAdd = 0;
                                if (whiteToMove) toAdd = 1;
                                if (!whiteToMove) toAdd = -1;

                                //if target is in 1 diagonal of pawn, and there is a piece on target, capture
                                if ((row + toAdd >= 0) && (row + toAdd < 8) && (ChessBoard[row][colunm] != 0) && (ChessBoard[row + toAdd][pawn] == whosePiece) && (colunm + 1 == pawn || colunm - 1 == pawn)){
                                    ChessBoard[row][colunm] = whosePiece;
                                    ChessBoard[row + toAdd][pawn] = 0;
                                    validMove = true;
                                    alreadyWrong = false;
                                }
                                //ENPASSAANANNTTHHH
                                else if (enPassantAble && row == enPassantRow && colunm == enPassantColunm && (ChessBoard[row + toAdd][pawn] == whosePiece) && (colunm + 1 == pawn || colunm - 1 == pawn) && (ChessBoard[row][colunm] == 0)){
                                    ChessBoard[row][colunm] = whosePiece;
                                    ChessBoard[row + toAdd][pawn] = 0;
                                    //remove da enPassant'ed pawn
                                    ChessBoard[row + toAdd][colunm] = 0;
                                    validMove = true;
                                    alreadyWrong = false;

                                }
                                else{
                                    validMove = false;
                                    alreadyWrong = true;
                                }
                            }
                            //inval inp
                            else{
                                validMove = false;
                                alreadyWrong = true;
                            }

                        }


                    //le triangle le triangle
                    //un deux trois
                    //EXPLOSION DE RAQUETTE
                    //un deux trois
                    //un deux trois quatre
                    //Attention Attention le triangle
                    //un deux trois ooooffff
                    //le triangle le triangle
                    //un deux trois quatre cinq
                    //Attention Attention EXPLOSION DE RAQUETTE
                    //un deux trois
                    //le triangle
                    //le triangle
                            


                        else{  //invalid input
                            validMove = false;
                            alreadyWrong = true;
                        }

                        //Detect if the move is illegal because there is a king under check and u moved other thingie
                        if (validMove){

                            //set the king piece white or blak
                            int myKing = 5;
                            if (!whiteToMove) {
                                myKing = 15;
                            }
                            int kingRow = -1;
                            int kingCol = -1;

                            //find the king
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (ChessBoard[i][j] == myKing) {
                                        kingRow = i;
                                        kingCol = j;
                                        break;
                                    }
                                }
                            }

                            //detect if king is still in check(i prevent this at king move but there are possibility of Double CHeck)
                            if (isThisSquareUnderAttack(kingRow, kingCol, !whiteToMove, ChessBoard)){
                                validMove = false;
                                inCheck = true;

                                //Undo the board 
                                for (int i = 0; i < 8; i++) {
                                    ChessBoard[i] = Arrays.copyOf(backupBoard[i], 8);
                                }
                                
                            }
                        }

                        //================================PAWN PROMOTION==============================
                        if (validMove) {
                            int promotionRow = 0;
                            if (!whiteToMove) promotionRow = 7;
                            int promotingPawn = 1;
                            if (!whiteToMove) promotingPawn = 11;
            
                            for (int col = 0; col < 8; col++) {
                                if (ChessBoard[promotionRow][col] == promotingPawn) {
                                    clearTerminal();
                                    display(ChessBoard, moveHistory);
                                    System.out.println("______________________________________");

                                    char choice = ' ';
                                    boolean correctPiece = true;
                                    while (choice != 'Q' && choice != 'R' && choice != 'B' && choice != 'N'){
                                        if (correctPiece){
                                            correctPiece = false;
                                            System.out.print("Choose piece (Q/R/B/N): ");
                                        }
                                        else{
                                            System.out.print("Invalid. Please choose among these (Q/R/B/N): ");
                                        }
                                        choice = myScanner.next().toUpperCase().charAt(0);
                                    }
                                    int promotedPiece = 0;
                                    if (choice == 'Q'){
                                        promotedPiece = 6;
                                    }
                                    else if (choice == 'R'){
                                        promotedPiece = 4;
                                    }
                                    else if (choice == 'B'){
                                        promotedPiece = 3;
                                    }
                                    else if (choice == 'N'){
                                        promotedPiece = 2;
                                    }
                                    else{

                                    }
                                    if (!whiteToMove) promotedPiece += 10;
                                    ChessBoard[promotionRow][col] = promotedPiece;
                                }
                            }
                        }

                        if (validMove){
                            //50 move rule count
                            boolean isCapture = (backupBoard[row][colunm] != 0);
                            boolean isPawnMove = (ChessBoard[row][colunm] % 10 == 1);
                            if (isCapture || isPawnMove) fiftyMoveDrawCount = 0;
                            else fiftyMoveDrawCount++;
                            //save the position
                            positionHistory.add(boardToString(ChessBoard));

                            //Game Log Setting
                            String moveLog = move;

                            if (move.equals("O-O")) moveLog = "0-0";
                            else if (move.equals("O-O-O")) moveLog = "0-0-0";
                            else{
                                //if Piece takes Piece but there is no x, add x to indicate this is capture
                                if (isCapture && move.length() == 3 && Character.isUpperCase(move.charAt(0))){
                                    moveLog = move.charAt(0) + "x" + move.substring(1);
                                }
                                //if Check or Checkmate, add + or #
                                int enemyKing = 5;
                                if (whiteToMove) enemyKing = 15;
                                int enemyKingRow = 0;
                                int enemyKingCol = 0;
                                for (int i = 0; i < 8; i ++){
                                    for (int j = 0; j < 8; j ++){
                                        if (ChessBoard[i][j] == enemyKing){
                                            enemyKingRow = i;
                                            enemyKingCol = j;
                                            break;
                                        }
                                    }
                                }

                                if (isThisSquareUnderAttack(enemyKingRow, enemyKingCol, whiteToMove, ChessBoard)){
                                    if (whatKindOfMate(!whiteToMove, ChessBoard) == 1) moveLog += "#";
                                    else moveLog += "+";
                                }

                            }
                            moveHistory.add(moveLog);
                        }


                        //if the move is correct, change the player
                        if (validMove && whiteToMove){
                            whiteToMove = false;
                        }
                        else if (validMove && !whiteToMove){
                            whiteToMove = true;
                        }

                    }
                    clearTerminal();


                }

                //=============game end, display chessboard and declair who wins or draw===============

                display(ChessBoard, moveHistory);
                System.out.println("______________________________________");

                if (isBlackResigned || isWhiteResigned){
                    System.out.println("Resigned!");
                    if(isWhiteResigned) System.out.println("Black wins!!  (0 - 1)");
                    else System.out.println("White WIns!!  (1 - 0)");
                }
                else if (drawAccepted){
                    System.out.println("Draw by agreement!");
                    System.out.println("(1/2 - 1/2)");
                }
                else if (fiftyMoveDrawCount >= 100){
                    System.out.println("Draw by 50-move Rule!");
                    System.out.println("(1/2 - 1/2)");
                }
                else if (isInsufficientMaterial(ChessBoard)){
                    System.out.println("Draw by Insuficient Material!");
                    System.out.println("(1/2 - 1/2)");
                }
                else if (isThreefoldRepetition(positionHistory, ChessBoard)){
                    System.out.println("Draw by Threefold Repetition!");
                    System.out.println("(1/2 - 1/2)");
                }
                else if (whatKindOfMate(whiteToMove, ChessBoard) == 1){
                    System.out.println("Checkmate!");
                    if (whiteToMove) System.out.println("Black wins!!  (0 - 1)");
                    else System.out.println("White wins!!  (1 - 0)");
                }
                else{
                    System.out.println("Stalemate!");
                    System.out.println("(1/2 - 1/2)");
                }

            }
            else if (respond == 2){
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
                while (!understand.equals("x")){
                    understand = myScanner.next();
                }
            }


        }

        //White or Black WINS booboom you are magnus carlson-sout this

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