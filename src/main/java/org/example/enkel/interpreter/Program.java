package org.example.enkel.interpreter;

import org.example.enkel.interpreter.ASTObject;

import java.util.ArrayList;
import java.util.List;

public class Program {
    public List<ASTObject> ASTObjects;
    public Program() {
        this.ASTObjects = new ArrayList<>();
    }
    public void addExpression(ASTObject ASTObject) {
        ASTObjects.add(ASTObject);
    }
}
