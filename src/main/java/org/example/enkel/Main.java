package org.example.enkel;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.example.EnkelLexer;
import org.example.EnkelParser;
import org.example.enkel.interpreter.EnkelVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar enkel-front.jar <program.enk>");
            System.exit(1);
        }

        try {
            String code = Files.readString(Paths.get(args[0]));

            // Lexical analysis
            EnkelLexer lexer = new EnkelLexer(CharStreams.fromString(code));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Parsing
            EnkelParser parser = new EnkelParser(tokens);
            EnkelParser.ProgramContext programContext = parser.program();

            // Interpretation
            EnkelVisitor visitor = new EnkelVisitor();
            visitor.visit(programContext);

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            System.exit(1);
        }
    }
}