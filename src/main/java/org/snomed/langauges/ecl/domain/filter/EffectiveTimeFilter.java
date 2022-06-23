package org.snomed.langauges.ecl.domain.filter;

import java.util.List;

public class EffectiveTimeFilter {

	private NumericComparisonOperator operator;
	private List<Integer> effectiveTime;

	@SuppressWarnings("unused")
	protected EffectiveTimeFilter() {
		// For JSON
	}

	public EffectiveTimeFilter(NumericComparisonOperator operator, List<Integer> effectiveTime) {
		this.operator = operator;
		this.effectiveTime = effectiveTime;
	}

	public NumericComparisonOperator getOperator() {
		return operator;
	}

	public List<Integer> getEffectiveTime() {
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
