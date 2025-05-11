package org.example.enkel.interpreter.statement;


import org.example.enkel.interpreter.ASTObject;

public class Assignment extends ASTObject {
    public String id;
    public ASTObject value;
    public Boolean isNew;
    public boolean hoisted;

    public Assignment(String id, ASTObject value, Boolean isNew, boolean hoisted) {
        this.id = id;
        this.value = value;
        this.isNew = isNew;
        this.hoisted = hoisted;
    }

    public String getVarName() {
        return this.id;
    }

    public ASTObject getExpression() {
        return this.value;
    }

    public Boolean isNew() {
        return this.isNew;
    }

    public boolean isHoisted() {
        return this.hoisted;
    }
}
