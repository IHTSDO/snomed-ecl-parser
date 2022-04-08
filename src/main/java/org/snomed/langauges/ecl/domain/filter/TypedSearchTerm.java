package org.snomed.langauges.ecl.domain.filter;

public class TypedSearchTerm {

	private SearchType type;
	private String term;

	@SuppressWarnings("unused")
	protected TypedSearchTerm() {
		// For JSON
	}

	public TypedSearchTerm(SearchType type, String term) {
		this.type = type;
		this.term = term;
	}

	public SearchType getType() {
		return type;
	}

	public String getTerm() {
		return term;
	}

}
