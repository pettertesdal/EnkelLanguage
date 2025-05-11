package org.example.enkel.interpreter.statement;

import org.example.enkel.interpreter.ASTObject;
import org.example.enkel.interpreter.SymbolTable;

// Base abstract class for read statements
public class ReadExpression extends ASTObject {
    private String type;
    public ReadExpression(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}