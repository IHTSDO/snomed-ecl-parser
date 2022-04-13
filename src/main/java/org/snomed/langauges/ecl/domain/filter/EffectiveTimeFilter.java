package org.snomed.langauges.ecl.domain.filter;

import java.util.Set;

public class EffectiveTimeFilter {

	private NumericComparisonOperator operator;
	private Set<Integer> effectiveTime;

	@SuppressWarnings("unused")
	protected EffectiveTimeFilter() {
		// For JSON
	}

	public EffectiveTimeFilter(NumericComparisonOperator operator, Set<Integer> effectiveTime) {
		this.operator = operator;
		this.effectiveTime = effectiveTime;
	}

	public NumericComparisonOperator getOperator() {
		return operator;
	}

	public Set<Integer> getEffectiveTime() {
		return effectiveTime;
	}

	@Override
	public String toString() {
		return "EffectiveTimeFilter{" +
				"operator=" + operator +
				", effectiveTime=" + effectiveTime +
				'}';
	}
}
