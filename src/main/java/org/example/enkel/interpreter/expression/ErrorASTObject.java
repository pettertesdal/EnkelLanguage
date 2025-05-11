package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

public class ErrorASTObject extends ASTObject {
    private String message;

    public ErrorASTObject(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error: " + message;
    }
}