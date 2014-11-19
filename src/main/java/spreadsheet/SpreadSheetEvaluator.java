package spreadsheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a spreadsheet.
 * Does so by making multiple passes over the spreadsheet.
 */
public class SpreadSheetEvaluator {
    Map<String, String> spreadSheet = null;
    List<String> toEvaluate = null;

    SpreadSheetEvaluator(Map<String, String> sheet) {
        spreadSheet = sheet;
        initialize();
    }

    private void initialize() {
        toEvaluate = new ArrayList<String>();
        for (String cell : spreadSheet.keySet()) {
            if (!isEvaluated(cell)) {
                toEvaluate.add(cell);
            }
        }
    }

    private boolean isEvaluated(String cell) {
        return SpreadSheetUtil.isNumber(spreadSheet.get(cell));
    }

    /**
     * An already evaluated cell can never be passed to this method
     * @param expression the expression to check for evaluation eligibility
     * @return
     */
    private boolean canEvaluate(String cell, String expression) throws IOException {
        int cannotEvaluate = 0;
        String [] tokens = expression.split(SpreadSheetUtil.WHITESPACE);
        for (int i = 0; i < tokens.length; i++) {
            if (SpreadSheetUtil.isNumber(tokens[i]) || SpreadSheetUtil.isOperator(tokens[i])) {
                continue;
            } else {
                if (!spreadSheet.containsKey(tokens[i])) {
                    throw new IOException("Cell " + cell + " contains a reference to cell " + tokens[i]
                    + " which is not defined in this spreadsheet." +
                            " Please make sure that all the tokens in your input RPN expressions are space-separated.");
                }
                else if (spreadSheet.containsKey(tokens[i]) && isEvaluated(tokens[i])) {
                    tokens[i] = spreadSheet.get(tokens[i]);
                    spreadSheet.put(cell, toExpression(tokens));
                }
                else {
                    cannotEvaluate++;
                }
            }
        }
        return cannotEvaluate == 0;
    }

    /**
     * Merges tokens back into a string representation
     * @param tokens the tokens to merge
     * @return the expression obtained by joining tokens with a space
     */
    private String toExpression(String[] tokens) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append(tokens[i]);
            if (i != tokens.length - 1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }

    /**
     * Evaluate an expression and return a Double formatted string with a precision of 5
     * @param expression the expression to evaluate
     * @return result of evaluation of the expression as a Double formatted string with a precision of 5
     */
    private String evaluate(String expression) {
        return String.format("%.5f", ExpressionEvaluator.evaluate(expression));
    }

    /**
     * Evaluate a spreadsheet in multiple passes
     * In each pass -
     * 1. It evaluates cells that can be evaluated (that either don't contain any references
     * or whose references have already been evaluated) in this pass.
     * 2. Updates the evaluated cells in the spreadsheet so they can be consumed in the next pass.
     * 3. In the next pass, cells that contain references to cells evaluated in the previous pass are evaluated.
     *
     * @throws IOException if 1. a cell in the spreadsheet contains a reference to an undefined cell; or
     * 2. there is a cyclic dependency in the cells in the spreadsheet
     */
    protected void evaluate() throws IOException {
        while (!toEvaluate.isEmpty()) {
            List<String> evaluatedInPass = new ArrayList<String>();
            for (String each : toEvaluate) {
                String expression = spreadSheet.get(each);
                if (canEvaluate(each, expression)) {
                    // reading again from spreadsheet because spreadsheet may have been modified in canEvaluate()
                    spreadSheet.put(each, evaluate(spreadSheet.get(each)));
                    evaluatedInPass.add(each);
                }
            }

            // a cyclic dependency exists in between cells if their values depend on each other.
            // in such a scenario, you cannot evaluate a single cell in some pass.
            // if this happens, signal the error and quit
            if (evaluatedInPass.size() == 0) {
                throw new IOException("Cannot evaluate this spreadsheet " +
                        "because of a dependency cycle between the cells - " +
                        cycleToString(toEvaluate));
            }

            toEvaluate.removeAll(evaluatedInPass);
        }
    }

    /**
     * Returns a list as a printable String
     * @param remaining the list to print
     * @return the list as a String
     */
    private String cycleToString(List<String> remaining) {
        StringBuffer buffer = new StringBuffer("[");
        for (int i = 0; i < remaining.size(); i++) {
            String cell = remaining.get(i);
            buffer.append(cell);
            buffer.append("(");
            buffer.append(spreadSheet.get(cell));
            buffer.append(")");
            if (i < remaining.size() - 1) {
                buffer.append(",");
            }
        }
        buffer.append("]");
        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> it = spreadSheet.values().iterator();
        while (it.hasNext()) {
            buffer.append(it.next());
            if (it.hasNext()) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
}
