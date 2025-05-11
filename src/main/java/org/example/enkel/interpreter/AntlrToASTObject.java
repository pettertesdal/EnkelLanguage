package org.example.enkel.interpreter;

import org.antlr.v4.runtime.Token;
import org.example.EnkelBaseVisitor;
import org.example.EnkelParser;
import org.example.enkel.interpreter.expression.*;
import org.example.enkel.interpreter.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

public class AntlrToASTObject extends EnkelBaseVisitor<ASTObject> {
    private SymbolTable symbolTable;
    private List<String> semanticErrors;

    public AntlrToASTObject(List<String> semanticErrors) {
        this.symbolTable = new SymbolTable();
        this.semanticErrors = semanticErrors;
    }

    @Override
    public ASTObject visitPrintStatement(EnkelParser.PrintStatementContext ctx) {
        ASTObject expression = this.visit(ctx.expression());

        System.out.println("visitPrintStatement:");
        return new PrintStatement(expression);
    }

    @Override
    public ASTObject visitReadExpression(EnkelParser.ReadExpressionContext ctx) {
        System.out.println("visitReadExpression:");
        if (ctx.TYPENUMBER() != null) {
            return new ReadExpression("INTEGER");
        } else if (ctx.TYPESTRING() != null) {
            return new ReadExpression("STRING");
        } else if (ctx.TYPEBOOL() != null) {
            return new ReadExpression("BOOLEAN");
        } else {
            return new ReadExpression("STRING");
        }
    }

    @Override
    public ASTObject visitAssignment(EnkelParser.AssignmentContext ctx) {
        // Extract the variable name from the context
        String varName = ctx.IDENT().getText();
        Token idToken = ctx.IDENT().getSymbol();
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        // Visit the expression to get its value
        ASTObject valueObj = visit(ctx.expression());

        // Check if NEW token exists in the context
        boolean isNewVariable = ctx.NEW() != null;
        boolean hoisted = ctx.HOIST() != null;
        System.out.println("visit assignment:");

        if (!isNewVariable) {
            // This is a declaration with NEW keyword
            if (!symbolTable.isDeclared(varName)) {
                symbolTable.declareString(varName, "", hoisted);
            } else {
                semanticErrors.add("Variable '" + varName + "' already declared two times");
            }
        } else {
            // This is just an update to an existing variable
            if (symbolTable.isDeclared(varName)) {
            }
        }

        // Return an Assignment object representing this operation
        return new Assignment(varName, valueObj, isNewVariable, hoisted);
    }

    @Override
    public ASTObject visitVariable(EnkelParser.VariableContext ctx) {
        // Extract the variable name
        System.out.println("visitVariable:");
        String varName = ctx.IDENT().getText();
        Token idToken = ctx.IDENT().getSymbol();
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        // Check if the variable exists in the symbol table
        return new Variable(varName);
    }

    @Override
    public ASTObject visitFunctionDef(EnkelParser.FunctionDefContext ctx) {
        // Extract function name and parameters
        String functionName = ctx.IDENT().getText();
        SymbolTable parameters = new SymbolTable();

        System.out.println("visit FunctionDef");
        // Extract parameters if they exist
        if (ctx.paramList() != null) {
            for (EnkelParser.ParamContext paramCtx : ctx.paramList().param()) {
                if (paramCtx.TYPENUMBER() != null) {
                        parameters.defineInteger(paramCtx.IDENT().getText());
                } else if (paramCtx.TYPESTRING() != null) {
                    parameters.defineString(paramCtx.IDENT().getText());
                } else if (paramCtx.TYPEBOOL() != null) {
                    parameters.defineBoolean(paramCtx.IDENT().getText());
                } else {
                    semanticErrors.add("Unsupported parameter type for '" + paramCtx.IDENT().getText() + "'");
                }
            }
        }

        String returnType = "";
        if (ctx.TYPEBOOL() != null) {
            returnType = "bool";
        }
        if (ctx.TYPESTRING() != null) {
            returnType = "string";
        }
        if (ctx.TYPENUMBER() != null) {
            returnType = "int";
        }

        // Visit function body statements
        List<ASTObject> bodyStatements = new ArrayList<>();
        for (EnkelParser.StatementContext stmtCtx : ctx.functionBody().statement()) {
            bodyStatements.add(this.visit(stmtCtx));
        }


        // Create function definition object
        FunctionDef functionDef = new FunctionDef(functionName, returnType, parameters, bodyStatements);

        // Add to symbol table
        System.out.println("Adding functionname to symbol table: " + functionName);
        symbolTable.declareFunction(functionName, functionDef, false);

        // Return the function definition object
        return functionDef;
    }

