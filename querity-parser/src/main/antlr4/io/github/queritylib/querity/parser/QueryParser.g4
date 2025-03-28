parser grammar QueryParser;

options { tokenVocab=QueryLexer; }

query            : DISTINCT? (condition)? (SORT sortFields)? (PAGINATION paginationParams)? ;
condition        : simpleCondition | conditionWrapper | notCondition;
operator         : NEQ | LTE | GTE | EQ | LT | GT | STARTS_WITH | ENDS_WITH | CONTAINS | IS_NULL | IS_NOT_NULL ;
conditionWrapper : (AND | OR) LPAREN condition (COMMA condition)* RPAREN ;
notCondition     : NOT LPAREN condition RPAREN ;
simpleCondition  : PROPERTY operator (INT_VALUE | DECIMAL_VALUE | BOOLEAN_VALUE | STRING_VALUE)? ;
direction        : ASC | DESC ;
sortField        : PROPERTY (direction)? ;
sortFields       : sortField (COMMA sortField)* ;
paginationParams : INT_VALUE COMMA INT_VALUE ;
