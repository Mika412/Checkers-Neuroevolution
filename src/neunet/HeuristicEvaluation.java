package neunet;

import checkers.*;

import java.util.ArrayList;
import java.util.List;

/**
 *  Heuristic evaluation class
 */
public class HeuristicEvaluation {

    boolean typeA = true;   //Switch to check between normalized and real values

    /**
     * Evaluation function
     * @param board current board
     * @param player    which player, black or white
     * @return
     */
    public double[] evaluateBoard(Board board, Player player) {
        double[] boardValue;

        if (player == Player.black) {
            boardValue = getScore(board.getFlipped());
        } else {
            boardValue = getScore(board);
        }

        return boardValue;
    }

    /**
     * Temporary class to save board positions
     */
    private static class TempPos{
        int row;
        int col;
        TempPos(int row, int col){
            this.row = col;
            this.col = row;
        }
    }

    /**
     * Calculate score for the board
     *
     * @param board board configuration
     * @return  an array of scores
     */
    private double[] getScore(Board board) {
        White white = new White();
        Black black = new Black();

        int nPawns = 0;
        int nKings = 0;

        int enemyPawns = 0;
        int enemyKings = 0;

        int safePawns = 0;
        int safeKings = 0;

        int movablePawns = 0;
        int movableKings = 0;

        int defenderPieces = 0;

        int centralPawns = 0;
        int centralKings = 0;


        int cornerPawns = 0;
        int cornerKings = 0;

        int aggregatedDistance = 0;
        int unocPromotion = 0;

        int lonerPawns = 0;
        int lonerKings = 0;

        int countEmpty = 0;

        double agrDistPawns = 0;
        double agrDistKings = 0;

        List<TempPos> blacksPos = new ArrayList<>();
        List<TempPos> whitesPos = new ArrayList<>();



        // Scan across the board
        for (int r = 0; r < Board.rows; r++) {
            // Check only valid cols
            int c = (r % 2 == 0) ? 0 : 1;

            for (; c < Board.cols; c += 2) {
                CellEntry entry = board.cell[r][c];


                // 1,2. Check pawns and kings
                if (entry == CellEntry.white)
                    nPawns++;
                else if (entry == CellEntry.whiteKing)
                    nKings++;

                // 1,2. Check pawns and kings
                if (entry == CellEntry.black)
                    enemyPawns++;
                else if (entry == CellEntry.blackKing)
                    enemyKings++;

                // 2,3. Check if the pawn and king is safe
                if(entry == CellEntry.white && (c == 0 || c == Board.cols-1))
                    safePawns++;
                else if(entry == CellEntry.whiteKing && (c == 0 || c == Board.cols-1))
                    safeKings++;

                // 4,5. Check if pawn and king is movable
                if(entry == CellEntry.white) {
                    if(white.ForwardLeftForWhite(r, c, board) != null || white.ForwardRightForWhite(r, c, board) != null)
                        movablePawns += 1;
                }else if(entry == CellEntry.whiteKing) {
                    if(white.ForwardLeftForWhite(r, c, board) != null || white.ForwardRightForWhite(r, c, board) != null || white.BackwardLeftForWhite(r, c, board) != null || white.BackwardRightForWhite(r, c, board) != null)
                        movableKings +=  1;
                }

                // 9. Defender pieces
                if(r < 2 && (entry == CellEntry.white || entry == CellEntry.whiteKing))
                    defenderPieces++;

                // 10. Attacking pieces
                if(r > 4 && entry == CellEntry.white)
                    defenderPieces++;

                // Aggregated distance
                if(entry == CellEntry.white)
                    aggregatedDistance += 7 - r;

                if(entry == CellEntry.empty && r == 7)
                    unocPromotion++;

                //Central pawns and kings
                if(r > 1 && r < 6 && c > 1 && c < 6){
                    if(entry == CellEntry.white)
                        centralPawns++;
                    else if(entry == CellEntry.whiteKing)
                        centralKings++;
                }

                //Pawns and kings in the corner
                if((r == 0 && c == 0) || (r == 7 && c == 7)) {
                    if (entry == CellEntry.white)
                        cornerPawns++;
                    else if (entry == CellEntry.whiteKing)
                        cornerKings++;
                }

                //Count lonely pieces
                if(white.ForwardLeftForWhite(r, c, board) != null
                        && white.ForwardRightForWhite(r, c, board) != null
                        && white.BackwardLeftForWhite(r, c, board) != null
                        && white.BackwardRightForWhite(r, c, board) != null) {
                    if (entry == CellEntry.white)
                        lonerPawns++;
                    else if(entry == CellEntry.whiteKing)
                        lonerKings++;
                }

                //Count empty slots
                if(entry == CellEntry.empty)
                    countEmpty++;

                if(entry == CellEntry.black || entry == CellEntry.blackKing)
                    blacksPos.add(new TempPos(r, c));

                if(entry == CellEntry.white || entry == CellEntry.whiteKing)
                    whitesPos.add(new TempPos(r, c));

            }
        }

        for(TempPos w : whitesPos){
            for(TempPos b : blacksPos){
                double dx = Math.abs(w.col - b.col); //Col
                double dy = Math.abs(w.row - b.row); //Row
                if(black.equals(CellEntry.black))
                    agrDistPawns += Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                else
                    agrDistKings += Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            }
        }
        if(typeA) {
            double difPawns = (nPawns - enemyPawns) / 12.0f;
            difPawns = (difPawns + 12.0f) / (12f + 12f);
            double difKings = (nKings - enemyKings) / 12.0f;
            difKings = (difKings + 12.0f) / (12f + 12f);
            double[] result = new double[]{nPawns / 12f, nKings / 12f, difPawns, difKings, safePawns / 12f, safeKings / 12f, movablePawns / 12f, movableKings / 12f, defenderPieces / 8f, centralPawns / 8f, centralKings / 8f, cornerPawns / 2f, cornerKings / 2f, aggregatedDistance / 20f, unocPromotion / 4f, lonerPawns / 12f, lonerKings / 12f, countEmpty / 24f, agrDistPawns / 1000f, agrDistKings / 1000f};
            return result;
        }else{
            double difPawns = nPawns - enemyPawns;
            double difKings = nKings - enemyKings;
            double[] result = new double[]{nPawns, nKings, difPawns, difKings, safePawns, safeKings, movablePawns, movableKings, defenderPieces, centralPawns, centralKings, cornerPawns, cornerKings, aggregatedDistance, unocPromotion, lonerPawns, lonerKings, countEmpty, agrDistPawns, agrDistKings};
            return result;
        }
    }
}
