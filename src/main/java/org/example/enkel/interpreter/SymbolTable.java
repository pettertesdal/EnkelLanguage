package org.example.enkel.interpreter;

import org.example.enkel.interpreter.expression.*;
import org.example.enkel.interpreter.statement.*;

import java.util.*;
import java.util.function.Function;

/**
 * Symbol table for the Enkel interpreter that can store variables of different types
 * (integers, booleans, and strings).
 */
public class SymbolTable {

    private SymbolTable parent = null;
    private SymbolTable hoisted = null;

    /**
     * Enum representing the different types of values the symbol table can store
     */
    public enum Type {
        INTEGER,
        BOOLEAN,
        STRING,
        FUNCTION
    }

    /**
     * A class to store a value along with its type
     */
    public static class Value {
        private final Object value;
        private final Type type;

        private Value(Object value, Type type) {
            this.value = value;
            this.type = type;
        }

        public static Value ofInteger(int value) {
            return new Value(value, Type.INTEGER);
        }

        public static Value ofBoolean(boolean value) {
            return new Value(value, Type.BOOLEAN);
        }

        public static Value ofString(String value) {
            return new Value(value, Type.STRING);
        }

        public static Value ofFunction(FunctionDef function) {return new Value(function, Type.FUNCTION);}

        public Type getType() {
            return type;
        }

        public int asInteger() {
            if (type != Type.INTEGER) {
                throw new RuntimeException("Cannot convert " + type + " to INTEGER");
            }
            return (Integer) value;
        }

        public boolean asBoolean() {
            if (type != Type.BOOLEAN) {
                throw new RuntimeException("Cannot convert " + type + " to BOOLEAN");
            }
            return (Boolean) value;
        }

        public String asString() {
            if (type != Type.STRING) {
                throw new RuntimeException("Cannot convert " + type + " to STRING");
            }
            return (String) value;
        }

        public FunctionDef asFunction() {
            if (type != Type.FUNCTION) {
                throw new RuntimeException("Cannot convert " + type + " to FUNCTION");
            }
            return (FunctionDef) value;
        }

        @Override
        public String toString() {
            if (type == Type.FUNCTION) {
                FunctionDef function = (FunctionDef) value;
                return "function " + function.getName() + "()";
            }
            return value.toString();
        }
    }

    private final Map<String, Value> symbols = new HashMap<>();

    /**
     * Declare an integer variable with the given name and value
     */
    public void declareInteger(String name, int value, boolean isHoisted) {
        symbols.put(name, Value.ofInteger(value));
        if (isHoisted) {
            if (hoisted == null) {
                hoisted = new SymbolTable();
            }
            hoisted.symbols.put(name, Value.ofInteger(value));
        }
    }

    /**
     * Declare a boolean variable with the given name and value
     */
    public void declareBoolean(String name, boolean value, boolean isHoisted) {
        symbols.put(name, Value.ofBoolean(value));
        if (isHoisted) {
            if (hoisted == null) {
                hoisted = new SymbolTable();
            }
            hoisted.symbols.put(name, Value.ofBoolean(value));
        }
    }

    /**
     * Declare a string variable with the given name and value
     */
    public void declareString(String name, String value, boolean isHoisted) {
        symbols.put(name, Value.ofString(value));
        if (isHoisted) {
            if (hoisted == null) {
                hoisted = new SymbolTable();
            }
            hoisted.symbols.put(name, Value.ofString(value));
        }
    }

    public void declareFunction(String name, FunctionDef function, boolean isHoisted) {
        symbols.put(name, Value.ofFunction(function));
        if (isHoisted) {
            if (hoisted == null) {
                hoisted = new SymbolTable();
            }
            hoisted.symbols.put(name, Value.ofFunction(function));
        }
    }

    /**
     * Declares an integer parameter with the given name in the current scope
     */
    public void defineInteger(String name) {
        symbols.put(name, Value.ofInteger(0)); // Default value that will be overwritten when called
    }

