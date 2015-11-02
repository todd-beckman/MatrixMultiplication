package matrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Matrix {
    static final int INITIAL_SIZE = 5;
    static final int SIZE_INCREMENT_PER_RUN = 5;
    static final int MINIMUM = -10;
    static final int MAXIMUM = 10;
    static final int RUNS = 100;
    static final int TRIALS_PER_RUN = 10;

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
     * Multiplies two matrices together using the Stassen method.
     *
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyStrassen(int[][] matrix1, int[][] matrix2) {
        // TODO: augment multiplyNaive or make a new multiply method to take in starting i and j values
        int n = matrix1.length;
        int[][] result = new int[n][n];

        // initialize matrices.
        // X11 == start i: 0, start j: 0
        // X12 == start i: 0, start j: n/2
        // X21 == start i: n/2, start j: 0
        // X22 == start i: n/2, start j: n/2

//      M1 = mult(add(X11, X22), add(Y11, Y22));
        int[][] M1 = multiplyNaive(add(matrix1, 0, 0, matrix2, n / 2, n / 2), add(matrix2, 0, 0, matrix2, n / 2, n / 2));
//
//      M2 = mult(add(X21, X22), Y11);
        int[][] M2 = multiplyNaive(add(matrix1, n / 2, 0, matrix1, n / 2, n / 2), matrix2, 0, 0);

//      M3 = mult(X11, sub(Y12, Y22));
        int[][] M3 = multiplyNaive(matrix1, 0, 0, sub(matrix2, 0, n / 2, matrix2, n / 2, n / 2));

//      M4 = mult(X22, sub(Y21, Y11));
        int[][] M4 = multiplyNaive(matrix1, n / 2, n / 2, sub(matrix2, n / 2, 0, matrix2, n / 2, n / 2));

//      M5 = mult(add(X11, X12), Y22);
        int[][] M5 = multiplyNaive(add(matrix1, 0, 0, matrix1, 0, n / 2), matrix2, n / 2, n / 2);

//      M6 = mult(sub(X21, X11), add(Y11, Y12));
        int[][] M6 = multiplyNaive(sub(matrix1, n / 2, 0, matrix1, 0, 0), add(matrix2, 0, 0, matrix2, 0, n / 2));

//      M7 = mult(sub(X12, X22), add(Y21, Y22));
        int[][] M7 = multiplyNaive(sub(matrix1, 0, n / 2, matrix1, n / 2, n / 2), add(matrix2, n / 2, 0, matrix2, n / 2, n / 2));

        //TODO: do the second half a Strassen
        return matrix1;
    }

    /**
     * Both entire NxN matrices are passed in.  The passed in i's and j's are
     * the beginning of one of the four quadrants (e.g. 0,0; 0,n/2; n/2,0; or n/2,n/2).
     *
     * @param matrix1 the first matrix to add
     * @param i1      the starting i value for matrix1
     * @param j1      the starting j value for matrix1
     * @param matrix2 the second matrix to add
     * @param i2      the starting i value for matrix2
     * @param j2      the starting i value for matrix2
     **/
    public static int[][] add(int[][] matrix1, int i1, int j1, int[][] matrix2, int i2, int j2) {
        // TODO: run som manual tests on this method to verify its correctness
        int n = matrix1.length;
        int[][] result = new int[n / 2][n / 2];
        for (int i = 0; i1 < (i1 + n / 2); i++) {
            for (int j = 0; j1 < (j1 + n / 2); j++) {
                result[i][j] = matrix1[i1 + i][j1 + j] + matrix2[i2 + i][j2 + j];
            }
        }
        return result;
    }

    /**
     * Both entire NxN matrices are passed in.  The passed in i's and j's are
     * the beginning of one of the four quadrants (e.g. 0,0; 0,n/2; n/2,0; or n/2,n/2).
     *
     * @param matrix1 the first matrix to subtract
     * @param i1      the starting i value for matrix1
     * @param j1      the starting j value for matrix1
     * @param matrix2 the second matrix to subtract
     * @param i2      the starting i value for matrix2
     * @param j2      the starting i value for matrix2
     **/
    public static int[][] sub(int[][] matrix1, int i1, int j1, int[][] matrix2, int i2, int j2) {
        // TODO: run som manual tests on this method to verify its correctness
        int n = matrix1.length;
        int[][] result = new int[n / 2][n / 2];
        for (int i = 0; i1 < (i1 + n / 2); i++) {
            for (int j = 0; j1 < (j1 + n / 2); j++) {
                result[i][j] = matrix1[i1 + i][j1 + j] + matrix2[i2 + i][j2 + j];
            }
        }
        return result;
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
                matrix[i][j] = (int)(Math.random() * max) + min;
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
            // Find how long this run's size is going to be
            sizes[i] = INITIAL_SIZE + i * SIZE_INCREMENT_PER_RUN;

            // Track this run's splits
            times1[i] = 0;
            times2[i] = 0;

            // For each trial
            for (int j = 0; j < TRIALS_PER_RUN; j++) {
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
                multiplyStrassen(matrixA, matrixB);
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
                            "Size," + csvify(sizes) + "\n" + "Naive," + csvify(times1) + "\n" + "Strassen," + csvify(times2));
            writer.close();
        } catch (IOException e) {
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
        // runTrials("output.csv");

        // Test environment first
        int[][] matrixA = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        int[][] matrixB = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        printMatrix(matrixA);
        printMatrix(matrixB);
        printMatrix(multiplyNaive(matrixA, matrixB));
    }
}
