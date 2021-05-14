package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DescriptionTypeFilter implements Filter {

	private final List<DescriptionType> types;
	private final String booleanComparisonOperator;

	public DescriptionTypeFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		types = new ArrayList<>();
	}

	public void addType(DescriptionType type) {
		types.add(type);
	}


	public List<DescriptionType> getTypes() {
		return types;
	}

	@Override
	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DescriptionTypeFilter that = (DescriptionTypeFilter) o;
		return Objects.equals(types, that.types) && Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator);
	}

	@Override
	public int hashCode() {
		return Objects.hash(types, booleanComparisonOperator);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DescriptionTypeFilter.class.getSimpleName() + "[", "]").add("types=" + types).add("booleanComparisonOperator='" + booleanComparisonOperator + "'").toString();
	}
}
