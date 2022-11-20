package Instruction;

import AST.*;
import java.util.*;

/**
 * Used for the following instructions:
 * 
 * lookupswitch
 * 
 */
public class LookupSwitchInstruction extends Instruction {
	private SortedMap<Object, SwitchLabel> sm; 
	private String defaultLabel;
	public LookupSwitchInstruction(int opCode, SortedMap<Object, SwitchLabel> sm, String defaultLabel) {
		super(opCode);
		this.sm = sm;
		this.defaultLabel = defaultLabel;
	}
	public SortedMap<Object, SwitchLabel> getValues() {
		return sm;
	}
    public String getDefaultLabel() {
	return defaultLabel;
    }
	public String toString() {
		String result = "lookupswitch\n";
		SwitchLabel sl = null;	
		for (Iterator<Object> ii=sm.keySet().iterator(); ii.hasNext();) {
			sl = sm.get(ii.next());
			result += "\t" + sl.expr().constantValue() + "\t: L" + sl.getSwitchGroup().getLabel() + "\n";
		}
		result += "\tdefault\t: " + defaultLabel;

		return result;
	}
}