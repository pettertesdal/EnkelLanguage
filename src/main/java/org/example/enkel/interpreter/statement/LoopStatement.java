package org.example.enkel.interpreter.statement;

import org.example.enkel.interpreter.ASTObject;

import java.util.List;

public class LoopStatement extends ASTObject {
    List<ASTObject> body;
    boolean isInfinite;
    ASTObject times;

    public LoopStatement(List<ASTObject> body, boolean isInfinite, ASTObject times) {
        this.body = body;
        this.isInfinite = isInfinite;
        this.times = times;
    }
    public List<ASTObject> getBody() {
        return body;
    }
    public boolean isInfinite() {
        return isInfinite;
    }
    public ASTObject getTimesExpression() {
        return times;
    }
}