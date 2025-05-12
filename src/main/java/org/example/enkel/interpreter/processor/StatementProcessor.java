package org.example.enkel.interpreter.processor;

import org.example.enkel.interpreter.expression.*;
import org.example.enkel.interpreter.statement.*;
import org.example.enkel.interpreter.ASTObject;
import java.util.ArrayList;
import java.util.List;
import org.example.enkel.interpreter.SymbolTable;

public class StatementProcessor {
    List<ASTObject> list;
    private SymbolTable symbolTable = new SymbolTable();
    List<String> evaluations = new ArrayList<>();

    public StatementProcessor(List<ASTObject> list) {
        this.list = list;
    }
    public StatementProcessor(List<ASTObject> list, SymbolTable symbolTable) {
        this.list = list;
        this.symbolTable = symbolTable;
    }
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public Object process() {

        for (ASTObject astObject : list) {
            if (astObject instanceof PrintStatement) {
                ASTObject expression = ((PrintStatement) astObject).getExpression();
                ExpressionProcessor ep = new ExpressionProcessor(this.symbolTable);



                String processedExpression = ep.process(expression).toString();

                if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                    this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                }
                System.out.println(processedExpression);
            } else if (astObject instanceof Assignment) {
                Assignment assignment = (Assignment) astObject;
                String varName = assignment.getVarName();
                ASTObject valueExpression = assignment.getExpression();

                ExpressionProcessor ep = new ExpressionProcessor(this.symbolTable);
                Object value = ep.process(valueExpression);

                boolean isNew = assignment.isNew();
                boolean isHoisted = assignment.isHoisted();
                if (!isNew) {
                    // This is not a new variable declaration
                    if (value instanceof Integer || value instanceof Double) {
                        symbolTable.declareInteger(varName, ((Number) value).intValue(), isHoisted);
                    } else if (value instanceof Boolean) {
                        symbolTable.declareBoolean(varName, (Boolean) value, isHoisted);
                    } else if (value instanceof String) {
                        symbolTable.declareString(varName, (String) value, isHoisted);
                    } else {
                        throw new RuntimeException("Unsupported value type for variable declaration: " + value.getClass().getName());
                    }
                } else {
                    // This is an update to an existing variable
                    if (value instanceof Integer || value instanceof Double) {
                        symbolTable.setValue(varName, SymbolTable.Value.ofInteger(((Number) value).intValue()), isHoisted);
                    } else if (value instanceof Boolean) {
                        symbolTable.setValue(varName, SymbolTable.Value.ofBoolean((Boolean) value), isHoisted);
                    } else if (value instanceof String) {
                        symbolTable.setValue(varName, SymbolTable.Value.ofString((String) value), isHoisted);
                    } else {
                        throw new RuntimeException("Unsupported value type for variable update: " + value.getClass().getName());
                    }
                }
                if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                    this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                }
            } else if (astObject instanceof FunctionDef) {
                FunctionDef functionDef = (FunctionDef) astObject;
                String functionName = functionDef.getName();

                symbolTable.declareFunction(functionName, functionDef, false);
            } else if (astObject instanceof ReturnStatement) {
                ExpressionProcessor ep = new ExpressionProcessor(this.symbolTable);

                Object result = ep.process(((ReturnStatement) astObject).statement);

                if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                    this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                }

                this.evaluations.addAll(ep.getEvaluations());
                return result;
            } else if (astObject instanceof Conditional) {
                Conditional conditional = (Conditional) astObject;
                ExpressionProcessor ep = new ExpressionProcessor(this.symbolTable);

                List<ConditionalPath> conditionalPaths = conditional.getBody();
                for (ConditionalPath path : conditionalPaths) {
                    Object result = ep.process(path.getExpression());
                    if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                        this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                    }
                    evaluations.addAll(ep.getEvaluations());
                    if (result instanceof Boolean && (Boolean) result) {
                        // Create a new StatementProcessor for the body of this path
                        StatementProcessor pathProcessor = new StatementProcessor(path.getBody(), this.symbolTable);
                        Object pathResult = pathProcessor.process();
                        if (pathProcessor.getSymbolTable() != null && pathProcessor.getSymbolTable().getHoisted() != null) {
                            this.symbolTable.merge(pathProcessor.getSymbolTable().getHoisted());
                        }

                        // Add the evaluations from the path processor to our evaluations
                        evaluations.addAll(pathProcessor.getEvaluations());

                        // if pathResult is not null then we know that if encountered a return
                        // that should be propagated
                        if (pathResult != null) {
                            return pathResult;
                        }
                        // If we executed a conditional path, we shouldn't check the other paths
                        break;
                    }
                }
            } else if (astObject instanceof LoopStatement) {
                LoopStatement loopStatement = (LoopStatement) astObject;
                ExpressionProcessor ep = new ExpressionProcessor(this.symbolTable);
                StatementProcessor pathProcessor = new StatementProcessor(loopStatement.getBody(), this.symbolTable);

                if (loopStatement.isInfinite()) {
                    while (true) {
                        Object pathResult = pathProcessor.process();
                        if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                            this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                        }
                        evaluations.addAll(pathProcessor.getEvaluations());
                        if (pathResult instanceof StopStatement) {
                            break;
                        }
                        if (pathResult != null) {
                            return pathResult;
                        }

                    }
                    
                } else if (ep.process(loopStatement.getTimesExpression()) instanceof Integer) {
                    int times = ((Number) ep.process(loopStatement.getTimesExpression())).intValue();
                    System.out.println("Executing loop " + times + " times ...");
                    if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                        this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                    }
                    for (int i = 0; i < times; i++) {
                        Object pathResult = pathProcessor.process();
                        if (ep.getSymbolTable() != null && ep.getSymbolTable().getHoisted() != null) {
                            this.symbolTable.merge(ep.getSymbolTable().getHoisted());
                        }

                        if (pathResult instanceof StopStatement) {
                            break;
                        }
                        if (pathResult != null) {
                            return pathResult;
                        }
                    }
                    evaluations.addAll(pathProcessor.getEvaluations());
                }

            } else if (astObject instanceof StopStatement) {
                return new StopStatement();
            }
        }

        return null;
    }
    public List<String> getEvaluations() {
        return this.evaluations;
    }
}

