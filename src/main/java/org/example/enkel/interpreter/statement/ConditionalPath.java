package org.example.enkel.interpreter.statement;


import org.example.enkel.interpreter.ASTObject;

import java.util.List;

public class ConditionalPath extends ASTObject {
    private ASTObject expression;
    private List<ASTObject> body;

    public ConditionalPath(ASTObject expression, List<ASTObject> body) {
        this.expression = expression;
        this.body = body;

    }
    public ASTObject getExpression() {
        return expression;
    }
    public List<ASTObject> getBody() {
        return body;
    }

}
