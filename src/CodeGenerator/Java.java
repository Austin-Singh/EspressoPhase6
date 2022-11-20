package CodeGenerator;

import Utilities.*;
import AST.*;

/** The Java visitor is used to replace Name objects in the parse tree.
 * 'Thread' is replaced by 'java/lang/Thread'
 * 'Runnable' is replaced by 'java/lang/Runnable'
 * 'Object' is replaced by 'java/lang/Object'
 * 
 * @author Matt Pedersen
 *
 */
public class Java extends Visitor {

    public Object visitName(Name na) {
	//if (na.getname().equals("Thread")) 
	//    na.setName("java/lang/Thread");
	//else if (na.getname().equals("Runnable")) 
	//    na.setName("java/lang/Runnable");
	if (na.getname().equals("Object"))
	    na.setName("java/lang/Object");
	return null;
    }
}
