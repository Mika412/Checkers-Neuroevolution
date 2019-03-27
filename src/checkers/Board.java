/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.Vector;
public class Board {

    public int blackPieces;
    public int whitePieces;

    public int blackKings;
    public int whiteKings;

    public static final int rows = 8;
    public static final int cols = 8;
    public CellEntry[][] cell;

    public White white;
    public Black black;

    public Board(){
        this.blackPieces = this.whitePieces = 12;
        this.blackKings = this.whiteKings= 0;

        white = new White();
        black = new Black();

        this.cell = new CellEntry[][]{
                {CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid},
                {CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white},
                {CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid, CellEntry.white, CellEntry.inValid},
                {CellEntry.inValid, CellEntry.empty, CellEntry.inValid, CellEntry.empty, CellEntry.inValid, CellEntry.empty, CellEntry.inValid, CellEntry.empty},
                {CellEntry.empty, CellEntry.inValid, CellEntry.empty, CellEntry.inValid, CellEntry.empty, CellEntry.inValid, CellEntry.empty, CellEntry.inValid},
                {CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black},
                {CellEntry.black,CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid},
                {CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black, CellEntry.inValid, CellEntry.black}
        };
    }

    public Board(CellEntry[][] board){
        this.blackPieces = this.whitePieces = 12;
        this.blackKings = this.whiteKings= 0;

        white = new White();
        black = new Black();

        this.cell = new CellEntry[rows][cols];
        for(int i = 0; i<rows; i++){
            System.arraycopy(board[i], 0, this.cell[i], 0, cols);
        }
    }

    public Board(int[] board){
        this.blackPieces = this.whitePieces = 12;
        this.blackKings = this.whiteKings= 0;

        this.cell = new CellEntry[rows][cols];
        for(int i = 0; i < board.length; i++){
            CellEntry tempEntry = CellEntry.empty;

            if ( (i/rows)%2 == 0 && (i%cols) % 2 == 0)
                tempEntry = CellEntry.inValid;
            if ( (i/rows)%2 != 0 && (i%cols) % 2 != 0)
                tempEntry = CellEntry.inValid;


            if(board[i] == 1)
                tempEntry = CellEntry.white;
            else if(board[i] == 3)
                tempEntry = CellEntry.whiteKing;
            else if(board[i] == -1)
                tempEntry = CellEntry.black;
            else if(board[i] == -3)
                tempEntry = CellEntry.blackKing;

            if ( (i/rows)%2 == 0)
                this.cell[i/rows][i%cols] = tempEntry;
            else
                this.cell[i/rows][(i+1)%cols] = tempEntry;
        }
    }


    public void MakeMove(int r1, int c1, int r2, int c2)
    {
        this.cell[r2][c2] = this.cell[r1][c1];
        this.cell[r1][c1] = CellEntry.empty;

        // Promote To King
        if(this.cell[r2][c2].equals(CellEntry.white) && r2==rows-1){
            this.cell[r2][c2] = CellEntry.whiteKing;
            whiteKings++;
            whitePieces--;
        }
        else if(this.cell[r2][c2].equals(CellEntry.black) && r2==0){
            this.cell[r2][c2] = CellEntry.blackKing;
            blackKings++;
            blackPieces--;
        }
    }

    // Capture Black Piece and Move
    public void CaptureBlackPiece(int r1, int c1, int r2, int c2)
    {
        // Check Valid Capture
        assert(Math.abs(r2-r1)==2 && Math.abs(c2-c1)==2);

        // Obtain the capture direction
        MoveDir dir = r2>r1?(c2>c1?MoveDir.forwardRight:MoveDir.forwardLeft)
                :(c2>c1?MoveDir.backwardRight:MoveDir.backwardLeft);

        CellEntry typeCaptured = CellEntry.empty;
        // Removing Black Piece from the board
        switch(dir){
            case forwardLeft:
                typeCaptured = this.cell[r1+1][c1-1];
                this.cell[r1+1][c1-1] = CellEntry.empty;
                break;
            case forwardRight:
                typeCaptured = this.cell[r1+1][c1+1];
                this.cell[r1+1][c1+1] = CellEntry.empty;
                break;
            case backwardLeft:
                typeCaptured = this.cell[r1-1][c1-1];
                this.cell[r1-1][c1-1] = CellEntry.empty;
                break;
            case backwardRight:
                typeCaptured = this.cell[r1-1][c1+1];
                this.cell[r1-1][c1+1] = CellEntry.empty;
                break;
        }

        // Decreasing the count of black pieces
        if(typeCaptured.equals(CellEntry.black))
            this.blackPieces--;
        else if(typeCaptured.equals(CellEntry.blackKing))
            this.blackKings--;
        // Making move
        this.MakeMove(r1, c1, r2, c2);
        //return dir;
    }

