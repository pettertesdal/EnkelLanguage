package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

public class Variable extends ASTObject {
    public String name;
    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
