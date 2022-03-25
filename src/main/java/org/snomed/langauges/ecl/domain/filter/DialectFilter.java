package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DialectFilter implements Filter {

	private final String booleanComparisonOperator;
	private final List<DialectAcceptability> dialectAcceptabilities;
	private SubExpressionConstraint subExpressionConstraint;

	public DialectFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		this.dialectAcceptabilities = new ArrayList<>();
	}

	public void addDialect(DialectAcceptability dialectAcceptability) {
		dialectAcceptabilities.add(dialectAcceptability);
	}

	@Override
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DialectFilter that = (DialectFilter) o;
		return Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator)&& Objects.equals(dialectAcceptabilities, that.dialectAcceptabilities);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanComparisonOperator, dialectAcceptabilities);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DialectFilter.class.getSimpleName() + "[", "]")
				.add("booleanComparisonOperator='" + booleanComparisonOperator + "'")
				.add("dialectAcceptabilities=" + dialectAcceptabilities).toString();
	}
}