    @Override
    public ASTObject visitFunctionCall(EnkelParser.FunctionCallContext ctx) {
        String ident = ctx.IDENT().getText();
        Token idToken = ctx.IDENT().getSymbol();
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        System.out.println("visit FunctionCall");


        List<ASTObject> args = new ArrayList<>();

        if (ctx.expressionList() != null) {
            for (int i = 0; i < ctx.expressionList().getChildCount(); i++) {
                args.add(visit(ctx.expressionList().getChild(i)));
            }
        }

        return new FunctionCall(ident, args);
    }

    @Override
    public ASTObject visitConditional(EnkelParser.ConditionalContext ctx) {
        List<ConditionalPath> conditionalPaths = new ArrayList<>();

        System.out.println("visit Conditional");
        // Process the if statement
        ASTObject ifExpression = visit(ctx.ifCondition().expression());
        List<ASTObject> ifBody = new ArrayList<>();

        // Visit all statements in the if block
        for (EnkelParser.StatementContext stmtCtx : ctx.ifCondition().conditionalBody().statement()) {
            ifBody.add(visit(stmtCtx));
        }

        // Create and add the if path
        conditionalPaths.add(new ConditionalPath(ifExpression, ifBody));

        // Process any else-if statements
        for (int i = 0; i < ctx.ifElseCondition().size(); i++) {
            ASTObject elseIfExpression = visit(ctx.ifElseCondition(i).expression());
            List<ASTObject> elseIfBody = new ArrayList<>();

            // Visit all statements in this else-if block
            for (EnkelParser.StatementContext stmtCtx : ctx.ifElseCondition(i).conditionalBody().statement()) {
                elseIfBody.add(visit(stmtCtx));
            }

            // Create and add the else-if path
            conditionalPaths.add(new ConditionalPath(elseIfExpression, elseIfBody));
        }

        // Process the else statement if it exists
        if (ctx.elseCondition() != null) {
            // Else has no expression, so we pass null
            List<ASTObject> elseBody = new ArrayList<>();

            // Visit all statements in the else block
            for (EnkelParser.StatementContext stmtCtx : ctx.elseCondition().conditionalBody().statement()) {
                elseBody.add(visit(stmtCtx));
            }

            // Create and add the else path
            conditionalPaths.add(new ConditionalPath(new BooleanLiteral(true), elseBody));
        }

        return new Conditional(conditionalPaths);
    }

    @Override
    public ASTObject visitLoop(EnkelParser.LoopContext ctx) {
        System.out.println("visit Loop");

        boolean isInfinite = (ctx.STOP() != null);
        List<ASTObject> bodyStatements = new ArrayList<>();
        ASTObject times = visit(ctx.expression());

        for (EnkelParser.StatementContext stmtCtx : ctx.functionBody().statement()) {
            bodyStatements.add(this.visit(stmtCtx));
        }
        return new LoopStatement(bodyStatements, isInfinite, times);
    }

    @Override
    public ASTObject visitStop(EnkelParser.StopContext ctx) {
        return new StopStatement();
    }

    @Override
    public ASTObject visitAddition(EnkelParser.AdditionContext ctx) {
        Token operatorToken = ctx.getChild(1).getPayload() instanceof Token ?
                (Token) ctx.getChild(1).getPayload() :
                ctx.getStart();
        int line = operatorToken.getLine();
        int column = operatorToken.getCharPositionInLine() + 1;

        System.out.println("visit Addition");
        ASTObject left = visit(ctx.getChild(0));
        ASTObject right = visit(ctx.getChild(2));
        return new Addition(left, right);
    }

