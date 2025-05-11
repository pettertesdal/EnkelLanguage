package org.example.enkel.interpreter.statement;


import org.example.enkel.interpreter.ASTObject;

public class ReturnStatement extends ASTObject {
    public ASTObject statement;

    public ReturnStatement(ASTObject statement) {
        this.statement = statement;
    }

    public ASTObject getStatement() {
        return statement;
    }
}
