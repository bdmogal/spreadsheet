package spreadsheet;

import java.util.Stack;

/**
 * Evaluate an expression
 */
public class ExpressionEvaluator {
    /**
     * enum representing permitted operators
     * an operator contains the string that represents it and information of whether it is unary or binary
     * also contains methods to evaluate an operator, provided an evaluation stack
     */
    static enum OPERATOR {
        ADDITION("+"),
        SUBTRACTION("-"),
        MULTIPLICATION("*"),
        DIVISION("/"),
        INCREMENT("++", true),
        DECREMENT("--", true);

        private String operator = null;
        private boolean isUnary;

        OPERATOR(String op) {
            this(op, false);
        }

        OPERATOR(String s, boolean unary) {
            operator = s;
            isUnary = unary;
        }

        protected String getOperator() {
            return operator;
        }

        /**
         * Check if OPERATOR is unary
         * @return true if operator is unary, false otherwise
         */
        protected boolean isUnary() {
            return isUnary;
        }

        static OPERATOR valueFrom(String operator) {
            if (operator != null) {
                for (OPERATOR op : OPERATOR.values()) {
                    if (operator.equalsIgnoreCase(op.operator)) {
                        return op;
                    }
                }
            }
            return null;
        }

        void evaluate(Stack<String> expStack) {
            if (isUnary()) {
                evaluateUnaryOperator(expStack);
            } else {
                evaluateBinaryOperator(expStack);
            }
        }

        /**
         * Evaluates a unary operator by popping the top of the stack
         * @param expressionStack the stack to use to evaluate
         */
        private void evaluateUnaryOperator(Stack<String> expressionStack) {
            double first = Double.valueOf(expressionStack.pop());
            switch (this) {
                case INCREMENT:
                    expressionStack.push(String.valueOf(first + 1));
                    break;
                case DECREMENT:
                    expressionStack.push(String.valueOf(first - 1));
                    break;
            }
        }

        /**
         * Evaluates a binary operator by popping the top two elements of the stack
         * @param expressionStack the stack to use to evaluate
         */
        private void evaluateBinaryOperator(Stack<String> expressionStack) {
            double first = Double.valueOf(expressionStack.pop());
            double second = Double.valueOf(expressionStack.pop());
            switch (this) {
                case ADDITION:
                    expressionStack.push(String.valueOf(first + second));
                    break;
                case SUBTRACTION:
                    expressionStack.push(String.valueOf(second - first));
                    break;
                case MULTIPLICATION:
                    expressionStack.push(String.valueOf(first * second));
                    break;
                case DIVISION:
                    expressionStack.push(String.valueOf(second / first));
                    break;
            }
        }
    }

    /**
     * Evaluates an RPN expression using a stack
     * @param expression the expression to evaluate
     * @return the evaluation result as a double
     */
    public static double evaluate(String expression) {
        String [] tokens = expression.split("\\s+");
        Stack<String> expressionStack = new Stack<String>();

        for (String token : tokens) {
            if (!SpreadSheetUtil.isOperator(token)) {
                expressionStack.push(token);
            }
            else {
                OPERATOR operator = OPERATOR.valueFrom(token);
                operator.evaluate(expressionStack);
            }
        }

        return Double.parseDouble(expressionStack.pop());
    }
}