package org.example.enkel.interpreter.statement;



import org.example.enkel.interpreter.ASTObject;

import java.util.List;

public class Conditional extends ASTObject {
    private List<ConditionalPath> body;

    public Conditional(List<ConditionalPath> body) {
        this.body = body;
    }
    public List<ConditionalPath> getBody() {
        return body;
    }
}
