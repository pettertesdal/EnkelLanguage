package org.example.enkel.interpreter.processor;

import org.example.enkel.interpreter.ASTObject;
import org.example.enkel.interpreter.SymbolTable;
import org.example.enkel.interpreter.statement.*;
import org.example.enkel.interpreter.expression.*;

import java.util.*;
import java.util.function.Function;

public class ExpressionProcessor {
    private SymbolTable symbolTable = new SymbolTable();
    List<String> evaluations = new ArrayList<>();


    public ExpressionProcessor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Object process(ASTObject expression) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof NumberLiteral) {
            return ((NumberLiteral) expression).getValue();
        } else if (expression instanceof StringLiteral) {
            return ((StringLiteral) expression).getValue();
        } else if (expression instanceof BooleanLiteral) {
            return ((BooleanLiteral) expression).getValue();
        } else if (expression instanceof Variable) {
            String varName = ((Variable) expression).getName();
            Optional<SymbolTable.Type> type = symbolTable.getType(varName);

            if (type.isEmpty()) {
                throw new RuntimeException("Variable " + varName + " has not been initialized");
            } else if (type.get() == SymbolTable.Type.INTEGER) {
                return symbolTable.getValue(varName).get().asInteger();
            } else if (type.get() == SymbolTable.Type.BOOLEAN) {
                return symbolTable.getValue(varName).get().asBoolean();
            } else if (type.get() == SymbolTable.Type.STRING) {
                return symbolTable.getValue(varName).get().asString();
            } else if (type.get() == SymbolTable.Type.FUNCTION) {
                return symbolTable.getValue(varName).get().asFunction();
            } else {
                throw new RuntimeException("Unsupported variable type: " + type.getClass().getName());
            }
        } else if (expression instanceof Addition) {
            Addition addition = (Addition) expression;
            Object leftValue = process(addition.getLeft());
            Object rightValue = process(addition.getRight());

            if (leftValue instanceof Number && rightValue instanceof Number) {
                return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
            } else {
                // String concatenation
                return String.valueOf(leftValue) + String.valueOf(rightValue);
            }
        } else if (expression instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) expression;
            Object leftValue = process(subtraction.getLeft());
            Object rightValue = process(subtraction.getRight());

            if (leftValue instanceof Number && rightValue instanceof Number) {
                return ((Number) leftValue).doubleValue() - ((Number) rightValue).doubleValue();
            }
            throw new RuntimeException("Cannot perform subtraction on non-numeric values");
        } else if (expression instanceof Multiplication) {
            Multiplication multiplication = (Multiplication) expression;
            Object leftValue = process(multiplication.getLeft());
            Object rightValue = process(multiplication.getRight());

            if (leftValue instanceof Number && rightValue instanceof Number) {
                return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();
            }
            throw new RuntimeException("Cannot perform multiplication on non-numeric values");
        } else if (expression instanceof Division) {
            Division division = (Division) expression;
            Object leftValue = process(division.getLeft());
            Object rightValue = process(division.getRight());

            if (leftValue instanceof Number && rightValue instanceof Number) {
                double divisor = ((Number) rightValue).doubleValue();
                if (divisor == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return ((Number) leftValue).doubleValue() / divisor;
            }
            throw new RuntimeException("Cannot perform division on non-numeric values");
        } else if (expression instanceof ComparableBool) {
            ComparableBool comparableBool = (ComparableBool) expression;
            Object leftValue = process(comparableBool.getLeft());
            Object rightValue = process(comparableBool.getRight());
            String operator = comparableBool.getOperator();

            return evaluateComparison(leftValue, rightValue, operator);
        } else if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;
            String functionName = functionCall.getName();
            List<ASTObject> arguments = functionCall.getArguments();

            // Process each argument
            List<Object> evaluatedArgs = new ArrayList<>();
            for (ASTObject arg : arguments) {
                evaluatedArgs.add(process(arg));
            }

            return executeFunction(functionName, evaluatedArgs);
        } else if (expression instanceof ParenthesesExpression) {
            // For parentheses, simply evaluate the inner expression
            ParenthesesExpression parenthesesExpression = (ParenthesesExpression) expression;
            return process(parenthesesExpression.getExpression());
        } else if (expression instanceof ReadExpression) {
            // Handle read expression (assuming it's for input)
            ReadExpression readExpression = (ReadExpression) expression;
            if (Objects.equals(readExpression.getType(), "INTEGER")) {
                System.out.print(": ");
                return Integer.parseInt(System.console().readLine());
            } else if (Objects.equals(readExpression.getType(), "BOOLEAN")) {
                System.out.print(": ");
                return Boolean.parseBoolean(System.console().readLine());
            } else if (Objects.equals(readExpression.getType(), "STRING")) {
                System.out.print(": ");
                return System.console().readLine();
            }
            throw new RuntimeException("Unsupported read expression type: " + readExpression.getType());
        }

        throw new RuntimeException("Unsupported expression type: " + expression.getClass().getName());

    }

    /**
     * Evaluate boolean comparisons
     */
    private boolean evaluateComparison(Object left, Object right, String operator) {
        if (left instanceof Number && right instanceof Number) {
            double leftNum = ((Number) left).doubleValue();
            double rightNum = ((Number) right).doubleValue();

            switch (operator) {
                case "==": return leftNum == rightNum;
                case "!=": return leftNum != rightNum;
                case "<": return leftNum < rightNum;
                case "<=": return leftNum <= rightNum;
                case ">": return leftNum > rightNum;
                case ">=": return leftNum >= rightNum;
                default: throw new RuntimeException("Unknown operator: " + operator);
            }
        } else {
            // String comparison or other types
            String leftStr = String.valueOf(left);
            String rightStr = String.valueOf(right);

            switch (operator) {
                case "==": return leftStr.equals(rightStr);
                case "!=": return !leftStr.equals(rightStr);
                default: throw new RuntimeException("Operator " + operator + " not supported for non-numeric values");
            }
        }
    }

    /**
     * Execute a function with the given name and arguments
     */
    private Object executeFunction(String name, List<Object> args) {
        // Get function definition from symbol table
        FunctionDef function = symbolTable.getValue(name).get().asFunction();

        System.out.println("Executing function " + name + " with arguments: " + args + " ...");
        // Create a new scope for function execution
        SymbolTable functionScope = new SymbolTable();
        functionScope.setParent(symbolTable);

        // Get parameter information from function definition
        SymbolTable parameterTable = function.getParameters();

        // Get parameter names from the parameter table
        List<String> parameterNames = parameterTable.getIDs();

        // Verify correct number of arguments
        if (parameterNames.size() != args.size()) {
            throw new RuntimeException("Function " + name + " expects " +
                    parameterNames.size() + " arguments but got " + args.size());
        }

        // Bind arguments to parameters
        for (int i = 0; i < parameterNames.size(); i++) {
            String paramName = parameterNames.get(i);
            Object argValue = args.get(i);

            // Set parameter value based on its type
            Optional<SymbolTable.Type> paramType = parameterTable.getType(paramName);
            if (paramType.isPresent()) {
                switch (paramType.get()) {
                    case INTEGER:
                        if (argValue instanceof Number) {
                            functionScope.declareInteger(paramName, ((Number) argValue).intValue(), false);
                        } else {
                            throw new RuntimeException("Type mismatch: parameter " + paramName +
                                    " expects INTEGER but got " + argValue.getClass().getName());
                        }
                        break;
                    case BOOLEAN:
                        if (argValue instanceof Boolean) {
                            functionScope.declareBoolean(paramName, (Boolean) argValue, false);
                        } else {
                            throw new RuntimeException("Type mismatch: parameter " + paramName +
                                    " expects BOOLEAN but got " + argValue.getClass().getName());
                        }
                        break;
                    case STRING:
                        if (argValue instanceof String) {
                            functionScope.declareString(paramName, (String) argValue, false);
                        } else {
                            throw new RuntimeException("Type mismatch: parameter " + paramName +
                                    " expects STRING but got " + argValue.getClass().getName());
                        }
                        break;
                    case FUNCTION:
                        if (argValue instanceof FunctionDef) {
                            functionScope.declareFunction(paramName, (FunctionDef) argValue, false);
                        } else {
                            throw new RuntimeException("Type mismatch: parameter " + paramName +
                                    " expects FUNCTION but got " + argValue.getClass().getName());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unsupported parameter type: " + paramType.get());
                }
            } else {
                throw new RuntimeException("Parameter type not specified: " + paramName);
            }
        }

        // Execute function body with new scope
        StatementProcessor statementProcessor = new StatementProcessor(function.getBody(), functionScope);

        Object result = statementProcessor.process();
        evaluations.addAll(statementProcessor.getEvaluations());
        if (statementProcessor.getSymbolTable() != null && statementProcessor.getSymbolTable().getHoisted() != null) {
            this.symbolTable.merge(statementProcessor.getSymbolTable().getHoisted());
        }


        return result;
    }


    public List<String> getEvaluations() {
        return evaluations;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

}
