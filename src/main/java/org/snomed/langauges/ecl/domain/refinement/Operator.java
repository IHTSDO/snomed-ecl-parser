package org.snomed.langauges.ecl.domain.refinement;

public enum Operator {

	childof("<!"), childorselfof("<<!"), descendantorselfof("<<"), descendantof("<"), parentof(">!"), parentorselfof(">>!"), ancestororselfof(">>"), ancestorof(">"), memberOf("^");

	private final String text;

	Operator(String text) {
		this.text = text;
	}

	public static Operator textLookup(String text) {
		for (Operator operator : values()) {
			if (operator.text.equals(text)) return operator;
		}
		return null;
	}

	public String getText() { return text; }
}
