package org.snomed.langauges.ecl.domain.filter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TermFilter implements Filter {

	private String booleanComparisonOperator;
	private Set<TypedSearchTerm> typedSearchTermSet;

	private TermFilter() {
	}

	public TermFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		typedSearchTermSet = new HashSet<>();
	}

	public void addTypedSearchTerm(TypedSearchTerm typedSearchTerm) {
		typedSearchTermSet.add(typedSearchTerm);
	}

	@Override
	public String getBooleanComparisonOperator() {
		return booleanComparisonOperator;
	}

	public Set<TypedSearchTerm> getTypedSearchTermSet() {
		return typedSearchTermSet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TermFilter that = (TermFilter) o;
		return Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator) && Objects.equals(typedSearchTermSet, that.typedSearchTermSet);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanComparisonOperator, typedSearchTermSet);
	}
}
