package org.example.enkel.compiler;

import org.example.enkel.interpreter.ASTObject;
import org.example.enkel.interpreter.Program;
import org.example.enkel.interpreter.SymbolTable;
import org.example.enkel.interpreter.expression.*;
import org.example.enkel.interpreter.statement.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import java.util.*;

public class LLVMCodeVisitor {
    private LLVMCodeGenerator codeGen;
    private Map<String, LLVMBasicBlockRef> namedBlocks = new HashMap<>();

    public LLVMCodeVisitor(String moduleName) {
        // Initialize the code generator
        codeGen = new LLVMCodeGenerator(moduleName);
    }

    // Top level program
    public void visit(Program program) {
        // Visit each statement in the program
        for (ASTObject statement : program.ASTObjects) {
            visitStatement(statement);
        }
    }

    // Dispatch to the right visitor method based on the type
    public void visitStatement(ASTObject statement) {
        if (statement instanceof PrintStatement) {
            visit((PrintStatement) statement);
        }
    }

    public void visit(PrintStatement printStatement) {
        // Get the expression to print
        LLVMValueRef valueToPrint = visitExpression(printStatement.getExpression());

        // Generate print instruction based on type
        // For simplicity, we'll assume all expressions are integers
        codeGen.printInt(valueToPrint);
    }


    // Expression evaluator, returns LLVM values
    public LLVMValueRef visitExpression(ASTObject expression) {

        if (expression instanceof NumberLiteral) {
            return codeGen.constInt(((NumberLiteral) expression).getValue());
        } else if (expression instanceof Addition) {
            return visitAddition((Addition) expression);
        }

        // Handle default or error case
        return null;
    }

    public void saveModuleToFile(String filename) {
        codeGen.saveModuleToFile(filename);
    }

    public void dumpIR() {
        codeGen.dumpModule();
    }

    // Helper for binary operations
    private LLVMValueRef visitAddition(Addition operation) {
        LLVMValueRef left = visitExpression(operation.getLeft());
        LLVMValueRef right = visitExpression(operation.getRight());

        // Create the appropriate instruction based on the opcode
        return codeGen.add(left, right);
    }
}

    /*
    // Specific visitors for each AST node type

    public void visit(Assignment assignment) {
        String varName = assignment.getVariable().getName();
        LLVMValueRef value = visitExpression(assignment.getValue());

        // Store or create a variable based on whether it already exists
        codeGen.storeVariable(varName, value);
    }

    public LLVMValueRef visitReadExpression(ReadExpression readExpr) {
        // Implement code to call a runtime function for input
        return codeGen.createReadCall();
    }

    public LLVMValueRef visit(Variable variable) {
        // Load the variable value from memory
        return codeGen.loadVariable(variable.getName());
    }

    public LLVMValueRef visit(Value value) {
        // Create constant based on the type
        if (value.isNumber()) {
            return codeGen.createIntConstant((int) value.getValue());
        } else {
            return codeGen.createStringConstant((String) value.getValue());
        }
    }

    public LLVMValueRef visitComparison(ComparableBool comparison) {
        LLVMValueRef left = visitExpression(comparison.getLeft());
        LLVMValueRef right = visitExpression(comparison.getRight());
        String operator = comparison.getOperator();

        // Map the operator string to LLVM predicate
        int predicate = mapOperatorToPredicate(operator);
        return codeGen.createComparison(left, right, predicate);
    }

    public LLVMValueRef visitFunctionCall(FunctionCall functionCall) {
        String functionName = functionCall.getName();
        List<ASTObject> arguments = functionCall.getArguments();
        return null;
    }

    private int mapOperatorToPredicate(String operator) {
        switch (operator) {
            case "==": return LLVMIntEQ;
            case "!=": return LLVMIntNE;
            case "<": return LLVMIntSLT;
            case "<=": return LLVMIntSLE;
            case ">": return LLVMIntSGT;
            case ">=": return LLVMIntSGE;
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    // Additional visitors for function definitions, conditional blocks, loops, etc.
    // ...

    // Utility methods for compilation and execution
    public void compile(String outputFile) {
        codeGen.compileToObjectFile(outputFile);
    }

    public void dumpIR() {
        codeGen.dumpModule();
    }

    public int execute() {
        return codeGen.executeJIT();
    }

    public void dispose() {
        codeGen.dispose();
    }
}
*/