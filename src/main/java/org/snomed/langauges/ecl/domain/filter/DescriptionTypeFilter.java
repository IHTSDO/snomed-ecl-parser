package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class DescriptionTypeFilter {

	private String booleanComparisonOperator;
	private List<DescriptionType> types;
	private SubExpressionConstraint subExpressionConstraint;

	@SuppressWarnings("unused")
	protected DescriptionTypeFilter() {
		// For JSON
	}

	public DescriptionTypeFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
	}

	public void addType(DescriptionType type) {
		if (types == null) {
			types = new ArrayList<>();
		}
		types.add(type);
	}

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
	public String toString() {
		return new StringJoiner(", ", DescriptionTypeFilter.class.getSimpleName() + "[", "]").add("types=" + types).add("booleanComparisonOperator='" + booleanComparisonOperator + "'").toString();
	}
}
