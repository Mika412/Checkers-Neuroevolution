
package checkers;

import neunet.*;

import java.util.*;

public class HeuristicRobot {

    public static int MAX_DEPTH = 4;
    public boolean shouldSwitch = true;

    public NeuralNetwork nn;
    public Oracle oracle;
    public Player predict = null;
    White white;
    Black black;
    private HeuristicEvaluation hr;




    public Map<Board, Map.Entry<Board, Integer>> savedValuesMap;

    public List<double[]> tempX = new ArrayList<>();
    public List<Double> tempY = new ArrayList<>();

    public HeuristicRobot(NeuralNetwork neuralNetwork){
        white = new White();
        black = new Black();
        this.nn = neuralNetwork;
        this.oracle = new Oracle();
        this.hr = new HeuristicEvaluation();
    }

    public Board makeNextWhiteMoves(Board board){
        predict = Player.white;
        Vector<Move> resultantMoveSeq = new Vector<Move>();

        savedValuesMap = new HashMap<>();

        tempX = new ArrayList<>();
        tempY = new ArrayList<>();
        alphaBeta(board, Player.white, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);

//        System.out.println(score);
//        System.out.println(savedValuesMap.size());
        for(Map.Entry entry : savedValuesMap.entrySet()){
            Board key = (Board) entry.getKey();
            Map.Entry<Board, Integer> value = (Map.Entry<Board, Integer>) entry.getValue();
            if(savedValuesMap.containsKey(value.getKey())){
                Map.Entry<Board, Integer> nextPos = savedValuesMap.get(value.getKey());
                if(value.getValue() % 2 == 0) {
                    tempX.add(hr.evaluateBoard(value.getKey(), Player.white));
                    tempY.add(calculateValue(nextPos.getKey(), Player.white));
                }else{
                    tempX.add(hr.evaluateBoard(value.getKey(), Player.black));
                    tempY.add(calculateValue(nextPos.getKey(), Player.black));
                }
            }
        }

        savedValuesMap.clear();


        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeWhiteMove(m);
        }
        return board;
    }

    public Board makeNextBlackMoves(Board board){
        predict = Player.black;

        Vector<Move> resultantMoveSeq = new Vector<Move>();

        savedValuesMap = new HashMap<>();
        tempX = new ArrayList<>();
        tempY = new ArrayList<>();

        alphaBeta(board, Player.black, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);

        //Apply the move to the game board.
        for(Move m:resultantMoveSeq){
            board.genericMakeBlackMove(m);
        }
        for(Map.Entry entry : savedValuesMap.entrySet()){
            Board key = (Board) entry.getKey();
            Map.Entry<Board, Integer> value = (Map.Entry<Board, Integer>) entry.getValue();
            if(savedValuesMap.containsKey(value.getKey())){
                Map.Entry<Board, Integer> nextPos = savedValuesMap.get(value.getKey());
                if(value.getValue() % 2 == 0) {
                    tempX.add(hr.evaluateBoard(value.getKey(), Player.black));
                    tempY.add(calculateValue(nextPos.getKey(), Player.black));
                }else{
                    tempX.add(hr.evaluateBoard(value.getKey(), Player.white));
                    tempY.add(calculateValue(nextPos.getKey(), Player.white));
                }
            }
        }

        return board;

    }

    public double alphaBeta(Board board, Player player, int depth, double alpha, double beta, Vector<Move> resultMoveSeq){
        if(!canExploreFurther(board, player, depth)){
            if(!shouldSwitch) {
                double value = oracle.evaluateBoard(board, predict);
                /*tempX.add(hr.evaluateBoard(board, Player.white));
                Double v = ((double)oracle.evaluateBoard(board, Player.white) + 10000.0d)/(10000.0d + 10000.0d);
                tempY.add(v);*/
                return value;
            }else {
                HeuristicEvaluation heuristicEvaluation = new HeuristicEvaluation();
                return nn.predict(heuristicEvaluation.evaluateBoard(board, predict))[0];

            }
        }

        Vector<Vector<Move>> possibleMoveSeq = expandMoves(board, player);
        Vector<Board> possibleBoardConf = getPossibleBoardConf(board, possibleMoveSeq, player);

        Vector<Move> bestMoveSeq = null;

        if(player == Player.white){
            for(int i=0; i<possibleBoardConf.size(); i++){

                Board b = possibleBoardConf.get(i);
                Vector<Move> moveSeq = possibleMoveSeq.get(i);

                double value = alphaBeta(b, Player.black, depth+1, alpha, beta, resultMoveSeq);

                if(value > alpha){
                    alpha = value;
                    bestMoveSeq = moveSeq;
                    savedValuesMap.put(board, new AbstractMap.SimpleEntry(b, depth));
                }
                if(alpha>beta){
                    break;
                }
            }

            //If the depth is 0, copy the bestMoveSeq in the result move seq.
            if(depth == 0 && bestMoveSeq!=null){
                resultMoveSeq.addAll(bestMoveSeq);
            }

            return alpha;

        }else{
            assert(player == Player.black);

            for(int i=0; i<possibleBoardConf.size(); i++){

                Board b = possibleBoardConf.get(i);
                Vector<Move> moveSeq = possibleMoveSeq.get(i);

                double value = alphaBeta(b, Player.white, depth+1, alpha, beta, resultMoveSeq);
                if(value < beta){
                    bestMoveSeq = moveSeq;
                    beta = value;
                    savedValuesMap.put(board, new AbstractMap.SimpleEntry(b, depth));
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


    private double calculateValue(Board board, Player player) {
        HeuristicEvaluation heuristicEvaluation = new HeuristicEvaluation();
        double value = nn.predict(heuristicEvaluation.evaluateBoard(board, player))[0];
        if (player == Player.white) {
            if (board.CheckGameComplete() && board.isWhiteWinner()) {
                value = 1.0f;
            } else if (board.CheckGameComplete() && board.isBlackWinner()) {
                value = 0.0f;
            } else if ((board.CheckGameDraw(Player.black) || board.CheckGameDraw(Player.white))) {
                value = 0.5f;
            }
        } else if (player == Player.black) {
            if (board.CheckGameComplete() && board.isBlackWinner())
                value = 1.0f;
            else if (board.CheckGameComplete() && board.isWhiteWinner())
                value = 0.0f;
            else if ((board.CheckGameDraw(Player.black) || board.CheckGameDraw(Player.white))) {
                value = 0.5f;
            }
        }
        return value;
    }


    public Vector<Vector<Move>> expandMoves(Board board, Player player){

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


    private static boolean canExploreFurther(Board board, Player player, int depth){
        boolean res = true;
        if(board.CheckGameComplete()  || board.CheckGameDraw(player)){
            res = false;
        }
        if(depth == MAX_DEPTH){
            res = false;
        }
        return res;
    }


    public static Vector<Board> getPossibleBoardConf(Board board, Vector<Vector<Move>> possibleMoveSeq, Player player){
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