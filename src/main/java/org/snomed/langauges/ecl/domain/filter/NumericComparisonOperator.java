package org.snomed.langauges.ecl.domain.filter;

public enum NumericComparisonOperator {

	EQUAL("="),
	NOT_EQUAL("!="),
	LESS_THAN_OR_EQUAL("<="),
	LESS_THAN("<"),
	GREATER_THAN_OR_EQUAL(">="),
	GREATER_THAN(">");

	private final String text;

	NumericComparisonOperator(String text) {
		this.text = text;
	}

	public static NumericComparisonOperator fromText(String textString) {
		for (NumericComparisonOperator value : values()) {
			if (value.text.equals(textString)) {
				return value;
			}
		}
		return null;
	}

	public String getText() {
		return text;
	}
}
