package AST;
import Utilities.Visitor;
import java.util.*;
public class StaticInitDecl extends ClassBodyDecl {

        public HashSet<String> initializedFields= new HashSet<String>();
    
	public StaticInitDecl(Block init) {
		super(init);
		nchildren = 1;
		children = new AST[] { init };
	}

	public Block initializer() { return (Block)children[0]; }

	public String getname() {
		return "static initializer";
	}

	public boolean isStatic() {
		return true;
	}

	/* *********************************************************** */
	/* **                                                       ** */
	/* ** Generic Visitor Stuff                                 ** */
	/* **                                                       ** */
	/* *********************************************************** */

	public Object visit(Visitor v) {
		return v.visitStaticInitDecl(this);
	}
}

