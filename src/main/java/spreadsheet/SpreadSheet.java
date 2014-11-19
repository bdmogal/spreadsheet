package spreadsheet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main Runner class for evaluating a spreadsheet
 */
public class SpreadSheet {
    static String inputSize = null;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Check if a cell can be evaluated without evaluating any other cells
     * @param tokens the tokens representing the expression in the cell
     * @return true if all the tokens are either numbers or operators, false otherwise
     */
    static boolean canEvaluate(String [] tokens) {
        for (String token : tokens) {
            // check if it is a number or an operator
            if (!SpreadSheetUtil.isNumber(token) && !SpreadSheetUtil.isOperator(token)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Read the spreadsheet from stdin
     * Also evaluate independent expressions that do not have any references to other cells while reading
     * @return a map representing the spreadsheet with independent cells evaluated
     */
    static Map<String, String> readAndProcessOnePass() {
        Scanner scanner = null;
        Map<String, String> results = null;
        try {
            scanner = new Scanner(System.in);
            String first = scanner.nextLine();
            if (null == first) {
                throw new IllegalArgumentException("Incorrect input. First line must be a non-null 'width height'");
            }
            String[] widthHeight = first.split(SpreadSheetUtil.WHITESPACE);
            if (widthHeight.length < 2) {
                throw new IllegalArgumentException("Incorrect width-height input");
            }
            int width = Integer.parseInt(widthHeight[0]);
            int height = Integer.parseInt(widthHeight[1]);

            results = new LinkedHashMap<String, String>();
            inputSize = first;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    String expression = scanner.nextLine();
                    if (canEvaluate(expression.split(SpreadSheetUtil.WHITESPACE))) {
                        double result = ExpressionEvaluator.evaluate(expression);
                        expression = String.format("%.5f", result);
                    }
                    results.put(getCellId(i, j + 1), expression);
                }
            }
        }
        finally {
            scanner.close();
        }

        return results;
    }

    /**
     * Get the id of a cell given its row and column
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the id of the row
     */
    private static String getCellId(int row, int col) {
        StringBuffer sb = new StringBuffer(String.valueOf(ALPHABET.charAt(row)));
        sb.append(col);
        return sb.toString();
    }

    public static void main(String [] args) {
        Map<String, String> results = readAndProcessOnePass();
        SpreadSheetEvaluator evaluator = new SpreadSheetEvaluator(results);
        try {
            evaluator.evaluate();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(-1);
        }
        System.out.println(inputSize);
        System.out.println(evaluator);
    }
}
