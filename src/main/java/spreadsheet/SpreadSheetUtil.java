package spreadsheet;

/**
 * Utility methods for SpreadSheet evaluator
 */
public class SpreadSheetUtil {

    protected static final String WHITESPACE = "\\s+";

    /**
     * Check if a token is a number
     * @param token the token to check
     * @return true if the token is a number, false otherwise
     */
    protected static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a token is an operator
     * @param token the token to check
     * @return true if the token is an operator, false otherwise
     */
    protected static boolean isOperator(String token) {
        return ExpressionEvaluator.OPERATOR.valueFrom(token) != null;
    }
}