    /**
     * Declares a string parameter with the given name in the current scope
     */
    public void defineString(String name) {
        symbols.put(name, Value.ofString("")); // Default value that will be overwritten when called
    }

    /**
     * Declares a boolean parameter with the given name in the current scope
     */
    public void defineBoolean(String name) {
        symbols.put(name, Value.ofBoolean(false)); // Default value that will be overwritten when called
    }



    /**
     * Set the value of an existing variable
     * @throws RuntimeException if the variable doesn't exist or if the types don't match
     */
    public void setValue(String name, Value value, boolean isHoisted) {
        // Check if the variable exists in current table
        if (symbols.containsKey(name)) {
            Value currentValue = symbols.get(name);
            if (currentValue.getType() != value.getType()) {
                throw new RuntimeException("Cannot assign " + value.getType() +
                        " to variable of type " + currentValue.getType());
            }
        }
        // Check if the variable exists in parent table
        else if (parent != null && parent.symbols.containsKey(name)) {
            // Check type compatibility with parent's value
            Value parentValue = parent.symbols.get(name);
            if (parentValue.getType() != value.getType()) {
                throw new RuntimeException("Cannot assign " + value.getType() +
                        " to variable of type " + parentValue.getType());
            }
            // We're going to add it to the current symbol table
            // (no need to throw an exception as it exists in parent)
        }
        // Not found in either current or parent tables
        else {
            throw new RuntimeException("Variable " + name + " not declared");
        }

        // Handle hoisted case
        if (isHoisted) {
            if (hoisted == null) {
                hoisted = new SymbolTable();
            }
            if (hoisted.symbols.containsKey(name)) {
                hoisted.symbols.put(name, value);
            } else {
                if (value.getType() == Type.INTEGER) {
                    hoisted.declareInteger(name, value.asInteger(), false);
                } else if (value.getType() == Type.BOOLEAN) {
                    hoisted.declareBoolean(name, value.asBoolean(), false);
                } else if (value.getType() == Type.STRING) {
                    hoisted.declareString(name, value.asString(), false);
                } else if (value.getType() == Type.FUNCTION) {
                    hoisted.declareFunction(name, value.asFunction(), false);
                } else {
                    throw new RuntimeException("Unsupported type hoisted value: " + value.getType());
                }
            }
        }

        // Always update in the current symbol table
        symbols.put(name, value);
    }

