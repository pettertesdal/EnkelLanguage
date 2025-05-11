package org.example.enkel.interpreter.expression;

import org.example.enkel.interpreter.ASTObject;

import java.lang.String;

import static java.lang.String.valueOf;

public class NumberLiteral extends ASTObject {
    int number;
    public NumberLiteral(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return valueOf(number);
    }
    public int getVal() {
        return number;
    };

    public int getValue() {
        return number;
    };
}
