package org.example.enkel.compiler;

import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

import java.util.HashMap;
import java.util.Map;

public class LLVMCodeGenerator {
    // Error handler
    private static final BytePointer error = new BytePointer();

    // LLVM components
    private LLVMContextRef context;
    private LLVMModuleRef module;
    private LLVMBuilderRef builder;
    private LLVMExecutionEngineRef engine;

    // Common types
    private LLVMTypeRef i32Type;
    private LLVMTypeRef i8Type;
    private LLVMTypeRef doubleType;
    private LLVMTypeRef voidType;

    // Function mapping
    private LLVMValueRef mainFunction;

    // Variable mapping
    private Map<String, LLVMValueRef> namedValues = new HashMap<>();

    public LLVMCodeGenerator(String moduleName) {
        // Initialize LLVM components
        LLVMInitializeNativeTarget();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();

        // Create module and context
        context = LLVMContextCreate();
        module = LLVMModuleCreateWithNameInContext(moduleName, context);
        builder = LLVMCreateBuilderInContext(context);

        // Initialize common types
        i32Type = LLVMInt32TypeInContext(context);
        i8Type = LLVMInt8TypeInContext(context);
        doubleType = LLVMDoubleTypeInContext(context);
        voidType = LLVMVoidTypeInContext(context);

        // Create main function
        createMainFunction();
    }

    private void createMainFunction() {
        // Create function type: int main(void)
        LLVMTypeRef mainType = LLVMFunctionType(i32Type, new PointerPointer<>(), 0, 0);

        // Add the function to our module
        mainFunction = LLVMAddFunction(module, "main", mainType);

        // Create entry block
        LLVMBasicBlockRef entryBlock = LLVMAppendBasicBlockInContext(context, mainFunction, "entry");
        LLVMPositionBuilderAtEnd(builder, entryBlock);
    }

    // Method to generate integer constant
    public LLVMValueRef constInt(int value) {
        return LLVMConstInt(i32Type, value, /* signExtend */ 0);
    }

    // Method to generate double constant
    public LLVMValueRef constDouble(double value) {
        return LLVMConstReal(doubleType, value);
    }

    // Method to generate string constant
    public LLVMValueRef constString(String value) {
        return LLVMBuildGlobalStringPtr(builder, value, "str");
    }

    // Method to create a variable
    public LLVMValueRef createVariable(String name, LLVMTypeRef type) {
        LLVMValueRef alloca = LLVMBuildAlloca(builder, type, name);
        namedValues.put(name, alloca);
        return alloca;
    }

    // Method to load a variable
    public LLVMValueRef loadVariable(String name) {
        LLVMValueRef alloca = namedValues.get(name);
        if (alloca == null) {
            throw new RuntimeException("Unknown variable name: " + name);
        }
        return LLVMBuildLoad2(builder, i32Type, alloca, name + "_val");
    }

    // Method to store a value to a variable
    public LLVMValueRef storeVariable(String name, LLVMValueRef value) {
        LLVMValueRef alloca = namedValues.get(name);
        if (alloca == null) {
            // First time - create the variable
            alloca = createVariable(name, i32Type);
        }
        return LLVMBuildStore(builder, value, alloca);
    }

    // Methods for arithmetic operations
    public LLVMValueRef add(LLVMValueRef lhs, LLVMValueRef rhs) {
        return LLVMBuildAdd(builder, lhs, rhs, "result = lhs + rhs");
    }

    public LLVMValueRef subtract(LLVMValueRef lhs, LLVMValueRef rhs) {
        return LLVMBuildSub(builder, lhs, rhs, "subtmp");
    }

    public LLVMValueRef multiply(LLVMValueRef lhs, LLVMValueRef rhs) {
        return LLVMBuildMul(builder, lhs, rhs, "multmp");
    }

    public LLVMValueRef divide(LLVMValueRef lhs, LLVMValueRef rhs) {
        return LLVMBuildSDiv(builder, lhs, rhs, "divtmp");
    }

    // Methods for comparison operations
    public LLVMValueRef buildCmp(int predicate, LLVMValueRef lhs, LLVMValueRef rhs) {
        return LLVMBuildICmp(builder, predicate, lhs, rhs, "cmptmp");
    }

