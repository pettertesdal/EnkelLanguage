lexer grammar EnkelTokens;

DEF: 'definer';
WITH: 'med';
START: ':';
END: 'slutt';
IF: 'hvis';
THEN: 'så';
NEW: 'ny';
RESULT: 'resultat';
READ: 'les';
WRITE: 'skriv';
DO: 'gjør';
TO: 'til';
GIVE: 'gi';
OR: 'eller';
ELSE: 'ellers';
HOIST: 'heis';
LOOP: 'loop';
STOP: 'stop';

FOR: 'for';
EACH: 'hver';
IN: 'i';

WHILE: 'mens';

TYPENUMBER: 'tall';
TYPESTRING: 'tekst';
TYPEBOOL: 'bool';

IDENT: [a-zA-Z_][a-zA-Z_0-9]*;
NUMBER: [0-9]+;
STRING: '"' ~["]* '"';
BOOL: 'false' | 'true';

ASSIGN: '=';

OP_PLUS: '+';
OP_MINUS: '-';
OP_MULT: '*';
OP_DIV: '/';

EQ: '==';
LEQ: '<=';
GEQ: '>=';
LESS: '<';
GREATER: '>';

BREAK: ',';
RETURN: '.';

NEWLINE : '\r'? '\n' [ \r\n\t]*;
WHITESPACE : [ \r\n\t]+ -> skip;

// Skip comments
COMMENT: '//' ~[\r\n]* -> skip;