    @Override
    public ASTObject visitParanthesesExpression(EnkelParser.ParanthesesExpressionContext ctx) {
        System.out.println("visit ParanthesesExpression");
        return new ParenthesesExpression(visit(ctx.expression()));
    }

    @Override
    public ASTObject visitSubtraction(EnkelParser.SubtractionContext ctx) {
        System.out.println("visit Subtraction");
        Token operatorToken = ctx.getChild(1).getPayload() instanceof Token ?
                (Token) ctx.getChild(1).getPayload() :
                ctx.getStart();
        int line = operatorToken.getLine();
        int column = operatorToken.getCharPositionInLine() + 1;
        ASTObject left = visit(ctx.getChild(0));
        ASTObject right = visit(ctx.getChild(2));


        return new Subtraction(left, right);
    }

    @Override
    public ASTObject visitMultiplication(EnkelParser.MultiplicationContext ctx) {
        System.out.println("visit Multiplication");
        Token operatorToken = ctx.getChild(1).getPayload() instanceof Token ?
                (Token) ctx.getChild(1).getPayload() :
                ctx.getStart();
        int line = operatorToken.getLine();
        int column = operatorToken.getCharPositionInLine() + 1;
        ASTObject left = visit(ctx.getChild(0));
        ASTObject right = visit(ctx.getChild(2));

        if (!(left instanceof NumberLiteral) || !(right instanceof NumberLiteral)) {
            semanticErrors.add("Multiplication can only be performed on numbers. Line " + line + ", column " + column + ".");
            return new ErrorASTObject("Invalid multiplication operation");
        }

        return new Multiplication(left, right);
    }

    @Override
    public ASTObject visitDivisjon(EnkelParser.DivisjonContext ctx) {
        System.out.println("visit Divisjon");
        Token operatorToken = ctx.getChild(1).getPayload() instanceof Token ?
                (Token) ctx.getChild(1).getPayload() :
                ctx.getStart();
        int line = operatorToken.getLine();
        int column = operatorToken.getCharPositionInLine() + 1;
        ASTObject left = visit(ctx.getChild(0));
        ASTObject right = visit(ctx.getChild(2));

        if (!(left instanceof NumberLiteral) || !(right instanceof NumberLiteral)) {
            semanticErrors.add("Divisjon can only be performed on numbers. Line " + line + ", column " + column + ".");
            return new ErrorASTObject("Invalid divisjon operation");
        }

        return new Division(left, right);
    }

    @Override
    public ASTObject visitComparableBool(EnkelParser.ComparableBoolContext ctx) {
        System.out.println("visit ComparableBool");
        Token operatorToken = ctx.getChild(1).getPayload() instanceof Token ?
                (Token) ctx.getChild(1).getPayload() :
                ctx.getStart();
        int line = operatorToken.getLine();
        int column = operatorToken.getCharPositionInLine() + 1;
        ASTObject left = visit(ctx.getChild(0));
        ASTObject right = visit(ctx.getChild(2));

        return new ComparableBool(left, right, ctx.getChild(1).getText());
    }

    @Override
    public ASTObject visitReturnStatement(EnkelParser.ReturnStatementContext ctx) {
        System.out.println("visit ReturnStatement");
        ASTObject expression = this.visit(ctx.expression());
        return new ReturnStatement(expression);
    }

    @Override
    public ASTObject visitNumber(EnkelParser.NumberContext ctx) {
        System.out.println("visit Number");
        String numText = ctx.getChild(0).getText();
        int num = Integer.parseInt(numText);
        return new NumberLiteral(num);
    }

    @Override
    public ASTObject visitString(EnkelParser.StringContext ctx) {
        System.out.println("visit String");
        String text = ctx.getText();
        // Remove the quotes from the string literal
        String value = text.substring(1, text.length() - 1);
        return new StringLiteral(value);
    }
}
