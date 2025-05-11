package org.example.enkel.interpreter;

import org.example.EnkelBaseVisitor;
import org.example.EnkelParser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.Map;

public class EnkelVisitor extends EnkelBaseVisitor<Object> {
    private final Map<String, Integer> variables = new HashMap<>();

    @Override
    public Object visitProgram(EnkelParser.ProgramContext ctx) {


        return super.visitProgram(ctx);
    }

    @Override
    public Object visitFunctionDef(EnkelParser.FunctionDefContext ctx) {
        return super.visitFunctionDef(ctx);
    }
    @Override
    public Object visitReturnStatement(EnkelParser.ReturnStatementContext ctx) {
        return super.visitReturnStatement(ctx);
    }
    @Override
    public Object visitAssignment(EnkelParser.AssignmentContext ctx) {
        ParseTree child = ctx.getChild(1);
        return super.visitAssignment(ctx);
    }

    @Override
    public Object visitNumber(EnkelParser.NumberContext ctx) {
        return super.visitNumber(ctx);
    }
}
