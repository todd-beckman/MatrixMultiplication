package matrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Matrix {
    static final int INITIAL_SIZE = 100;
    static final int SIZE_INCREMENT_PER_RUN = 10;
    static final int MINIMUM = -5;
    static final int MAXIMUM = 5;
    static final int RUNS = 3;
    static final int TRIALS_PER_RUN = 5;

    /**
     * Multiplies two matrices together using the naive approach.
     *
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyNaive(int[][] matrix1, int[][] matrix2) {
        int[][] returnMatrix = new int[matrix1.length][matrix1.length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                for (int k = 0; k < matrix1.length; k++) {
                    returnMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return returnMatrix;
    }

    /**
     * Multiplies two matrices together using the divide and conquer method.
     *
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyDivideAndConquer(int[][] matrix1, int[][] matrix2) {
        //  Initialize solution array
        int[][] solution = new int[matrix1.length][matrix2.length];
        
        //  The low and high of the array.
        int n = matrix1.length - 1;
        
        //  Do the multiplication on the entire range
        multiplyDivideAndConquerHelper(solution, matrix1, matrix2, 0, n, 0, n);
        
        //  Return solution array
        return solution;
    }
    private static void multiplyDivideAndConquerHelper(
            int[][] solution, int[][] matrix1, int[][] matrix2,
            int low1, int high1, int low2, int high2) {

        if (low1 == high1 && low2 == high2) {
            //  Base case: Looking at a single row from the first and a single column from the second
            //  Find the dot product of this pair
            solution[low1][low2] = multiplyDivideAndConquerHelperAddition(
                    matrix1, matrix2, low1, low2, 0, solution.length - 1);
            return;
        }

        int middle1 = (low1 + high1) / 2;
        int middle2 = (low2 + high2) / 2;

        //  Top right
        Thread t1 = threadedDivideAndConquerHelper(solution, matrix1, matrix2, low1, middle1, middle2 + 1, high2);
        
        //  Bottom left
        Thread t2 = threadedDivideAndConquerHelper(solution, matrix1, matrix2, middle1 + 1, high1, low2, middle2);
            
        //  Bottom right
        Thread t3 = threadedDivideAndConquerHelper(solution, matrix1, matrix2, middle1 + 1, high1, middle2 + 1, high2);

        //  Top left (let this be the current thread's recursive call because it is the only one
        //          that is guaranteed to be called at every iteration (due to the bounds of others
        //          causing problems)
        multiplyDivideAndConquerHelper(solution, matrix1, matrix2, low1, middle1, low2, middle2);

        
        try{
            t1.join();
            t2.join();
            t3.join();
        }
        catch (InterruptedException e) {}
    }
    
    private static Thread threadedDivideAndConquerHelper(
            int[][] solution, int[][] matrix1, int[][] matrix2,
            int low1, int high1, int low2, int high2){
        Thread t = (new Thread() {
            public void run() {
                if (low1 <= high1 && low2 <= high2) {
                    multiplyDivideAndConquerHelper(solution, matrix1, matrix2, low1, high1, low2, high2);
                }
            }
        });
        t.start();
        return t;
    }
    
    private static int multiplyDivideAndConquerHelperAddition(
            int[][] matrix1, int[][] matrix2,
            int i, int j, int low, int high) {
        
        //  Base case: Looking at one element
        if (low == high) {
            int s = matrix1[i][low] * matrix2[low][j];
            //System.out.println(matrix1[i][low] + " * " + matrix2[low][j] + " = " + s);
            return s;
        }
        //  Find the left half of the dot product of this row/column pair
        int left = multiplyDivideAndConquerHelperAddition(
                matrix1, matrix2, i, j, low, (low + high) / 2);
        
        //  Find the right half of the dot product of this row/column pair
        int right = multiplyDivideAndConquerHelperAddition(
                matrix1, matrix2, i, j, (low + high) / 2 + 1, high);
        
        //  Add the two halves of the dot product
        return left + right;
    }
    
    /**
     * Builds a square matrix of size nxn filled with components randomly
     * between min and max
     *
     * @param n   The edge length
     * @param min The minimum inclusive of each cell
     * @param max The maximum exclusive of each cell
     * @return
     */
    public static int[][] generateMatrix(int n, int min, int max) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (int)(Math.random() * (max - min)) + min;
            }
        }
        return matrix;
    }

    /**
     * Produces a copy of a matrix. Fails if the input matrix is not square.
     *
     * @param matrix The matrix to be copied
     * @return A copy matrix
     */
    public static int[][] copyMatrix(int[][] matrix) {
        int[][] matrix2 = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            matrix2[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return matrix2;
    }

    /**
     * Prints the contents of a matrix to the console.
     *
     * @param matrix The matrix to be printed
     */
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
        System.out.println();
    }

    /**
     * Runs a number of trials, averaging trials per run, with each run
     * increasing the sizes of the input matrices.
     *
     * @param outputFile
     */
    public static void runTrials(String outputFile) {
        // Make a list of the sizes of each run
        int[] sizes = new int[RUNS];

        // Keep track of the times for each run
        // One set of times per method
        int[] times1 = new int[RUNS];
        int[] times2 = new int[RUNS];

        // For each run
        for (int i = 0; i < RUNS; i++) {
            System.out.print("\nStarting run " + i + ". Trials run: ");
            // Find how long this run's size is going to be
            sizes[i] = INITIAL_SIZE + i * SIZE_INCREMENT_PER_RUN;

            // Track this run's splits
            times1[i] = 0;
            times2[i] = 0;

            // For each trial
            for (int j = 0; j < TRIALS_PER_RUN; j++) {
                System.out.print("|");
                // Generate matrices to be multiplied
                int[][] matrixA = generateMatrix(sizes[i], MINIMUM, MAXIMUM);
                int[][] matrixB = generateMatrix(sizes[i], MINIMUM, MAXIMUM);

                // Time the first method
                long start1 = System.currentTimeMillis();
                multiplyNaive(matrixA, matrixB);
                long end1 = System.currentTimeMillis();
                times1[i] += (int)(end1 - start1);

                // Time the second method
                long start2 = System.currentTimeMillis();
                multiplyDivideAndConquer(matrixA, matrixB);
                long end2 = System.currentTimeMillis();
                times2[i] += (int)(end2 - start2);
            }

            // Find the average time per trial
            times1[i] /= TRIALS_PER_RUN;
            times2[i] /= TRIALS_PER_RUN;
        }

        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(
                            "Size," + csvify(sizes) + "\n" + "Naive," + csvify(times1) + "\n" + "Div/Conquer," + csvify(times2));
            writer.close();
        } catch (IOException e) {
            System.out.println("Failed to write to " + outputFile + ". Writing to console instead.\n");
            System.out.println("Sizes per run:  " + csvify(sizes));
            System.out.println("Method 1 times: " + csvify(times1));
            System.out.println("Method 2 times: " + csvify(times2));
        }
    }

    private static String csvify(int[] arr) {
        String str = "";
        for (int i = 0; i < arr.length - 1; i++) {
            str += ("" + arr[i] + ",");
        }
        str += "" + arr[arr.length - 1];
        return str;
    }

    public static void main(String[] args) {
        runTrials("output.csv");

        // Test environment first
//        int[][] matrixA = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
//        int[][] matrixB = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
//        printMatrix(matrixA);
//        printMatrix(matrixB);
//        printMatrix(multiplyNaive(matrixA, matrixB));
//        printMatrix(multiplyDivideAndConquer(matrixA, matrixB));
    }
}
