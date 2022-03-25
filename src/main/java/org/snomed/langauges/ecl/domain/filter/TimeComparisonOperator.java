package org.snomed.langauges.ecl.domain.filter;

public enum TimeComparisonOperator {

	EQUAL("="),
	NOT_EQUAL("!="),
	LESS_THAN_OR_EQUAL("<="),
	LESS_THAN("<"),
	GREATER_THAN_OR_EQUAL(">="),
	GREATER_THAN(">");

	private final String text;

	TimeComparisonOperator(String text) {
		this.text = text;
	}

	public static TimeComparisonOperator fromText(String textString) {
		for (TimeComparisonOperator value : values()) {
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
