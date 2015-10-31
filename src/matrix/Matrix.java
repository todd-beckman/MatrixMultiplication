package matrix;

import java.util.Arrays;

public class Matrix {
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
     * Multiplies two matrices together using the naive approach.
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
    public static void main(String[] args) {
        int SIZE = 5;
        int MINIMUM = 0;
        int MAXIMUM = 10;
        
        //  Generate two matrices
        int[][] matrix1 = generateMatrix(SIZE, MINIMUM, MAXIMUM);
        int[][] matrix2 = generateMatrix(SIZE, MINIMUM, MAXIMUM);
        printMatrix(matrix1);
        printMatrix(matrix2);
        
        int[][] result = multiplyNaive(copyMatrix(matrix1), copyMatrix(matrix2));        
        //int[][] result = multiplyStrassen(copyMatrix(matrix1), copyMatrix(matrix2));        
        
        printMatrix(result);
    }
}
