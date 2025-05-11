package org.example.enkel.interpreter.statement;

import org.example.enkel.interpreter.ASTObject;
import org.example.enkel.interpreter.SymbolTable;

import org.example.enkel.interpreter.expression.Variable;

import java.util.List;

public class FunctionDef extends ASTObject {
    private String name;
    private List<ASTObject> body;
    private SymbolTable parameters;
    private String returnType;

    public FunctionDef(String name, String returnType, SymbolTable parameters, List<ASTObject> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }
    public String getName() {
        return name;
    }
    public SymbolTable getParameters() {
        return parameters;
    }
    public List<ASTObject> getBody() {
        return body;
    }
}
