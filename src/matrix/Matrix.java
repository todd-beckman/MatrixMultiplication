package matrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Matrix {
    static final int INITIAL_SIZE = 5;
    static final int SIZE_INCREMENT_PER_RUN = 5;
    static final int MINIMUM = 0;
    static final int MAXIMUM = 100;
    static final int RUNS = 10;
    static final int TRIALS_PER_RUN = 10;
    
    /**
     * Multiplies two matrices together using the naive approach.
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyNaive(int[][] matrix1, int[][] matrix2) {
        //  TODO: Not yet implemented
        return matrix1;
    }
    
    /**
     * Multiplies two matrices together using the Stassen method.
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyStassen(int[][] matrix1, int[][] matrix2) {
        //  TODO: Not yet implemented
        return matrix1;
    }
    
    /**
     * Builds a square matrix of size nxn filled with components randomly between min and max
     * @param n The edge length
     * @param min The minimum inclusive of each cell
     * @param max The maximum exclusive of each cell
     * @return
     */
    public static int[][] generateMatrix(int n, int min, int max) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (int)(Math.random() * max) + min;
            }
        }
        return matrix;
    }
    
    /**
     * Produces a copy of a matrix. Fails if the input matrix is not square.
     * @param matrix The matrix to be copied
     * @return A copy matrix
     */
    public static int[][] copyMatrix(int[][] matrix) {
        int[][] matrix2 = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return matrix2;
    }
    
    /**
     * Prints the contents of a matrix to the console.
     * @param matrix The matrix to be printed
     */
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
        System.out.println();
    }
    
    /**
     * Runs a number of trials, averaging trials per run, with each run increasing the sizes of the input matrices.
     * @param outputFile
     */
    public static void runTrials(String outputFile) {
        //  Make a list of the sizes of each run
        int[] sizes = new int[RUNS];

        //  Keep track of the times for each run
        //  One set of times per method
        int[] times1 = new int[RUNS];
        int[] times2 = new int[RUNS];
        
        //  For each run
        for (int i = 0; i < RUNS; i++){
            //  Find how long this run's size is going to be
            sizes[i] = INITIAL_SIZE + RUNS * SIZE_INCREMENT_PER_RUN;

            //  Track this run's splits
            times1[i] = 0;
            times2[i] = 0;
            
            //  For each trial
            for (int j = 0; j < TRIALS_PER_RUN; j++) {
                //  Generate all of the trials ahead of time
                //  Two of each required to keep input of each method consistent
                //  Multiplying Ai * Bi
                int[][] matrixA1 = generateMatrix(sizes[i], MINIMUM, MAXIMUM);
                int[][] matrixB1 = generateMatrix(sizes[i], MINIMUM, MAXIMUM);
                int[][] matrixA2 = copyMatrix(matrixA1);
                int[][] matrixB2 = copyMatrix(matrixB1);
                
                //  Time the first method
                int start1 = (int)System.currentTimeMillis();
                multiplyNaive(matrixA1, matrixB1);
                int end1 = (int)System.currentTimeMillis();
                times1[i] += end1 - start1;
                
                //  Time the second method
                int start2 = (int)System.currentTimeMillis();
                multiplyStassen(matrixA2, matrixB2);
                int end2 = (int)System.currentTimeMillis();
                times2[i] += end2 - start2;
            }
            times1[i] /= TRIALS_PER_RUN;
            times2[i] /= TRIALS_PER_RUN;
        }
        
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(csvify(sizes));
            writer.write(csvify(times1));
            writer.write(csvify(times2));
            writer.close();
        }
        catch(IOException e) {
            System.out.println("Failed to write to " + outputFile + ". Writing to console instead.\n");
            System.out.println("Sizes per run:  " + csvify(sizes));
            System.out.println("Method 1 times: " + csvify(sizes));
            System.out.println("Method 2 times: " + csvify(sizes));
        }
    }
    private static String csvify(int[] arr) {
        String str = "";
        for (int i = 0; i < arr.length - 1; i++) {
            str += arr[i] + ",";
        }
        str += arr[arr.length - 1];
        return str;
    }
    public static void main(String[] args) {
        //runTrials("output.csv");
        
        //  Test environment first
        int[][] matrixA = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        int[][] matrixB = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        printMatrix(matrixA);
        printMatrix(matrixB);
        printMatrix(multiplyNaive(matrixA, matrixB));
    }
}