    // Method for printing value
    /**
     * Method for printing values using printf function
     * @param value The LLVM value to print
     * @param format Format string for printf (e.g., "%d" for integers, "%f" for doubles, "%s" for strings)
     * @return The LLVM function call instruction
     */
    public LLVMValueRef print(LLVMValueRef value, String format) {
        // Ensure printf function is declared if not already
        LLVMValueRef printfFunc = LLVMGetNamedFunction(module, "printf");

        if (printfFunc == null) {
            // Create printf function signature: int printf(char* format, ...)
            LLVMTypeRef[] printfParamTypes = { LLVMPointerType(i8Type, 0) };
            LLVMTypeRef printfType = LLVMFunctionType(i32Type, new PointerPointer<>(printfParamTypes), 1, 1);
            printfFunc = LLVMAddFunction(module, "printf", printfType);
        }

        // Create format string with newline appended
        String formatWithNewline = format + "\n";
        LLVMValueRef formatStr = constString(formatWithNewline);

        // Create arguments array for the printf call
        PointerPointer<Pointer> args = new PointerPointer<>(2)
                .put(0, formatStr)
                .put(1, value);

        // Build the call to printf
        return LLVMBuildCall2(builder, LLVMGetElementType(LLVMTypeOf(printfFunc)), printfFunc, args, 2, "print_call");
    }

    /**
     * Method for printing an integer value
     * @param value The integer value to print
     * @return The LLVM function call instruction
     */
    public LLVMValueRef printInt(LLVMValueRef value) {
        return print(value, "%d");
    }

    /**
     * Method for printing a double value
     * @param value The double value to print
     * @return The LLVM function call instruction
     */
    public LLVMValueRef printDouble(LLVMValueRef value) {
        return print(value, "%f");
    }

    /**
     * Method for printing a string value
     * @param value The string value to print
     * @return The LLVM function call instruction
     */
    public LLVMValueRef printString(LLVMValueRef value) {
        return print(value, "%s");
    }

    // Method to add return from main
    public void buildMainReturn(LLVMValueRef returnValue) {
        if (returnValue == null) {
            returnValue = constInt(0);
        }
        LLVMBuildRet(builder, returnValue);
    }

    // Methods for optimization and execution
    public void optimize() {
        LLVMPassManagerRef pm = LLVMCreatePassManager();

        LLVMRunPassManager(pm, module);
        LLVMDisposePassManager(pm);
    }

    public void verify() {
        if (LLVMVerifyModule(module, LLVMPrintMessageAction, error) != 0) {
            System.err.println("Error verifying module: " + error.getString());
            LLVMDisposeMessage(error);
            throw new RuntimeException("Module verification failed");
        }
    }

    // Method to dump IR to console
    public void dumpModule() {
        LLVMDumpModule(module);
    }

    // Method to initialize JIT execution engine
    public void initializeExecutionEngine() {
        engine = new LLVMExecutionEngineRef();
        LLVMMCJITCompilerOptions options = new LLVMMCJITCompilerOptions();
        if (LLVMCreateMCJITCompilerForModule(engine, module, options, 3, error) != 0) {
            System.err.println("Failed to create JIT compiler: " + error.getString());
            LLVMDisposeMessage(error);
            throw new RuntimeException("Failed to create execution engine");
        }
    }

    // Method to run the generated code
    public int executeMain() {
        initializeExecutionEngine();
        LLVMGenericValueRef result = LLVMRunFunction(engine, mainFunction, 0, new PointerPointer<>(0));
        return (int)LLVMGenericValueToInt(result, 0);
    }
    public void saveModuleToFile(String filename) {
        if (LLVMPrintModuleToFile(module, new BytePointer(filename), error) != 0) {
            System.err.println("Error printing to file: " + error.getString());
            LLVMDisposeMessage(error);
        }
    }
    // Resource cleanup
    public void dispose() {
        if (engine != null) {
            LLVMDisposeExecutionEngine(engine);
        }
        LLVMDisposeBuilder(builder);
        LLVMContextDispose(context);
    }
}