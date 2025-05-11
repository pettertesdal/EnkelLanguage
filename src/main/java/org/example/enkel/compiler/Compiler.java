package org.example.enkel.compiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.example.EnkelLexer;
import org.example.EnkelParser;
import org.example.enkel.interpreter.AntlrToProgram;
import org.example.enkel.interpreter.Program;
import org.example.enkel.interpreter.SymbolTable;
import org.example.enkel.interpreter.app.EnkelREPL;

import java.io.IOException;
import java.nio.file.Path;

public class Compiler {
    private final Path sourcePath;
    private final SymbolTable symbolTable;
    private final LLVMCodeVisitor visitor;

    public Compiler(Path sourcePath) {
        this.sourcePath = sourcePath;
        this.symbolTable = new SymbolTable();
        this.visitor = new LLVMCodeVisitor(sourcePath.getFileName().toString());
    }

    public void compile() {
        // 1. Parse the source file to get AST
        EnkelParser parser = getParser(sourcePath.toString());
        ParseTree antlrTree = parser.program();

        AntlrToProgram programVisitor = new AntlrToProgram();

        Program program = programVisitor.visit(antlrTree);


        // 2. Visit the AST to generate LLVM IR
        visitor.visit(program);

        // 3. Output IR to file
        String llFile = sourcePath.toString().replaceFirst("[.][^.]+$", ".ll");
        visitor.saveModuleToFile(llFile);

        // 4. Call clang to compile the IR
        String outputFile = sourcePath.toString().replaceFirst("[.][^.]+$", "");
        compileWithClang(llFile, outputFile);
    }

    private void compileWithClang(String inputFile, String outputFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("clang", inputFile, "-o", outputFile);
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Clang compilation failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile with clang", e);
        }
    }

    public void dumpIR() {
        visitor.dumpIR();
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            String fileName = args[0];
            Path sourcePath = Path.of(fileName);
            Compiler compiler = new Compiler(sourcePath);
            compiler.compile();
        } else {
            System.err.println("Wrong arguments provided");
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