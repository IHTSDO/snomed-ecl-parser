package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;


public class LanguageFilter implements Filter {
	private final String booleanComparisonOperator;
	private final List<String> languageCodes;

	public LanguageFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		languageCodes = new ArrayList<>();
	}

	public void addLanguageCode(String languageCode) {
		languageCodes.add(languageCode);
	}

	public List<String> getLanguageCodes() {
		return languageCodes;
	}

	@Override
	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LanguageFilter that = (LanguageFilter) o;
		return Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator) && Objects.equals(languageCodes, that.languageCodes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanComparisonOperator, languageCodes);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", LanguageFilter.class.getSimpleName() + "[", "]").add("booleanComparisonOperator='" + booleanComparisonOperator + "'").add("languageCodes=" + languageCodes).toString();
	}
}