    // Capture White Piece and Move
    public void CaptureWhitePiece(int r1, int c1, int r2, int c2)
    {
        // Check Valid Capture
        assert(Math.abs(r2-r1)==2 && Math.abs(c2-c1)==2);

        // Obtain the capture direction
        MoveDir dir = r2<r1?(c2<c1?MoveDir.forwardRight:MoveDir.forwardLeft)
                :(c2<c1?MoveDir.backwardRight:MoveDir.backwardLeft);


        CellEntry typeCaptured = CellEntry.empty;
        // Removing White Piece from the board
        switch(dir){
            case forwardLeft:
                typeCaptured = this.cell[r1-1][c1+1];
                this.cell[r1-1][c1+1] = CellEntry.empty;
                break;
            case forwardRight:
                typeCaptured = this.cell[r1-1][c1-1];
                this.cell[r1-1][c1-1] = CellEntry.empty;
                break;
            case backwardLeft:
                typeCaptured = this.cell[r1+1][c1+1];
                this.cell[r1+1][c1+1] = CellEntry.empty;
                break;
            case backwardRight:
                typeCaptured = this.cell[r1+1][c1-1];
                this.cell[r1+1][c1-1] = CellEntry.empty;
                break;
        }

        // Decreasing the count of black pieces
        if(typeCaptured.equals(CellEntry.white))
            this.whitePieces--;
        else if((typeCaptured.equals(CellEntry.whiteKing)))
            this.whiteKings--;

        // Making move
        this.MakeMove(r1, c1, r2, c2);
        //return dir;
    }

    /**
     * Makes all kinds of valid moves of a white player.
     * @param move
     */
    public void genericMakeWhiteMove(Move move){
        int r1 = move.initialRow;
        int c1 = move.initialCol;
        int r2 = move.finalRow;
        int c2 = move.finalCol;

        if((Math.abs(r2-r1)==2 && Math.abs(c2-c1)==2)){
            CaptureBlackPiece(r1, c1, r2, c2);

        }else{
            MakeMove(r1, c1, r2, c2);
        }
    }

    /**
     * Makes all kinds of valid moves of a black player.
     */
    public void genericMakeBlackMove(Move move){
        int r1 = move.initialRow;
        int c1 = move.initialCol;
        int r2 = move.finalRow;
        int c2 = move.finalCol;

        if(Math.abs(r2-r1)==2 && Math.abs(c2-c1)==2){
            CaptureWhitePiece(r1, c1, r2, c2);

        }else{
            MakeMove(r1, c1, r2, c2);
        }

    }

    public void Display()
    {
        this.DisplayColIndex();
        this.DrawHorizontalLine();

        for(int i = rows-1; i >=0; i--)
        {
            this.DisplayRowIndex(i);
            this.DrawVerticalLine();

            for(int j = 0; j< cols; j++)
            {
                System.out.print(this.BoardPiece(i,j));
                this.DrawVerticalLine();
            }

            this.DisplayRowIndex(i);
            System.out.println();
            this.DrawHorizontalLine();
        }

        this.DisplayColIndex();
        System.out.println();
    }

    private String BoardPiece(int i, int j) {
        assert(i>0 && i<rows && j>0 && j< cols);
        String str = new String();

        if(this.cell[i][j] == CellEntry.inValid){
            str = "     ";
        }
        else if(this.cell[i][j] == CellEntry.empty){
            str = "  _  ";
        }
        else if(this.cell[i][j] == CellEntry.white){
            str = "  ◆  ";
        }
        else if(this.cell[i][j] == CellEntry.black){
            str = "  ◇  ";
        }
        else if(this.cell[i][j] == CellEntry.whiteKing){
            str = "  " + ConsoleColors.YELLOW_BRIGHT + "◆" + ConsoleColors.RESET + " ";
        }
        else if(this.cell[i][j] == CellEntry.blackKing){
            str = "  " + ConsoleColors.PURPLE_BRIGHT + "◇" + ConsoleColors.RESET + " ";
        }

        return str;
    }

    private void DrawHorizontalLine() {
        System.out.println("    _______________________________________________");
    }

    private void DrawVerticalLine() {
        System.out.print("|");
    }

    private void DisplayColIndex() {
        System.out.print("   ");
        for(int colIndex = 0; colIndex<cols; colIndex++){
            System.out.print("   " + colIndex + "  " );
        }
        System.out.println();
    }

    private void DisplayRowIndex(int i) {
        System.out.print(" " + i + " ");
    }


    public Board duplicate(){
        Board newBoard = new Board(this.cell);
        newBoard.blackPieces = this.blackPieces;
        newBoard.whitePieces = this.whitePieces;
        newBoard.blackKings = this.blackKings;
        newBoard.whiteKings = this.whiteKings;


        return newBoard;
    }


    public boolean CheckGameComplete() {
        return ((this.blackPieces==0 && this.blackKings==0) || (this.whitePieces == 0 && this.whiteKings == 0))?true:false;
    }


