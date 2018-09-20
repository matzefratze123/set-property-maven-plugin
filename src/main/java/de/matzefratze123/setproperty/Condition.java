package de.matzefratze123.setproperty;

import java.util.regex.Pattern;

public class Condition {

	private String string;
	private Operator operator;
	private String operand;
  private boolean caseSensitive = true;

	public boolean process() {
		if (operator == Operator.EQUALS) {
      return string == null ? operand == null : caseSensitive ? string.equals(operand) : string.equalsIgnoreCase(operand);
    } else {
      if (string == null || operand == null) {
        return string == operand;
      }
      Pattern pattern = caseSensitive ? Pattern.compile(operand) : Pattern.compile(operand, Pattern.CASE_INSENSITIVE);
      return pattern.matcher(string).matches();
    }
	}
	
	public enum Operator {
		
		EQUALS,
		EQUALS_REGEX;
		
	}
	
}