    /**
     * Modified getValue method to look up the scope chain
     * If a variable is not found in this symbol table, it will look in the parent
     *
     * @param name The name of the variable to look up
     * @return Optional containing the value, or empty if the variable doesn't exist in any scope
     */
    public Optional<Value> getValue(String name) {
        if (symbols.containsKey(name)) {
            return Optional.of(symbols.get(name));
        } else if (parent != null) {
            // Look up the variable in the parent scope
            return parent.getValue(name);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets the names of all parameters/variables defined in this symbol table
     *
     * @return List of parameter/variable names
     */
    public List<String> getIDs() {
        return new ArrayList<>(symbols.keySet());
    }

    /**
     * Check if a variable exists in the symbol table
     */
    public boolean isDeclared(String name) {
        if (symbols.containsKey(name)) {
            return true;
        } else if (parent != null) {
            return parent.isDeclared(name);
        } else {
            return false;
        }
    }


    /**
     * Get the type of a variable
     * @return Optional containing the type, or empty if the variable doesn't exist
     */
    public Optional<Type> getType(String name) {
        if (symbols.containsKey(name)) {
            return Optional.of(symbols.get(name).getType());
        } else if (parent != null) {
            return parent.getType(name);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Determines if a variable is declared in the current scope (not parent scopes)
     * Useful for distinguishing between creating a new variable and updating an existing one
     *
     * @param name The name of the variable to check
     * @return true if the variable exists in the current scope
     */
    public boolean isDeclaredInCurrentScope(String name) {
        return symbols.containsKey(name);
    }



    /**
     * Clear all variables from the symbol table
     */
    public void clear() {
        symbols.clear();
    }

    /**
     * Gets the parameter name at the specified index
     * @param index The index of the parameter
     * @return The name of the parameter at the specified index, or null if index is out of bounds
     */
    public String getParameterNameByIndex(int index) {
        if (index < 0 || index >= symbols.size()) {
            return null;
        }

        // Convert the keyset to an array to access by index
        return (String) symbols.keySet().toArray()[index];
    }

    /**
     * Gets the number of symbols in this symbol table
     * @return The number of symbols
     */
    public int size() {
        return symbols.size();
    }

    /**
     * Checks if the provided ASTObject type matches the expected type
     * @param obj The ASTObject to check
     * @param expectedType The expected type
     * @return true if the types match, false otherwise
     */
    public static boolean typeMatches(ASTObject obj, Type expectedType) {
        if (obj instanceof NumberLiteral && expectedType == Type.INTEGER) {
            return true;
        } else if (obj instanceof StringLiteral && expectedType == Type.STRING) {
            return true;
        } else if (obj instanceof BooleanLiteral && expectedType == Type.BOOLEAN) {
            return true;
        }

        return false;
    }

    /**
     * Sets the parent symbol table for this symbol table
     * Used to implement nested scopes
     *
     * @param parent The parent symbol table
     */
    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Gets the parent symbol table
     *
     * @return The parent symbol table, or null if this is a global scope
     */
    public SymbolTable getParent() {
        return parent;
    }

    public void setHoisted(SymbolTable hoisted) {
        this.hoisted = hoisted;
    }

    public SymbolTable getHoisted() {
        if (this.hoisted == null) {
            return null;
        }
        for (String name: this.hoisted.getIDs()) {
            if (this.symbols.containsKey(name)) {
                hoisted.setValue(name, this.symbols.get(name), false);
            }
        }
        return hoisted;
    }

    /**
     * Merges another symbol table into this one.
     * <p>
     * For each symbol in the provided table:
     * - If it doesn't exist in the current table, it will be added
     * - If it exists and has the same type, its value will be updated
     * - If it exists but has a different type, a RuntimeException will be thrown
     *
     * @param other The symbol table to merge into this one
     * @throws RuntimeException if a symbol exists in both tables with different types
     */
    public void merge(SymbolTable other) {
        for (Map.Entry<String, Value> entry : other.symbols.entrySet()) {
            String name = entry.getKey();
            Value value = entry.getValue();

            Optional<Value> existingValue = this.getValue(name);

            if (existingValue.isPresent()) {
                // Symbol exists, check type compatibility
                if (existingValue.get().getType() != value.getType()) {
                    throw new RuntimeException("Cannot merge symbol '" + name +
                            "': Type mismatch. Existing type: " + existingValue.get().getType() +
                            ", New type: " + value.getType());
                }

                // Same type, update the value
                this.setValue(name, value, false);
            } else {
                // Symbol doesn't exist, add it directly to symbols map
                symbols.put(name, value);
            }
        }
    }

    public void updateHoisted(SymbolTable other) {
        for (Map.Entry<String, Value> entry : other.symbols.entrySet()) {
            String name = entry.getKey();
            Value value = entry.getValue();

            if (this.hoisted.symbols.containsKey(name)) {
                this.hoisted.setValue(name, value, false);
            } else {
                if (value.getType() == Type.INTEGER) {
                    this.hoisted.declareInteger(name, value.asInteger(), false);
                } else if (value.getType() == Type.BOOLEAN) {
                    this.hoisted.declareBoolean(name, value.asBoolean(), false);
                } else if (value.getType() == Type.STRING) {
                    this.hoisted.declareString(name, value.asString(), false);
                } else if (value.getType() == Type.FUNCTION) {
                    this.hoisted.declareFunction(name, value.asFunction(), false);
                } else {
                    throw new RuntimeException("Unsupported type hoisted value: " + value.getType());
                }
                this.hoisted.symbols.put(name, value);
            }
        }
    }


}