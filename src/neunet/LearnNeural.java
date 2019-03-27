package neunet;

import checkers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Teacher class
 */
public class LearnNeural {

    static int maxMovesExpandBoard = 70;   //Number of moves per game
    static int maxTillSwitch = 10000;   // Max learning moves
    static List<double[]> tempX = new ArrayList<>();    //Temp
    static List<Double> tempY = new ArrayList<>();
    public static void main(String[] args) {
        TestNetwork();
    }

    /**
     * Tests and teaches the network
     */
    public static void TestNetwork(){
        int counterMoves;
        NeuralNetwork neuralNetwork;

        neuralNetwork = new NeuralNetwork(new int[]{20, 20,16,8, 1});
        HeuristicRobot heuristicRobot = new HeuristicRobot(neuralNetwork);

        Robot robot = new Robot();
        Random rand = new Random();
        double probabilityRandom = 0.1d;
        int switchCounter = 0;


        long startTime = System.currentTimeMillis();
        System.out.println("Start time " + startTime);

        for (int k = 0; k < 1000; k++) {

            double[][] trainX;
            double[][] trainY;
            /*
            if(switchCounter >= maxTillSwitch) {
                heuristicRobot.shouldSwitch = true;
                System.out.println(ConsoleColors.PURPLE_BRIGHT + " yes " + ConsoleColors.RESET);
            }else{
                System.out.println(ConsoleColors.YELLOW_BRIGHT + " no " + ConsoleColors.RESET);
            }*/
            int winnings = 0;
            double sumErrorGames = 0;
            for (int i = 1; i <= 4; i++) {
                Board board = new Board();

                counterMoves = 0;
                robot.MAX_DEPTH = i;

//                System.out.println(ConsoleColors.CYAN_BRIGHT + "Game " + k + " difficulty " + i +ConsoleColors.RESET);
                while (!board.CheckGameComplete() && counterMoves < maxMovesExpandBoard - 1) {
                    tempX.clear();
                    tempY.clear();
                    if (board.CheckGameDraw(Player.white)) {
//                        System.out.println("Draw white");
                        break;
                    }

                    heuristicRobot.makeNextWhiteMoves(board);

                    tempX = heuristicRobot.tempX;
                    tempY = heuristicRobot.tempY;


                    trainX = new double[tempX.size()][20];
                    trainY = new double[tempY.size()][1];
                    for (int j = 0; j < tempX.size(); j++) {
                        trainX[j] = tempX.get(j).clone();
                        trainY[j][0] = tempY.get(j);
                    }
                    //######################################### Train NETWORK #######################################################

                    if(trainX.length > 0) {
                        sumErrorGames += neuralNetwork.getError(trainX, trainY);
                        neuralNetwork.train(trainX, trainY);
                    }
                    tempX.clear();
                    tempY.clear();

                    if (board.isWhiteWinner()) {
//                        System.out.println("Won");
                        winnings++;
                        break;
                    }
                    if (board.CheckGameDraw(Player.black)) {
//                        System.out.println("Draw black");
                        break;
                    }
                    robot.makeNextBlackMoves(board);

                    if (board.isBlackWinner()) {
//                        System.out.println("Lost");
                        break;
                    }
                    counterMoves++;
                    switchCounter++;

                }
                //board.Display();
            }
            System.out.println(sumErrorGames);
           if(winnings == 3 && heuristicRobot.shouldSwitch == true){
                System.out.println(ConsoleColors.YELLOW_BACKGROUND_BRIGHT + "We did it!!" + ConsoleColors.RESET);

                long endTime = System.currentTimeMillis();
                System.out.println("End time " + endTime);
                System.exit(0);
            }
        }
    }
}