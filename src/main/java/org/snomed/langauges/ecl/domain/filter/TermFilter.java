package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class TermFilter implements Filter {
	private final String booleanComparisonOperator;
	private final List<String> typedSearchTerms;

	public TermFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		typedSearchTerms = new ArrayList<>();
	}

	public void addTypedSearchTerm(String typedSearchTerm) {
		typedSearchTerms.add(typedSearchTerm);
	}

	public List<String> getTypedSearchTerms() {
		return typedSearchTerms;
	}

	public static String getTerm(String typedSearchTerm) {
		if (typedSearchTerm.contains(SearchType.MATCH.getText()) || typedSearchTerm.contains(SearchType.WILDCARD.getText())) {
			return removeEscapedDoubleQuotes(typedSearchTerm.split(":")[1]);
		} else {
			return removeEscapedDoubleQuotes(typedSearchTerm);
		}
	}

	public static SearchType getSearchType(String typedSearchTerm) {
		if (typedSearchTerm == null) {
			return null;
		}
		if (typedSearchTerm.startsWith(SearchType.WILDCARD.getText())) {
			return SearchType.WILDCARD;
		}
		return SearchType.MATCH;
	}


	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	private static String removeEscapedDoubleQuotes(String escapedTerm) {
		return escapedTerm.replace("\"", "");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TermFilter that = (TermFilter) o;
		return Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator) && Objects.equals(typedSearchTerms, that.typedSearchTerms);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanComparisonOperator, typedSearchTerms);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TermFilter.class.getSimpleName() + "[", "]").add("booleanComparisonOperator='" + booleanComparisonOperator + "'").add("typedSearchTerms=" + typedSearchTerms).toString();
	}
}
