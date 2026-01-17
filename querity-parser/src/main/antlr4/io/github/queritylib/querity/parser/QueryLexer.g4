lexer grammar QueryLexer;

// Case-insensitive letter fragments
fragment A: [aA]; fragment B: [bB]; fragment C: [cC]; fragment D: [dD];
fragment E: [eE]; fragment F: [fF]; fragment G: [gG]; fragment H: [hH];
fragment I: [iI]; fragment J: [jJ]; fragment K: [kK]; fragment L: [lL];
fragment M: [mM]; fragment N: [nN]; fragment O: [oO]; fragment P: [pP];
fragment Q: [qQ]; fragment R: [rR]; fragment S: [sS]; fragment T: [tT];
fragment U: [uU]; fragment V: [vV]; fragment W: [wW]; fragment X: [xX];
fragment Y: [yY]; fragment Z: [zZ];

// Keywords
DISTINCT    : D I S T I N C T;
AND         : A N D;
OR          : O R;
NOT         : N O T;
SELECT      : S E L E C T;
SORT        : S O R T ' ' B Y;
ASC         : A S C;
DESC        : D E S C;
PAGINATION  : P A G E;
AS          : A S;
GROUP_BY    : G R O U P ' ' B Y;
HAVING      : H A V I N G;
WHERE       : W H E R E;

// Comparison operators
NEQ         : '!=';
LTE         : '<=';
GTE         : '>=';
EQ          : '=';
LT          : '<';
GT          : '>';
STARTS_WITH : S T A R T S ' ' W I T H;
ENDS_WITH   : E N D S ' ' W I T H;
CONTAINS    : C O N T A I N S;
IS_NULL     : I S ' ' N U L L;
IS_NOT_NULL : I S ' ' N O T ' ' N U L L;
IN          : I N;
NOT_IN      : N O T ' ' I N;

// Punctuation
LPAREN      : '(';
RPAREN      : ')';
COMMA       : ',';

// Arithmetic functions
ABS_FUNC       : A B S;
SQRT_FUNC      : S Q R T;
MOD_FUNC       : M O D;

// String functions
CONCAT_FUNC    : C O N C A T;
SUBSTRING_FUNC : S U B S T R I N G;
TRIM_FUNC      : T R I M;
LTRIM_FUNC     : L T R I M;
RTRIM_FUNC     : R T R I M;
LOWER_FUNC     : L O W E R;
UPPER_FUNC     : U P P E R;
LENGTH_FUNC    : L E N G T H;
LOCATE_FUNC    : L O C A T E;

// Date/Time functions (nullary)
CURRENT_DATE_FUNC      : C U R R E N T '_' D A T E;
CURRENT_TIME_FUNC      : C U R R E N T '_' T I M E;
CURRENT_TIMESTAMP_FUNC : C U R R E N T '_' T I M E S T A M P;

// Conditional functions
COALESCE_FUNC : C O A L E S C E;
NULLIF_FUNC   : N U L L I F;

// Aggregate functions
COUNT_FUNC    : C O U N T;
SUM_FUNC      : S U M;
AVG_FUNC      : A V G;
MIN_FUNC      : M I N;
MAX_FUNC      : M A X;

// Literals
INT_VALUE     : [0-9]+;
DECIMAL_VALUE : [0-9]+'.'[0-9]+;
BOOLEAN_VALUE : T R U E | F A L S E;
STRING_VALUE  : '"' (~["\\] | '\\' .)* '"';

// Identifiers - must come after keywords to avoid conflicts
BACKTICK_PROPERTY : '`' (~[`\\] | '\\' .)* '`';
PROPERTY      : [a-zA-Z_][a-zA-Z0-9_.]*;

WS : [ \t\r\n]+ -> skip;