    public boolean CheckGameDraw(Player turn){
        Vector<Vector<Move>> possibleMoveSeq = expandMoves(this.duplicate(), turn);

        if(possibleMoveSeq.isEmpty()){
            return true;

        }else{
            return false;
        }
    }

    public boolean isWhiteWinner(){
        boolean res = false;
        if(this.blackPieces <= 0 && this.blackKings <= 0){
            res = true;
        }
        return res;
    }

    public boolean isBlackWinner(){
        boolean res = false;
        if(this.whitePieces <= 0 && this.whiteKings <= 0){
            res = true;
        }
        return res;
    }

    public Board getFlipped(){

        CellEntry[][] flippedMatrix = new CellEntry[rows][cols];

        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++) {
                CellEntry tempEntry = CellEntry.inValid;
                if(cell[x][y] == CellEntry.white)
                    tempEntry = CellEntry.black;
                else if(cell[x][y] == CellEntry.black)
                    tempEntry = CellEntry.white;
                else if(cell[x][y] == CellEntry.whiteKing)
                    tempEntry = CellEntry.blackKing;
                else if(cell[x][y] == CellEntry.blackKing)
                    tempEntry = CellEntry.whiteKing;
                else if(cell[x][y] == CellEntry.empty)
                    tempEntry = CellEntry.empty;
                flippedMatrix[7 - x][7 - y] = tempEntry;

            }
        }

        return new Board(flippedMatrix);
    }


    public Vector<Vector<Move>> expandMoves(Board board, Player player){

        Vector<Vector<Move>> outerVector = new Vector<>();

        if(player == Player.black){

            Vector<Move> moves = black.CalculateAllForcedMovesForBlack(board);

            if(moves.isEmpty()){
                moves = black.CalculateAllNonForcedMovesForBlack(board);

                for(Move m:moves){
                    Vector<Move> innerVector = new Vector<Move>();
                    innerVector.add(m);
                    outerVector.add(innerVector);
                }

            }else{
                for(Move m:moves){

                    int r = m.finalRow;
                    int c = m.finalCol;
                    Vector<Move> innerVector = new Vector<Move>();

                    innerVector.add(m);

                    Board boardCopy = board.duplicate();
                    boardCopy.genericMakeBlackMove(m);
                    expandMoveRecursivelyForBlack(boardCopy, outerVector, innerVector, r, c);

                    innerVector.remove(m);

                }
            }

        }else if(player == Player.white){

            Vector<Move> moves = null;

            moves = white.CalculateAllForcedMovesForWhite(board);
            if(moves.isEmpty()){
                moves = white.CalculateAllNonForcedMovesForWhite(board);
                for(Move m:moves){
                    Vector<Move> innerVector = new Vector<Move>();
                    innerVector.add(m);
                    outerVector.add(innerVector);
                }
            }else{
                for(Move m:moves){

                    int r = m.finalRow;
                    int c = m.finalCol;
                    Vector<Move> innerVector = new Vector<Move>();

                    innerVector.add(m);

                    Board boardCopy = board.duplicate();
                    boardCopy.genericMakeWhiteMove(m);
                    expandMoveRecursivelyForWhite(boardCopy, outerVector, innerVector, r, c);

                    innerVector.remove(m);

                }

            }
        }
        return outerVector;
    }


    private void expandMoveRecursivelyForWhite(Board board, Vector<Vector<Move>> outerVector, Vector<Move> innerVector, int r, int c){

        Vector<Move> forcedMoves = white.ObtainForcedMovesForWhite(r, c, board);

        if(forcedMoves.isEmpty()){
            Vector<Move> innerCopy = (Vector<Move>)innerVector.clone();
            outerVector.add(innerCopy);
            return;

        }else{
            for(Move m:forcedMoves){

                Board boardCopy = board.duplicate();
                boardCopy.genericMakeWhiteMove(m);

                innerVector.add(m);
                expandMoveRecursivelyForWhite(boardCopy, outerVector, innerVector, m.finalRow, m.finalCol);
                innerVector.remove(m);

            }
        }


    }

    private  void expandMoveRecursivelyForBlack(Board board, Vector<Vector<Move>> outerVector, Vector<Move> innerVector, int r, int c){

        Vector<Move> forcedMoves = black.ObtainForcedMovesForBlack(r, c, board);

        if(forcedMoves.isEmpty()){
            Vector<Move> innerCopy = (Vector<Move>)innerVector.clone();
            outerVector.add(innerCopy);
            return;

        }else{
            for(Move m:forcedMoves){

                Board boardCopy = board.duplicate();
                boardCopy.genericMakeBlackMove(m);

                innerVector.add(m);
                expandMoveRecursivelyForBlack(boardCopy, outerVector, innerVector, m.finalRow, m.finalCol);
                innerVector.remove(m);

            }
        }
    }
}