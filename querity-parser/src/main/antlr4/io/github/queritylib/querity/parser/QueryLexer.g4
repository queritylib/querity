lexer grammar QueryLexer;

DISTINCT   : 'distinct';
AND        : 'and';
OR         : 'or';
NOT        : 'not';
SORT       : 'sort by';
ASC        : 'asc';
DESC       : 'desc';
PAGINATION : 'page';
NEQ        : '!=';
LTE        : '<=';
GTE        : '>=';
EQ         : '=';
LT         : '<';
GT         : '>';
STARTS_WITH: 'starts with';
ENDS_WITH  : 'ends with';
CONTAINS   : 'contains';
IS_NULL    : 'is null';
IS_NOT_NULL: 'is not null';
LPAREN     : '(';
RPAREN     : ')';
COMMA      : ',';

INT_VALUE     : [0-9]+;
DECIMAL_VALUE : [0-9]+'.'[0-9]+;
BOOLEAN_VALUE : 'true' | 'false';
PROPERTY      : [a-zA-Z_][a-zA-Z0-9_.]*;
STRING_VALUE  : '"' (~["\\] | '\\' .)* '"';

WS : [ \t\r\n]+ -> skip;
