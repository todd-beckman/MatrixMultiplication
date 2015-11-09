package matrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Matrix {
    /**
     * The size of the first matrices during the first run of the experiment.
     */
    static final int INITIAL_SIZE = 17;
    
    /**
     * The size increase of the matrices when stepping up to higher runs.
     */
    static final int SIZE_INCREMENT_PER_RUN = INITIAL_SIZE;
    
    /**
     * The inclusive lower bound on the generated matrix cell values
     */
    static final int MINIMUM = -10;
    
    /**
     * The exclusive upper bound on the generated matrix cell values
     */
    static final int MAXIMUM = 10;
    
    /**
     * Number of test runs to make, incrementing by SIZE_INCREMENT_PER_RUN every time
     */
    static final int RUNS = 10;
    
    /**
     * Number of trials at each run, where the average of this many trials is recorded in the output.
     */
    static final int TRIALS_PER_RUN = 5;
    
    /**
     * The number of recursive calls in the Divide and Conquer method to make recursively. The log of this number
     * is the number of threads that will be opened in parallel. Only the dividing and conquering part is recursive.
     */
    static final int DEPTH_OF_PARALLEL_RECURSION = 0;

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
     * Multiplies two matrices together using the naive method, except in span(log(n)) time
     * using parallel programming.
     *
     * @param matrix1 The first matrix to multiply
     * @param matrix2 The second matrix to multiply
     * @return The result of the multiplication
     */
    public static int[][] multiplyNaiveParallel(int[][] matrix1, int[][] matrix2) {
        //  Initialize solution array
        int[][] solution = new int[matrix1.length][matrix2.length];
        
        //  The low and high of the array.
        int n = matrix1.length - 1;
        
        //  Do the multiplication on the entire range
        multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, 0, n, 0, n, 0);
        
        //  Return solution array
        return solution;
    }
    
    /**
     * Recursively splits the first matrix by row and the second matrix by column
     * @param solution The solution matrix in progress
     * @param matrix1 The first operand matrix
     * @param matrix2 The second operand matrix
     * @param low1 The lower bound on the left matrix range of rows
     * @param high1 The upper bound on the right matrix range of rows
     * @param low2 The lower bound on the right matrix range of columns
     * @param high2 The higher bound on the right matrix range of columns
     * @param depth The current recursion depth, used to determine whether to spawn new threads
     */
    private static void multiplyNaiveParallelHelperSplitting(
            int[][] solution, int[][] matrix1, int[][] matrix2,
            int low1, int high1, int low2, int high2,   //  keep track of the bounds on the matrices
            int depth                                   //  keep track of how deep this recursion is to determine
            ) {

        if (low1 == high1 && low2 == high2) {
            //  Base case: Looking at a single row from the first and a single column from the second
            //  Find the dot product of this pair
            solution[low1][low2] = multiplyNaiveHelperAddition(
                    matrix1, matrix2, low1, low2, 0, solution.length - 1);
            return;
        }

        int middle1 = (low1 + high1) / 2;
        int middle2 = (low2 + high2) / 2;
        
        if (depth < DEPTH_OF_PARALLEL_RECURSION) {
            //  Top right
            Thread t1 = threadedNaiveMultiplyHelperSplitting(solution, matrix1, matrix2, low1, middle1, middle2 + 1, high2, depth + 1);
            
            //  Bottom left
            Thread t2 = threadedNaiveMultiplyHelperSplitting(solution, matrix1, matrix2, middle1 + 1, high1, low2, middle2 + 1, depth + 1);
                
            //  Bottom right
            Thread t3 = threadedNaiveMultiplyHelperSplitting(solution, matrix1, matrix2, middle1 + 1, high1, middle2 + 1, high2, depth + 1);
    
            //  Top left (let this be the current thread's recursive call because it is the only one
            //          that is guaranteed to be called at every iteration (due to the bounds of others
            //          causing problems)
            multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, low1, middle1, low2, middle2, depth + 1);
    
            try{
                t1.join();
                t2.join();
                t3.join();
            }
            catch (InterruptedException e) {}
        }
        else {
            if (middle2 < high2) {
                //  Top right
                multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, low1, middle1, middle2 + 1, high2, depth + 1);
            }
            if (middle1 < high1) {
                //  Bottom left
                multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, middle1 + 1, high1, low2, middle2, depth + 1);
            }
            
            if (middle1 < high1 && middle2 < high2) {
                //  Bottom right
                multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, middle1 + 1, high1, middle2 + 1, high2, depth + 1);
            }
    
            //  Top left
            multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, low1, middle1, low2, middle2, depth + 1);
        }
    }

    /**
     * Starts a new thread which will recursively split the first matrix by row and the second matrix by column
     * @param solution The solution matrix in progress
     * @param matrix1 The first operand matrix
     * @param matrix2 The second operand matrix
     * @param low1 The lower bound on the left matrix range of rows
     * @param high1 The upper bound on the right matrix range of rows
     * @param low2 The lower bound on the right matrix range of columns
     * @param high2 The higher bound on the right matrix range of columns
     * @param depth The current recursion depth, used to determine whether to spawn new threads
     * @return The thread instance which has been started
     */
    private static Thread threadedNaiveMultiplyHelperSplitting(
            int[][] solution, int[][] matrix1, int[][] matrix2,
            int low1, int high1, int low2, int high2, int depth){
        Thread t = (new Thread() {
            public void run() {
                if (low1 <= high1 && low2 <= high2) {
                    multiplyNaiveParallelHelperSplitting(solution, matrix1, matrix2, low1, high1, low2, high2, depth);
                }
            }
        });
        t.start();
        return t;
    }
    
    /**
     * Performs the dot product in the ith row of matrix 1 and the jth column of matrix 2 in the specified range
     * @param matrix1 s
     * @param matrix2
     * @param i
     * @param j
     * @param low
     * @param high
     * @return
     */
    private static int multiplyNaiveHelperAddition(
            int[][] matrix1, int[][] matrix2,
            int i, int j, int low, int high) {
        
        //  Base case: Looking at one element.
        if (low == high) {
            int s = matrix1[i][low] * matrix2[low][j];
            return s;
        }
        //  Find the left half of the dot product of this row/column pair
        int left = multiplyNaiveHelperAddition(
                matrix1, matrix2, i, j, low, (low + high) / 2);
        
        //  Find the right half of the dot product of this row/column pair
        int right = multiplyNaiveHelperAddition(
                matrix1, matrix2, i, j, (low + high) / 2 + 1, high);
        
        //  Add the two halves of the dot product
        return left + right;
    }
    
    /**
     * Multiplies two matrices using the Divide and Conquer method
     * @param matrix1 The first operand
     * @param matrix2 The second operand
     * @return The completed matrix
     */
    public static int[][] multiplyDivideAndConquer(int[][] matrix1, int[][] matrix2) {
        int n = matrix1.length;
        return multiplyDivideAndConquerHelper(new int[n][n], matrix1, matrix2);
    }
    
    /**
     * Is literally the stupidest code I have ever written. Use of this method is highly discouraged.
     * In fact, I'm not even going to document what parameters are.
     */
    private static int[][] multiplyDivideAndConquerHelper(int[][] into, int[][] a, int[][] b) {
        if (a.length == 0 || a[0].length == 0) {
            return a;
        }
        if (b.length == 0 || b[0].length == 0) {
            return b;
        }
        if (a.length == 1 && a[0].length == 1 && b.length == 1 && b[0].length == 1) {
            into[0][0] = a[0][0] * b[0][0];
            return into;
        }
        
        //  Reluctantly allocate yet another matrix of memory
        int[][] temp = new int[into.length][into[0].length];

        //  Reluctantly split the matrices
        int[][][] aSplit = splitMatrixInto4(a);
        int[][][] bSplit = splitMatrixInto4(b);
        int[][][] cSplit = splitMatrixInto4(into);
        int[][][] tSplit = splitMatrixInto4(temp);

        //  This algorithm is too terrible to be worthy of comments.
        //  These occur in the same order as shown here: 
        //  https://en.wikipedia.org/wiki/Matrix_multiplication_algorithm#Divide_and_conquer_algorithm
        return multiplyDivideAndConquerHelperAddition(
                join4Matrices(
                        multiplyDivideAndConquerHelper(cSplit[0], aSplit[0], bSplit[0]),
                        multiplyDivideAndConquerHelper(cSplit[1], aSplit[0], bSplit[1]),
                        multiplyDivideAndConquerHelper(cSplit[2], aSplit[2], bSplit[0]),
                        multiplyDivideAndConquerHelper(cSplit[3], aSplit[2], bSplit[1])),
                join4Matrices(
                        multiplyDivideAndConquerHelper(tSplit[0], aSplit[1], bSplit[2]),
                        multiplyDivideAndConquerHelper(tSplit[1], aSplit[1], bSplit[3]),
                        multiplyDivideAndConquerHelper(tSplit[2], aSplit[3], bSplit[2]),
                        multiplyDivideAndConquerHelper(tSplit[3], aSplit[3], bSplit[3])));
    }
    
    private static int[][] multiplyDivideAndConquerHelperAddition(int[][] left, int[][] right) {
        if (left.length == 0 || left[0].length == 0) {
            return left;
        }
        if (right.length == 0 || right[0].length == 0) {
            return right;
        }
        if (left.length == 1 && left[0].length == 1 && right.length == 1 && right[0].length == 1) {
            left[0][0] += right[0][0];
            return left;
        }

        int[][][] leftSplit = splitMatrixInto4(left);
        int[][][] rightSplit = splitMatrixInto4(right);

        return join4Matrices(
            multiplyDivideAndConquerHelperAddition(leftSplit[0], rightSplit[0]),
            multiplyDivideAndConquerHelperAddition(leftSplit[1], rightSplit[1]),
            multiplyDivideAndConquerHelperAddition(leftSplit[2], rightSplit[2]),
            multiplyDivideAndConquerHelperAddition(leftSplit[3], rightSplit[3]));
    }
    
    private static int[][][] splitMatrixInto4 (int[][] matrix) {
        
        //  Get the size
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][][] matrices = new int[4][][];

        
        //  Get the bounds
        int midrow = rows / 2;
        int midcol = cols / 2;
        int midrowb = rows - midrow;
        int midcolb = cols - midcol;
        

        matrices[0] = new int[midrow][midcol];
        matrices[1] = new int[midrow][midcolb];
        matrices[2] = new int[midrowb][midcol];
        matrices[3] = new int[midrowb][midcolb];

        for (int i = 0; i < midrow; i++) {
            matrices[0][i] = Arrays.copyOfRange(matrix[i], 0, midcol);
            matrices[1][i] = Arrays.copyOfRange(matrix[i], midcol, cols);
        }

        if (midcol < matrix[0].length) {    //  THIS SHOULD STOP IT
            for (int i = midrow; i < rows; i++) {
                //  WTF !!
                System.out.println(midrow + " " + midcol + " " + matrix[i].length);
                matrices[2][i - midrow] = 
                        Arrays.copyOfRange(matrix[i], 0, midcol);
                matrices[3][i - midrow] =
                        Arrays.copyOfRange(matrix[i], midcol, cols);
            }
        }
        
        return matrices;
    }
    
    private static int[][] join4Matrices (int[][] a, int[][] b, int[][] c, int[][] d) {
        
        //  Get the bounds of the matrices
        int rows = a.length + c.length;
        int alen = (a.length == 0) ? 0 : a[0].length;
        int blen = (b.length == 0) ? 0 : b[0].length;
        int clen = (c.length == 0) ? 0 : c[0].length;
        int dlen = (d.length == 0) ? 0 : d[0].length;
        int[][] matrix = new int[rows][alen + blen];


        for (int i = 0; i < a.length; i++) {
            matrix[i] = new int[alen + blen];
            if (0 < alen) {
                System.arraycopy(a[i], 0, matrix[i], 0, alen);
            }
            if (0 < blen) {
                System.arraycopy(b[i], 0, matrix[i], alen, blen);
            }
        }
        for (int i = 0, j = i + alen; i < c.length; i++, j++) {
            matrix[j] = new int[clen + dlen];
            if (0 < clen) {
                System.arraycopy(c[i], 0, matrix[j], 0, clen);
            }
            if (0 < dlen) {
                System.arraycopy(d[i], 0, matrix[j], clen, dlen);
            }
        }

        return matrix;
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
        long[] times1 = new long[RUNS];
        long[] times2 = new long[RUNS];

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
                times1[i] += end1 - start1;

                // Time the second method
                long start2 = System.currentTimeMillis();
                multiplyNaiveParallel(matrixA, matrixB);
                long end2 = System.currentTimeMillis();
                times2[i] += end2 - start2;
            }

            // Find the average time per trial
            times1[i] /= TRIALS_PER_RUN;
            times2[i] /= TRIALS_PER_RUN;
        }

        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write("Size," + csvify(sizes) + "\n" + "Naive," + csvify(times1) + "\n" + "NaiveParallel," + csvify(times2));
            writer.close();
            System.out.println("\nWrote to " + outputFile);
        } catch (IOException e) {
            System.out.println("Failed to write to " + outputFile + ". Writing to console instead.\n");
            System.out.println("Sizes per run:  " + csvify(sizes));
            System.out.println("Method 1 times: " + csvify(times1));
            System.out.println("Method 2 times: " + csvify(times2));
        }
    }

    private static String csvify(long[] arr) {
        String str = "";
        for (int i = 0; i < arr.length - 1; i++) {
            str += ("" + arr[i] + ",");
        }
        str += "" + arr[arr.length - 1];
        return str;
    }

    private static String csvify(int[] arr) {
        String str = "";
        for (int i = 0; i < arr.length - 1; i++) {
            str += ("" + arr[i] + ",");
        }
        str += "" + arr[arr.length - 1];
        return str;
    }
    
    public static String nameFile() {
        return RUNS + "-runs__" + INITIAL_SIZE + "-initial__" + SIZE_INCREMENT_PER_RUN + "-increment__"
                + DEPTH_OF_PARALLEL_RECURSION + "-parallel-depth.csv";
    }

    public static void main(String[] args) {
//        runTrials(nameFile());

        // Test environment first
        int[][] matrixA = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        int[][] matrixB = generateMatrix(INITIAL_SIZE, MINIMUM, MAXIMUM);
        printMatrix(matrixA);
        printMatrix(matrixB);
        printMatrix(multiplyNaive(matrixA, matrixB));
//        //  TODO
        printMatrix(multiplyDivideAndConquer(matrixA, matrixB));
    }
}
