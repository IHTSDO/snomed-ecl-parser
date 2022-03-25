package org.snomed.langauges.ecl.domain.filter;

public enum TimeComparisonOperator {

	EQUAL("=", "EQUALS"),
	NOT_EQUAL("!=", "EXCLAMATION EQUALS"),
	LESS_THAN_OR_EQUAL("<=", "LESS_THAN EQUALS"),
	LESS_THAN("<", "LESS_THAN"),
	GREATER_THAN_OR_EQUAL(">=", "GREATER_THAN EQUALS"),
	GREATER_THAN(">", "GREATER_THAN");

	private final String text;
	private final String grammar;

	TimeComparisonOperator(String text, String grammar) {
		this.text = text;
		this.grammar = grammar;
	}

	public static TimeComparisonOperator fromGrammar(String grammarString) {
		for (TimeComparisonOperator value : values()) {
			if (value.grammar.equals(grammarString)) {
				return value;
			}
		}
		return null;
	}

	public String getText() {
		return text;
	}
}
