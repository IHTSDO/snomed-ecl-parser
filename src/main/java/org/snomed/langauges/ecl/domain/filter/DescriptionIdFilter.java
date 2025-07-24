package org.snomed.langauges.ecl.domain.filter;

import java.util.HashSet;
import java.util.Set;

public class DescriptionIdFilter {

	private String booleanComparisonOperator;
	private Set<String> descriptionIds;

	@SuppressWarnings("unused")
	protected DescriptionIdFilter() {
		// For JSON
	}

	public DescriptionIdFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		descriptionIds = new HashSet<>();
	}

	public void addDescriptionId(String id) {
		descriptionIds.add(id);
	}

	public Set<String> getDescriptionIds() {
		return descriptionIds;
	}

	public String getBooleanComparisonOperator() {
		return booleanComparisonOperator;
	}

	@Override
	public String toString() {
		return "DescriptionIdFilter{" +
				"booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", descriptionIds=" + descriptionIds +
				'}';
	}
}
