package neunet;

import checkers.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Teach by playing playing Network vs Network
 */
public class LearnNeuralNvsN {

    static int maxTillSwitch = 10000;
    static int maxMovesExpandBoard = 70;
    static List<double[]> whiteTempX = new ArrayList<>();
    static List<double[]> blackTempX = new ArrayList<>();
    static List<Double> whiteTempY = new ArrayList<>();
    static List<Double> blackTempY = new ArrayList<>();
    static HeuristicRobot whiteRobot;
    static NeuralNetwork whiteNetwork;
    public static void main(String[] args) {
        TestNetwork();
    }

    public static void TestNetwork(){
        int counterMoves;
        whiteNetwork = new NeuralNetwork(new int[]{20,20,12,8,1});
        whiteRobot = new HeuristicRobot(whiteNetwork);

        NeuralNetwork blackNetwork = new NeuralNetwork(new int[]{20,32,1});
        HeuristicRobot blackRobot = new HeuristicRobot(blackNetwork);

        int switchCounter = 0;
//        whiteRobot.shouldSwitch = false;
        for (int k = 0; k < 10000; k++) {

            if(switchCounter >= maxTillSwitch) {
                whiteRobot.shouldSwitch = true;
                System.out.println(ConsoleColors.PURPLE_BRIGHT + " yes " + ConsoleColors.RESET);
            }else{
                System.out.println(ConsoleColors.YELLOW_BRIGHT + " no " + ConsoleColors.RESET);
            }

            System.out.println(ConsoleColors.CYAN_BRIGHT + "Game " + k + " difficulty " + k +ConsoleColors.RESET);

            if(k % 50 == 0) {
                System.out.println("Did enter " + k);
                testAgainstRobot();
                continue;
            }
            Board board = new Board();

            double[][] whiteTrainX;
            double[][] whiteTrainY;
            double[][] blackTrainX;
            double[][] blackTrainY;

            counterMoves = 0;
            while (!board.CheckGameComplete() && counterMoves < maxMovesExpandBoard - 1) {

                whiteTempX.clear();
                blackTempX.clear();
                whiteTempY.clear();
                blackTempY.clear();

                if (board.CheckGameDraw(Player.white)) {
                    System.out.println("Draw white");
                    break;
                }

                whiteRobot.makeNextWhiteMoves(board);


                whiteTempX = whiteRobot.tempX;
                whiteTempY = whiteRobot.tempY;


                whiteTrainX = new double[whiteTempX.size()][20];
                whiteTrainY = new double[whiteTempY.size()][1];
                for (int j = 0; j < whiteTempX.size(); j++) {
                    whiteTrainX[j] = whiteTempX.get(j).clone();
                    whiteTrainY[j][0] = whiteTempY.get(j);
                }

                whiteTempX.clear();
                whiteTempY.clear();

                //######################################### Train NETWORK #######################################################
                if(whiteTrainX.length > 0)
                    whiteNetwork.train(whiteTrainX, whiteTrainY);


                if (board.isWhiteWinner()) {
                    System.out.println("Won");
                    break;
                }
                if (board.CheckGameDraw(Player.black)) {
                    System.out.println("Draw black");
                    break;
                }

                blackRobot.makeNextBlackMoves(board);


                blackTempX = blackRobot.tempX;
                blackTempY = blackRobot.tempY;


                blackTrainX = new double[blackTempX.size()][20];
                blackTrainY = new double[blackTempY.size()][1];
                for (int j = 0; j < blackTempX.size(); j++) {
                    blackTrainX[j] = blackTempX.get(j).clone();
                    blackTrainY[j][0] = blackTempY.get(j);
                }

                blackTempX.clear();
                blackTempY.clear();

                //######################################### Train NETWORK #######################################################
                if(blackTrainX.length > 0)
                    blackNetwork.train(blackTrainX, blackTrainY);

                if (board.isBlackWinner()) {
                    System.out.println("Lost");
                    break;
                }
                counterMoves++;
                switchCounter++;

            }

            board.Display();
            }
    }

    private static void testAgainstRobot() {

        double[][] trainX;
        double[][] trainY;
        List<double[]> tempX = new ArrayList<>();
        List<Double> tempY = new ArrayList<>();

        Robot robot = new Robot();
        int winnings = 0;
        for (int i = 1; i <= 3; i++) {
            Board board = new Board();

            int counterMoves = 0;
            robot.MAX_DEPTH = i;

            System.out.println(ConsoleColors.CYAN_BRIGHT + " difficulty " + i +ConsoleColors.RESET);
            while (!board.CheckGameComplete() && counterMoves < maxMovesExpandBoard - 1) {
                tempX.clear();
                tempY.clear();

                if (board.CheckGameDraw(Player.white)) {
                    System.out.println("Draw white");
                    break;
                }

                whiteRobot.makeNextWhiteMoves(board);


                if (board.isWhiteWinner()) {
                    System.out.println("Won");
                    winnings++;
                    break;
                }
                if (board.CheckGameDraw(Player.black)) {
                    System.out.println("Draw black");
                    break;
                }

                robot.makeNextBlackMoves(board);

                if (board.isBlackWinner()) {
                    System.out.println("Lost");
                    break;
                }
                counterMoves++;

                trainX = new double[tempX.size()][20];
                trainY = new double[tempY.size()][1];
                for (int j = 0; j < tempX.size(); j++) {
                    trainX[j] = tempX.get(j).clone();
                    trainY[j][0] = tempY.get(j);
                }

                tempX.clear();
                tempY.clear();

                //######################################### Train NETWORK #######################################################
                if(trainX.length > 0)
                    whiteNetwork.train(trainX, trainY);
            }

            board.Display();
        }
        if(winnings == 3){
            System.out.println(ConsoleColors.YELLOW_BACKGROUND_BRIGHT + "We did it!!" + ConsoleColors.RESET);
            System.exit(0);
        }
    }
}