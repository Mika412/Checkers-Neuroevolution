package neunet;

public class NeuralNetwork{

    private double alpha=0.001d;
    private int trainingCycles=20;

    private int inputSize=0;

    public double trainOutput[][];
    public double trainInputs[][];
    private int trainSize;

    private int[] inputNodes;

    private double hiddenWeight[][][];
    private double errors[][];
    public double hiddenNeuron[][];

    public NeuralNetwork(int[] inputNodes) {
        this.inputNodes = inputNodes;
        initNeuron();
        initWeight();
    }

    public NeuralNetwork(double[][][] hiddenWeight) {
        inputNodes = new int[hiddenWeight.length + 1];
        for (int i = 0; i < hiddenWeight.length; i++) {
            inputNodes[i] = hiddenWeight[i].length - 1;
        }
        inputNodes[inputNodes.length - 1] = hiddenWeight[hiddenWeight.length - 1][0].length;

        this.hiddenWeight = hiddenWeight.clone();
        initNeuron();
    }

    public void train(double[][] trainI, double[][] trainO) {
        trainInputs = trainI.clone();
        trainOutput = trainO.clone();
        inputSize = trainI.length;
        trainSize = inputSize;
        for(int epoch=0;epoch<trainingCycles;epoch++){
            for(int currSample = 0; currSample < trainSize; currSample++) {
                feedForward(trainInputs[currSample]);
                backprop(trainOutput[currSample].clone());
            }
        }
    }



    public double[] predict(double[][] testInputs){
        double[] predicted = new double[testInputs.length];
        double[] inputs = null;
        for(int i=0;i<testInputs.length;i++){
                inputs = testInputs[i];
            predicted[i] = feedForward(inputs)[0];
        }
        return predicted;
    }

    public double[] predict(double[] testInputs){
        double[] predicted = null;
        predicted = feedForward(testInputs);
        return predicted;
    }

    private void initNeuron() {
        hiddenNeuron = new double[inputNodes.length][];
        errors = new double[inputNodes.length][];
        for (int i = 0; i < inputNodes.length; i++) {
            hiddenNeuron[i] = new double[inputNodes[i]];
            errors[i] = new double[inputNodes[i]];
        }
    }

    private void initWeight() {
        int countWeights = 0;
        hiddenWeight = new double[inputNodes.length-1][][];
        for (int i = 0; i < hiddenWeight.length; i++) {
            hiddenWeight[i] = new double[inputNodes[i] + 1][inputNodes[i + 1]];
            countWeights += (inputNodes[i] + 1) * inputNodes[i + 1];
        }
        double randRange = 1.0d/ Math.sqrt(countWeights);

        for(int i = 0; i < hiddenWeight.length;i++){
            for(int k = 0; k < hiddenWeight[i].length; k++) {
                for (int j = 0; j < hiddenWeight[i][k].length; j++) {
//                    hiddenWeight[i][k][j] = 0;
                    hiddenWeight[i][k][j] = Utils.randDouble(-randRange, randRange);
//                    System.out.println("w " + hiddenWeight[i][k][j]);
                }
            }
        }
    }

    public double getError(double[][] inputs, double[][] outputs){
        double[] predicted = predict(inputs);
        double sumError = 0;
//        System.out.println(outputs.length);
//        System.out.println(predicted.length);
        for (int i = 0; i < outputs.length; i++) {
            sumError += Math.pow(predicted[i] - outputs[i][0],2) / 2.0d;
        }
        return  sumError;
    }

    private void forward(double[][] weight, double[] prev, double[] next) {
        for(int i = 0 ; i < next.length; i++) {
            double sum = 0;
            for(int j = 0 ; j< prev.length; j++) {
                sum += prev[j] * weight[j][i];
            }
            sum += weight[weight.length - 1][i];
            next[i]= Utils.sigmoid(sum);
        }
    }

    private double[] feedForward(double[] inputs) {
        this.hiddenNeuron[0] = inputs.clone();
        for(int i = 1; i < inputNodes.length; i++) {
            forward(hiddenWeight[i - 1], hiddenNeuron[i-1], hiddenNeuron[i]);
        }
        return hiddenNeuron[hiddenNeuron.length - 1];
    }

    private void backprop(double[] expected){

        for(int i = 0; i < inputNodes[inputNodes.length - 1]; i++){
            errors[errors.length - 1][i] = (expected[i] - hiddenNeuron[hiddenNeuron.length - 1][i]) * Utils.sigmoidDerivative(hiddenNeuron[hiddenNeuron.length - 1][i]);
        }

        for(int hl = errors.length - 2; hl >= 1; hl--){
            for (int i = 0; i < errors[hl].length; i++) {
                errors[hl][i]=0.0;
                for (int j = 0; j < errors[hl + 1].length; j++) {
                    errors[hl][i] += errors[hl + 1][j] * hiddenWeight[hl][i][j];
                }
                errors[hl][i] *= Utils.sigmoidDerivative(hiddenNeuron[hl][i]);
            }
        }

        for(int hl = hiddenNeuron.length - 2; hl >= 0; hl--){
            for (int j = 0; j < hiddenNeuron[hl + 1].length; j++) {
                for (int i = 0; i < hiddenNeuron[hl].length; i++) {
                    hiddenWeight[hl][i][j] += (alpha * errors[hl + 1][j] * hiddenNeuron[hl][i]);
//                    System.out.println(hiddenWeight[hl][i][j] + " " + errors[hl][i] + " " + hiddenNeuron[hl][i]);
                }
                hiddenWeight[hl][hiddenWeight[hl].length - 1][j] += (alpha * errors[hl + 1][j]);
            }
        }
//        System.out.println(ConsoleColors.YELLOW_BRIGHT + "ended" + ConsoleColors.RESET);
    }


    public double sigmoid(double x){
        return 1.0d/(1.0d+Math.exp(-x));
    }
    public double sigmoidDerivative(double x){
        return x*(1.0d-x);
    }
}