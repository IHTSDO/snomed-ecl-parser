package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class DialectFilter {

	private boolean dialectAliasFilter;
	private String booleanComparisonOperator;
	private List<DialectAcceptability> dialectAcceptabilities;
	private SubExpressionConstraint subExpressionConstraint;

	@SuppressWarnings("unused")
	protected DialectFilter() {
		// For JSON
	}

	public DialectFilter(String booleanComparisonOperator, boolean dialectAliasFilter) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		this.dialectAliasFilter = dialectAliasFilter;
		this.dialectAcceptabilities = new ArrayList<>();
	}

	public void addDialect(DialectAcceptability dialectAcceptability) {
		dialectAcceptabilities.add(dialectAcceptability);
	}

	public boolean isDialectAliasFilter() {
		return dialectAliasFilter;
	}

	public void setDialectAliasFilter(boolean dialectAliasFilter) {
		this.dialectAliasFilter = dialectAliasFilter;
	}

	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	public List<DialectAcceptability> getDialectAcceptabilities() {
		return dialectAcceptabilities;
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}

	public void setSubExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
	}

	@Override
	public String toString() {
		return "DialectFilter{" +
				"dialectAliasFilter=" + dialectAliasFilter +
				", booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", dialectAcceptabilities=" + dialectAcceptabilities +
				", subExpressionConstraint=" + subExpressionConstraint +
				'}';
	}
}
