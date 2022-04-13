package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;


public class LanguageFilter {

	private String booleanComparisonOperator;
	private List<String> languageCodes;

	@SuppressWarnings("unused")
	protected LanguageFilter() {
		// For JSON
	}

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

	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	@Override
	public String toString() {
		return "LanguageFilter{" +
				"booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", languageCodes=" + languageCodes +
				'}';
	}
}
