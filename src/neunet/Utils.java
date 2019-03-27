package neunet;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {


    /**
     * Generates a random value between a range
     *
     * @param min   min range value
     * @param max   max range value
     * @return      return a double
     */
    public static double randDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    /**
     * Generate a random integer between a range
     * @param min   min range
     * @param max   max range
     * @return
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * Executes a sigmoid activation function
     *
     * @param z desired value
     * @return  activation value
     */
    public static double sigmoid(double z){
        return 1.0/(1.0+Math.exp(-z));
    }

    /**
     * Calculates the derivative of the sigmoid activation function
     *
     * @param z desired value
     * @return  derivative of the value
     */
    public static double sigmoidDerivative(double z){
        return z*(1.0-z);
    }

    /**
     * Executes a ReLu activation function
     *
     *  @param x desired value
     *  @return  activation value
     */
    public static double ReLu(double x){
        return Math.max(0, x);
    }

    /**
     * Executes a Leaky ReLu activation function
     *
     *  @param x desired value
     *  @return  activation value
     */
    public static double LeakyReLu(double x){
        return Math.max(0.01f * x, x);
    }

    /**
     * Calculates the derivative of the ReLu activation function
     *
     *  @param x desired value
     *  @return  activation value
     */
    public static double ReLuDerivative(double x){
        return x > 0 ? 1 : 0;
    }
}
