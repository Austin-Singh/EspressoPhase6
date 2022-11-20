package Phases;

import AST.*;
import Utilities.SymbolTable;

public abstract class Phase {
    public static int phase; // the phase the compiler was invoked to run
    public static AST root;
    public static SymbolTable classTable = new SymbolTable();
    public abstract void execute(Object arg, int debuglevel, int runLevel) ;	
}
