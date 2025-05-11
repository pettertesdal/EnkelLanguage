package org.example.enkel.interpreter;

import org.example.EnkelBaseVisitor;
import org.example.EnkelParser;

import java.util.ArrayList;
import java.util.List;

public class AntlrToProgram extends EnkelBaseVisitor<Program> {

    public List<String> semanticErrors;

    public AntlrToProgram() {
        this.semanticErrors = new ArrayList<>();
    }

    @Override
    public Program visitProgram(EnkelParser.ProgramContext ctx) {
        Program program = new Program();
        semanticErrors = new ArrayList<>();

        // Helper visitor to make the subtree into an expression object
        AntlrToASTObject ASTVisitor = new AntlrToASTObject(semanticErrors);

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if(i == ctx.getChildCount()-1){
                // Last child of the start symbol prog is EOF
                // Do not visit this child, and convert it to an expression or statement
            } else {
                program.addExpression(ASTVisitor.visit(ctx.getChild(i)));
            }
        }
        return program;
    }
}
