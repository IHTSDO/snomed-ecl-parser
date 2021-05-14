package org.snomed.langauges.ecl.domain.filter;

public enum SearchType {
	// a word-prefix-any-order match
	MATCH("match"),
	WILDCARD("wild");

	private final String text;

	SearchType(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}