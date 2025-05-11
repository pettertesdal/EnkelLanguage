grammar Enkel;
import EnkelTokens;

program: (statement NEWLINE)* EOF;

functionDef: DEF (TYPENUMBER | TYPESTRING | TYPEBOOL )? IDENT (WITH paramList)? START NEWLINE functionBody END;
functionBody: (statement NEWLINE)*;

paramList: param (BREAK param)*;
param: TYPENUMBER IDENT | TYPESTRING IDENT | TYPEBOOL IDENT;
stop: STOP;
statement
    : returnStatement
    | assignment
    | conditional
    | functionDef
    | loop
    | stop
    | printStatement;

returnStatement
    : expression RETURN;

assignment: HOIST? NEW? IDENT ASSIGN expression;
loop: LOOP TO (expression | STOP) START NEWLINE functionBody END;

functionCall: (READ | DO) IDENT (WITH expressionList)?;
expressionList: expression (BREAK expression)*;

expression
    : expression OP_MULT expression # Multiplication
    | expression OP_DIV expression # Divisjon
    | expression OP_PLUS expression # Addition
    | expression OP_MINUS expression # Subtraction
    | expression comparable expression # ComparableBool
    | '(' expression ')' # ParanthesesExpression
    | NUMBER #Number
    | STRING #String
    | functionCall # Functioncall
    | IDENT # Variable
    | readExpression # Readexpression
    ;

conditional: ifCondition ifElseCondition* elseCondition?;
ifCondition: IF expression THEN NEWLINE conditionalBody END NEWLINE?;
ifElseCondition: OR IF expression THEN NEWLINE conditionalBody END NEWLINE?;
elseCondition: ELSE THEN NEWLINE conditionalBody END NEWLINE?;
conditionalBody: (statement NEWLINE)*;

printStatement: WRITE expression;
readExpression: READ (TYPENUMBER | TYPESTRING | TYPEBOOL);
comparable: EQ | LEQ | GEQ | GREATER | LESS;