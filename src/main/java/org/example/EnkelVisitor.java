// Generated from org/example/Enkel.g4 by ANTLR 4.13.1
package org.example;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EnkelParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EnkelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EnkelParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(EnkelParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#functionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDef(EnkelParser.FunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(EnkelParser.FunctionBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(EnkelParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(EnkelParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#stop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStop(EnkelParser.StopContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(EnkelParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(EnkelParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(EnkelParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop(EnkelParser.LoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(EnkelParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(EnkelParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplication(EnkelParser.MultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddition(EnkelParser.AdditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(EnkelParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Readexpression}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadexpression(EnkelParser.ReadexpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Subtraction}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtraction(EnkelParser.SubtractionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(EnkelParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Divisjon}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDivisjon(EnkelParser.DivisjonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code String}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(EnkelParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParanthesesExpression}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParanthesesExpression(EnkelParser.ParanthesesExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Functioncall}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctioncall(EnkelParser.FunctioncallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComparableBool}
	 * labeled alternative in {@link EnkelParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparableBool(EnkelParser.ComparableBoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#conditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(EnkelParser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#ifCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfCondition(EnkelParser.IfConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#ifElseCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElseCondition(EnkelParser.IfElseConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#elseCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseCondition(EnkelParser.ElseConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#conditionalBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalBody(EnkelParser.ConditionalBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#printStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStatement(EnkelParser.PrintStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#readExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadExpression(EnkelParser.ReadExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link EnkelParser#comparable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparable(EnkelParser.ComparableContext ctx);
}