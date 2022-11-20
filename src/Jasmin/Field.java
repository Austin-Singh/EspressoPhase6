package Jasmin;

import AST.*;

public class Field {
	private FieldDecl field;

	public Field(FieldDecl fd) {
		field = fd;
	}

	public FieldDecl getField() {
		return field;
	}
}