parser grammar QueryParser;

options { tokenVocab=QueryLexer; }

query            : DISTINCT? (condition)? (SORT sortFields)? (PAGINATION paginationParams)? ;
condition        : simpleCondition | conditionWrapper | notCondition;
operator         : NEQ | LTE | GTE | EQ | LT | GT | STARTS_WITH | ENDS_WITH | CONTAINS | IS_NULL | IS_NOT_NULL | IN | NOT_IN ;
conditionWrapper : (AND | OR) LPAREN condition (COMMA condition)* RPAREN ;
notCondition     : NOT LPAREN condition RPAREN ;
simpleValue      : INT_VALUE | DECIMAL_VALUE | BOOLEAN_VALUE | STRING_VALUE;
arrayValue       : LPAREN simpleValue (COMMA simpleValue)* RPAREN ;
simpleCondition  : PROPERTY operator (simpleValue | arrayValue)? ;
direction        : ASC | DESC ;
sortField        : PROPERTY (direction)? ;
sortFields       : sortField (COMMA sortField)* ;
paginationParams : INT_VALUE COMMA INT_VALUE ;
