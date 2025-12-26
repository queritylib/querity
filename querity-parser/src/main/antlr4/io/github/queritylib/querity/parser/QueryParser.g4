parser grammar QueryParser;

options { tokenVocab=QueryLexer; }

// Main query structure
query            : DISTINCT? (selectClause)? (condition)? (SORT sortFields)? (PAGINATION paginationParams)? ;

// Select clause with support for expressions and optional aliases
selectClause     : SELECT selectFields ;
selectFields     : selectField (COMMA selectField)* ;
selectField      : propertyExpression (AS alias=propertyName)? ;

// Conditions
condition        : simpleCondition | conditionWrapper | notCondition;
conditionWrapper : (AND | OR) LPAREN condition (COMMA condition)* RPAREN ;
notCondition     : NOT LPAREN condition RPAREN ;

// Simple condition with support for expressions on the left side
simpleCondition  : propertyExpression operator (simpleValue | arrayValue | valueProperty=propertyName)? ;

// Operators
operator         : NEQ | LTE | GTE | EQ | LT | GT | STARTS_WITH | ENDS_WITH | CONTAINS | IS_NULL | IS_NOT_NULL | IN | NOT_IN ;

// Values
simpleValue      : INT_VALUE | DECIMAL_VALUE | BOOLEAN_VALUE | STRING_VALUE;
arrayValue       : LPAREN simpleValue (COMMA simpleValue)* RPAREN ;

// Property expression: either a simple property or a function call
propertyExpression : propertyName | functionCall ;

// Property name, optionally quoted with backticks
propertyName : PROPERTY | BACKTICK_PROPERTY ;

// Function calls
functionCall     : functionName LPAREN functionArgs? RPAREN
                 | nullaryFunction
                 ;

// Nullary functions (no arguments, no parentheses required)
nullaryFunction  : CURRENT_DATE_FUNC
                 | CURRENT_TIME_FUNC
                 | CURRENT_TIMESTAMP_FUNC
                 ;

// Function names
functionName     : ABS_FUNC
                 | SQRT_FUNC
                 | MOD_FUNC
                 | CONCAT_FUNC
                 | SUBSTRING_FUNC
                 | TRIM_FUNC
                 | LTRIM_FUNC
                 | RTRIM_FUNC
                 | LOWER_FUNC
                 | UPPER_FUNC
                 | LENGTH_FUNC
                 | LOCATE_FUNC
                 | COALESCE_FUNC
                 | NULLIF_FUNC
                 | COUNT_FUNC
                 | SUM_FUNC
                 | AVG_FUNC
                 | MIN_FUNC
                 | MAX_FUNC
                 ;

// Function arguments: expressions or literal values
functionArgs     : functionArg (COMMA functionArg)* ;
functionArg      : propertyExpression | simpleValue ;

// Sorting with support for expressions
sortField        : propertyExpression (direction)? ;
sortFields       : sortField (COMMA sortField)* ;
direction        : ASC | DESC ;

// Pagination
paginationParams : INT_VALUE COMMA INT_VALUE ;
