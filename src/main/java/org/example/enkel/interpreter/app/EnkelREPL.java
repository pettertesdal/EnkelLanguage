package org.example.enkel.interpreter.app;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.example.EnkelLexer;
import org.example.EnkelParser;
import org.example.enkel.interpreter.AntlrToProgram;
import org.example.enkel.interpreter.Program;
import org.example.enkel.interpreter.SymbolTable;
import org.example.enkel.interpreter.processor.StatementProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EnkelREPL {
    private final SymbolTable symbolTable;
    private static final String PROMPT = "enkel> ";
    private static final String MULTILINE_PROMPT = "... ";
    private boolean running = true;

    public EnkelREPL() {
        this.symbolTable = new SymbolTable();
    }

    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enkel REPL (Interactive Mode)");
        System.out.println("Type 'exit' or 'quit' to exit, 'help' for help.");

        while (running) {
            try {
                System.out.print(PROMPT);
                String line = reader.readLine();

                if (line == null || line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                    running = false;
                    continue;
                } else if (line.equalsIgnoreCase("help")) {
                    showHelp();
                    continue;
                } else if (line.trim().isEmpty()) {
                    continue;
                }


                // Handle multi-line input for blocks
                if (line.trim().endsWith(":")) {
                    line = line + "\n";
                    StringBuilder codeBlock = new StringBuilder(line);
                    String blockLine;
                    // Initialize with 1 since we've already found a colon
                    int blockCount = 1;

                    while (blockCount > 0) {
                        System.out.print(MULTILINE_PROMPT);
                        blockLine = reader.readLine();

                        if (blockLine == null) break;

                        // Add newline to the end of each line in the block
                        blockLine = blockLine + "\n";
                        codeBlock.append(blockLine);

                        // Count new colons and "slutt" keywords to track nesting
                        blockCount += countOccurrences(blockLine, ":") - countOccurrences(blockLine, "slutt");
                    }

                    line = codeBlock.toString();
                }
                line = line + "\n";


                // Process the input
                processInput(line);

            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    private int countOccurrences(String str, String substr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(substr, idx)) != -1) {
            count++;
            idx += substr.length();
        }
        return count;
    }

    private void processInput(String input) {
        try {
            // Parse the input
            CharStream charStream = CharStreams.fromString(input);
            EnkelLexer lexer = new EnkelLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            EnkelParser parser = new EnkelParser(tokens);

            // Use the program rule for consistency
            ParseTree tree = parser.program();

            // Convert to AST
            AntlrToProgram visitor = new AntlrToProgram();
            Program program = visitor.visit(tree);

            if (!visitor.semanticErrors.isEmpty()) {
                for (String error : visitor.semanticErrors) {
                    System.err.println("Error: " + error);
                }
                return;
            }

            // Execute statements
            StatementProcessor processor = new StatementProcessor(program.ASTObjects, symbolTable);
            processor.process();

            // Print evaluations
            List<String> evaluations = processor.getEvaluations();
            if (!evaluations.isEmpty()) {
                for (String evaluation : evaluations) {
                    System.out.println(evaluation);
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private int countChar(String str, char c) {
        return (int) str.chars().filter(ch -> ch == c).count();
    }

    private void showHelp() {
        System.out.println("Enkel REPL Help:");
        System.out.println("  - Type Enkel code to execute it");
        System.out.println("  - For multi-line blocks, start with a line ending in ':', and end them with 'slutt'");
        System.out.println("  - Variables and functions defined in one command are available in later commands");
        System.out.println("  - Commands:");
        System.out.println("    * exit, quit - Exit the REPL");
        System.out.println("    * help - Show this help");
    }
}