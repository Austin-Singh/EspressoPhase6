package Jasmin;

import java.util.*;

class Signature {
	private String[] signature;
	private String returnType;

	public Signature(String sig) {
		signature = breaksignature(sig);
		returnType = signature[signature.length-1];
	}

	public String[] getSignature() {
		return signature;
	}

	public String getReturnType() {
		return returnType;
	}

	public int size() {
		return signature.length-1;
	}

	public String toString() {
		String s = "(";
		for (int i=0;i<size();i++) {
			s = s + signature[i];
		}
		s = s + ")" + returnType;
		return s;
	}

	public static String[] breaksignature(String s) {
		char[] chars = s.toCharArray();
		int cc = 0;
		int len;
		String news ="";

		len = chars.length;

		List<String> strings = new ArrayList<String>();

		for (int i=0; i<len; i++) {
			if (cc == 1) {
				if (chars[i] == ';') {
					cc = 0;
					strings.add(news + chars[i]);
					news = "";
				}
				else
					news = news + chars[i];
			}
			else {
				if (chars[i] == 'L') {
					cc = 1;
					news = "" + chars[i];
				}
				else {
					if (chars[i] == '(' || chars[i] == ')')
						;
					else {
						strings.add("" + chars[i]);
					}
				}
			}
		}
		return strings.toArray(new String[strings.size()]);
	}
}





