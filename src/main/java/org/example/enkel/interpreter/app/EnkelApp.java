package org.example.enkel.interpreter.app;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.example.EnkelLexer;
import org.example.EnkelParser;
import org.example.enkel.interpreter.AntlrToProgram;
import org.example.enkel.interpreter.Program;
import org.example.enkel.interpreter.processor.StatementProcessor;
import org.example.enkel.compiler.Compiler;

import java.io.IOException;

public class EnkelApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            // No arguments, launch REPL
            EnkelREPL repl = new EnkelREPL();
            repl.start();
        } else if (args.length == 1 && args[0].equals("--build")) {
            // Build argument with no file specified
            System.err.println("Error: No input file specified for build operation");
            System.err.println("Usage: java -jar enkel-front.jar --build program.enk");
            System.exit(1);
        } else if (args.length == 1) {
            // One argument, assume it's a file to run
            String filename = args[0];
            runFile(filename);
        } else if (args.length == 2 && args[0].equals("--build")) {
            // Build argument with file specified
            String filename = args[1];
            buildFile(filename);
        } else {
            System.err.println("Usage: java -jar enkel-front.jar [program.enk]");
            System.err.println("       java -jar enkel-front.jar --build program.enk");
            System.err.println("       If no file is specified, interactive mode will be launched.");
            System.exit(1);
        }
    }

    private static void buildFile(String filename) {
        EnkelParser parser = getParser(filename);

        if (parser != null) {
            try {
                // Create a compiler instance and compile the file
                Compiler compiler = new Compiler(filename);
                compiler.compile();
                System.out.println("Successfully compiled: " + filename);
            } catch (Exception e) {
                System.err.println("Compilation failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void runFile(String filename) {
        EnkelParser parser = getParser(filename);

        if (parser != null) {
            ParseTree antlrTree = parser.program();
            // Create a visitor for converting the parsetree to a into Program/statements object
            AntlrToProgram programVisitor = new AntlrToProgram();

            Program program = programVisitor.visit(antlrTree);

            if (programVisitor.semanticErrors.isEmpty()) {
                StatementProcessor sp = new StatementProcessor(program.ASTObjects);
                sp.process();
                for (String evaluation : sp.getEvaluations()) {
                    System.out.println(evaluation);
                }
            } else {
                System.err.println("Semantic errors:");
                for (String error : programVisitor.semanticErrors) {
                    System.err.println(error);
                }
            }
        }
    }

    private static EnkelParser getParser(String filename) {
        EnkelParser parser = null;

        try {
            CharStream input = CharStreams.fromFileName(filename);
            EnkelLexer lexer = new EnkelLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new EnkelParser(tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parser;
    }
}