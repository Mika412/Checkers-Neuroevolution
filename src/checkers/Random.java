package checkers;

import java.util.Vector;

public class Random {

    White white;
    Black black;
    public Random(){
        white = new White();
        black = new Black();
    }
    public static boolean moveEqualsFinal(Move move, Move move2){
        return (move.finalRow == move2.finalRow
                && move.finalCol == move2.finalCol)?true:false;
    }

    public static String moveToString(Move move){
        return "(" + move.initialRow + "," + move.initialCol + ")->(" + move.finalRow + "," + move.finalCol + ")";
    }

    public void makeNextWhiteMoves(Board board){

        Vector<Move> resultantMoveSeq = new Vector<Move>();

        resultantMoveSeq = selectMove(board, Player.white, resultantMoveSeq);

        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeWhiteMove(m);
        }

        System.out.print("BOT's Move was ");
        System.out.println();
    }

    public void makeNextBlackMoves(Board board){

        Vector<Move> resultantMoveSeq = new Vector<Move>();

        resultantMoveSeq =  selectMove(board, Player.black, resultantMoveSeq);

        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeBlackMove(m);
        }



    }

    private Vector<Move> selectMove(Board board, Player player, Vector<Move> resultMoveSeq) {
        Vector<Vector<Move>> possibleMoveSeq = expandMoves(board, player);

        java.util.Random rand = new java.util.Random();
        int n = rand.nextInt(possibleMoveSeq.size()) ;
//        System.out.println("Chose " + n + " out of " + possibleMoveSeq.size());
        return possibleMoveSeq.get(n);
    }

    public static Vector<Vector<Move>> getSafeMoves(Vector<Vector<Move>> MyPlays, Vector<Vector<Move>> OthersPlays){
        Vector<Vector<Move>> SafePlays = new Vector<>();

        boolean hit = false;
        for (int i=0; i<MyPlays.size(); i++){
            Vector<Move> MyPlayVectorSelected = MyPlays.get(i);
            Move MyPlaySelected = MyPlayVectorSelected.get(MyPlayVectorSelected.size()-1);
            System.out.println("1) My Last Move: " + moveToString(MyPlaySelected));

            hit = false;
            for (int j=0; j<OthersPlays.size(); j++){
                Vector<Move> OthersPlayVectorSelected = OthersPlays.get(j);
                Move OthersPlaySelected = OthersPlayVectorSelected.get(OthersPlayVectorSelected.size()-1);
                System.out.println("2) His Last Move: " + moveToString(OthersPlaySelected));

                if (moveEqualsFinal(OthersPlaySelected, MyPlaySelected)){
                    System.out.println("3) Found Kill Move: " + moveToString(OthersPlaySelected));
                    hit = true;
                    break;
                }
            }

            if (!hit) SafePlays.add(MyPlays.get(i));
        }

        return SafePlays;
    }

    public Vector<Vector<Move>> expandMoves(Board board, Player player){
        // Returns all possibles plays

        Vector<Vector<Move>> outerVector = new Vector<Vector<Move>>();

        if(player == Player.black){

            Vector<Move> moves = null;
            moves = black.CalculateAllForcedMovesForBlack(board);

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

    private void expandMoveRecursivelyForBlack(Board board, Vector<Vector<Move>> outerVector, Vector<Move> innerVector, int r, int c){

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

    // For debugging (util)
    private static void displayMovesInVector(Vector<Move> v){
        for(Move m:v){
            m.display();
            System.out.print(", ");
        }
        System.out.println();
    }

}
