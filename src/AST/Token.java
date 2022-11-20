package AST;

public class Token {
    private int sym;
    private String lexeme;
    private int line;
    private int charBegin;
    private int charEnd;
    
    public static final String names[] = {
	"EOF",
        "error",
	"BYTE",
        "CHAR",
        "SHORT",
        "INT",
        "LONG",
        "FLOAT",
        "DOUBLE",
        "BOOLEAN",
        "STRING",
        "BREAK",                
        "CLASS",                
        "CONTINUE",
        "DO",      
        "ELSE",    
        "EXTENDS",
        "FOR",    
        "IF",    
        "NEW",   
        "RETURN",  
        "SUPER",         
        "THIS",  
        "VOID",          
        "WHILE",         
        "CASE", 
        "SWITCH",
        "DEFAULT",
        "IMPLEMENTS", 
        "INTERFACE", 
	"IMPORT",
        "PUBLIC", 
        "PRIVATE", 
        "STATIC", 
        "FINAL", 
        "ABSTRACT", 
        "BOOLEAN_LITERAL",
        "FLOAT_LITERAL",
        "DOUBLE_LITERAL",
        "IDENTIFIER", 
        "INTEGER_LITERAL", 
        "LONG_LITERAL", 
        "NULL_LITERAL",
        "STRING_LITERAL",
        "CHARACTER_LITERAL",
        "EQ",
        "LT",
        "GT",   
        "LTEQ",
        "GTEQ", 
        "PLUSPLUS",     
        "MINUSMINUS",
        "PLUS",
        "MINUS",
        "MULT", 
        "DIV",  
        "COMP",
        "NOT", 
        "MOD",  
        "EQEQ",
        "NOTEQ",        
        "AND",
        "XOR",
        "OR",   
        "ANDAND",
        "OROR", 
        "LSHIFT",
        "RSHIFT",
        "RRSHIFT",      
        "INSTANCEOF",   
        "MULTEQ",
        "DIVEQ",   
        "PLUSEQ",
        "MINUSEQ", 
        "MODEQ",
        "XOREQ",    
        "LSHIFTEQ",
        "RSHIFTEQ", 
        "RRSHIFTEQ",
        "ANDEQ",
        "OREQ",     
        "SEMICOLON",
        "COLON",
        "COMMA",
        "DOT",  
        "QUEST",  
        "LBRACE",
        "RBRACE", 
        "LPAREN",
        "RPAREN",
        "LBRACK",
        "RBRACK"};
    
    public Token (int p_kind, String p_lexeme, int p_line, int p_charBegin, int p_charEnd) {
	sym = p_kind;
	lexeme = p_lexeme;
	line = p_line;
	charBegin = p_charBegin;
	charEnd = p_charEnd;
    }
    
    public int getCharBegin() { return charBegin; }
    public int getCharEnd()   { return charEnd; }
    public int getLine() { return line; }
    public String getLexeme() { return lexeme; }
    public int getSym() { return sym; }

    public String toString() {
	return "Token " + names[sym] + " '" + lexeme + "'" + " line " + line + 
	       " pos [" + charBegin + ".." + charEnd + "]";
    }
}



