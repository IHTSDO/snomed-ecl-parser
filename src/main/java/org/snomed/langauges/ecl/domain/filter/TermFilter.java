package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;

public class TermFilter {

	private String booleanComparisonOperator;
	private List<TypedSearchTerm> typedSearchTermSet;

	@SuppressWarnings("unused")
	protected TermFilter() {
		// For JSON
	}

	public TermFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		typedSearchTermSet = new ArrayList<>();
	}

	public void addTypedSearchTerm(TypedSearchTerm typedSearchTerm) {
		typedSearchTermSet.add(typedSearchTerm);
	}

	public String getBooleanComparisonOperator() {
		return booleanComparisonOperator;
	}

	public List<TypedSearchTerm> getTypedSearchTermSet() {
		return typedSearchTermSet;
	}

	@Override
	public String toString() {
		return "TermFilter{" +
				"booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", typedSearchTermSet=" + typedSearchTermSet +
				'}';
	}
}
