
package checkers;
import java.util.Vector;
/**
 *
 * @author apurv
 */
public class Robot {

    Oracle oracle;
    public  int MAX_DEPTH = 3;
    public  White white;
    public  Black black;

    public Robot(){
        white = new White();
        black = new Black();
        oracle = new Oracle();
    }
    public Board makeNextWhiteMoves(Board board){

        Vector<Move> resultantMoveSeq = new Vector<Move>();

        alphaBeta(board, Player.white, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);

        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeWhiteMove(m);
        }

        return board;
//        System.out.print("Robot's Move was ");
//        UserInteractions.DisplayMoveSeq(resultantMoveSeq);
//        System.out.println();
    }

    public Board makeNextBlackMoves(Board board){

        Vector<Move> resultantMoveSeq = new Vector<Move>();

        alphaBeta(board, Player.black, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);

        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeBlackMove(m);
        }

        return board;
//        System.out.print("Robot's Move was ");
//        UserInteractions.DisplayMoveSeq(resultantMoveSeq);
//        System.out.println();


    }

    /**
     * White represents the maximizing player, he/she wants to maximize the value of the board.
     * Black is the minimizing player, he/she wants to minimize the value of the board.
     *
     * alpha represents the maximum value that the max player is assured of, initially -inf.
     * beta represents the minimum value that the min player is assured of, initially +inf.
     *
     * if(alpha>beta) break
     *
     *
     */
    public int alphaBeta(Board board, Player player, int depth, int alpha, int beta, Vector<Move> resultMoveSeq){

        if(!canExploreFurther(board, player, depth)){
            int value = oracle.evaluateBoard(board, player);
            return value;
        }

        Vector<Vector<Move>> possibleMoveSeq = expandMoves(board, player);
        Vector<Board> possibleBoardConf = getPossibleBoardConf(board, possibleMoveSeq, player);

        Vector<Move> bestMoveSeq = null;

        if(player == Player.white){
            for(int i=0; i<possibleBoardConf.size(); i++){

                Board b = possibleBoardConf.get(i);
                Vector<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBeta(b, Player.black, depth+1, alpha, beta, resultMoveSeq);

                if(value > alpha){
                    alpha = value;
                    bestMoveSeq = moveSeq;
                }
                if(alpha>beta){
                    break;
                }
            }

            //If the depth is 0, copy the bestMoveSeq in the result move seq.
            if(depth == 0 && bestMoveSeq!=null){
                resultMoveSeq.addAll(bestMoveSeq);
            }
//            System.out.println("Best alpha " + alpha);

            return alpha;

        }else{
            assert(player == Player.black);

            for(int i=0; i<possibleBoardConf.size(); i++){

                Board b = possibleBoardConf.get(i);
                Vector<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBeta(b, Player.white, depth+1, alpha, beta, resultMoveSeq);
                if(value < beta){
                    bestMoveSeq = moveSeq;
                    beta = value;
                }
                if(alpha>beta){
                    break;
                }
            }
            //If the depth is 0, copy the bestMoveSeq in the result move seq.
            if(depth == 0 && bestMoveSeq!=null){
                resultMoveSeq.addAll(bestMoveSeq);
            }

//            System.out.println("Best beta " + beta);
            return beta;
        }
    }

    public Vector<Vector<Move>> expandMoves(Board board, Player player){

        Vector<Vector<Move>> outerVector = new Vector<>();

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


    private  boolean canExploreFurther(Board board, Player player, int depth){
        boolean res = true;
        if(board.CheckGameComplete()  || board.CheckGameDraw(player)){
            res = false;
        }
        if(depth == MAX_DEPTH){
            res = false;
        }
        return res;
    }


    public  Vector<Board> getPossibleBoardConf(Board board, Vector<Vector<Move>> possibleMoveSeq, Player player){
        Vector<Board> possibleBoardConf= new Vector<Board>();

        for(Vector<Move> moveSeq:possibleMoveSeq){
            Board boardCopy = board.duplicate();
            for(Move move:moveSeq){
                if(player == Player.black){
                    boardCopy.genericMakeBlackMove(move);

                }else{
                    boardCopy.genericMakeWhiteMove(move);
                }
            }
            possibleBoardConf.add(boardCopy);
            //System.out.println();
        }

        return possibleBoardConf;
    }

}