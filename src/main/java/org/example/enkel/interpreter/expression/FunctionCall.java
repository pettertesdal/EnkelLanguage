package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

import java.util.List;

public class FunctionCall extends ASTObject {
    public String name;
    public List<ASTObject> arguments;
    public FunctionCall(String name, List<ASTObject> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<ASTObject> getArguments() {
        return arguments;
    }
}
