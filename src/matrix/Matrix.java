package matrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Matrix {
    static final int INITIAL_SIZE = 5;
    static final int SIZE_INCREMENT_PER_RUN = 5;
    static final int MINIMUM = 0;
    static final int MAXIMUM = 100;
    static final int RUNS = 10;
    
    /**
     * Multiplies two matrices together using the naive approach.
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyNaive(int[][] matrix1, int[][] matrix2) {
        //  TODO: Not yet implemented
        return null;
    }
    /**
     * Multiplies two matrices together using the Stassen method.
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyStassen(int[][] matrix1, int[][] matrix2) {
        //  TODO: Not yet implemented
        return null;
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
    public static void runTrials(String outputFile) {
        //  Generate all of the trials ahead of time
        //  Two of each required to keep input of each method consistent
        //  Multiplying Ai * Bi
        int[][][] allMatrixA1 = new int[RUNS][][];
        int[][][] allMatrixB1 = new int[RUNS][][];
        int[][][] allMatrixA2 = new int[RUNS][][];
        int[][][] allMatrixB2 = new int[RUNS][][];
        for (int i = 0; i < RUNS; i++) {
            allMatrixA1[i] = generateMatrix(INITIAL_SIZE + RUNS * SIZE_INCREMENT_PER_RUN, MINIMUM, MAXIMUM);
            allMatrixB1[i] = generateMatrix(INITIAL_SIZE + RUNS * SIZE_INCREMENT_PER_RUN, MINIMUM, MAXIMUM);
            
            //  Copy the matrices to enforce the same input to each method
            allMatrixA2[i] = copyMatrix(allMatrixA1[i]);
            allMatrixB2[i] = copyMatrix(allMatrixB1[i]);
        }
        //  Keep track of the times for each run
        //  One set of times per method
        long[] times1 = new long[RUNS];
        long[] times2 = new long[RUNS];
        for (int i = 0; i < RUNS; i++){
            //  Time the first method
            long start1 = System.currentTimeMillis();
            multiplyNaive(allMatrixA1[i], allMatrixB1[i]);
            long end1 = System.currentTimeMillis();
            times1[i] = end1 - start1;
            
            //  Time the second method
            long start2 = System.currentTimeMillis();
            multiplyStassen(allMatrixA2[i], allMatrixB2[i]);
            long end2 = System.currentTimeMillis();
            times2[i] = end2 - start2;
        }
        
        try {
            FileWriter writer = new FileWriter(outputFile);
            //  TODO: not yet implemented
        }
        catch(IOException e) {
            //  My suggestion for this:
            //  System.out.println("Failed to write to " + outputFile + ". Writing to console instead.\n");
            //  TODO: not yet implemented
        }
    }
    public static void main(String[] args) {
        //runTrials();
        
        //  Test environment first
        int[][] matrixA = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        int[][] matrixB = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        printMatrix(matrixA);
        printMatrix(matrixB);
        printMatrix(multiplyNaive(matrixA, matrixB));
    }
}
