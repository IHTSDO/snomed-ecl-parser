package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DescriptionTypeFilter implements Filter {

	private final String booleanComparisonOperator;
	private List<DescriptionType> types;
	private SubExpressionConstraint subExpressionConstraint;

	public DescriptionTypeFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
	}

	public void addType(DescriptionType type) {
		if (types == null) {
			types = new ArrayList<>();
		}
		types.add(type);
	}

	@Override
	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	public List<DescriptionType> getTypes() {
		return types;
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}

	public void setSubExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
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
