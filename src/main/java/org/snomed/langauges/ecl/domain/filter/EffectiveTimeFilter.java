package org.snomed.langauges.ecl.domain.filter;

import java.util.Set;

public class EffectiveTimeFilter {

	private TimeComparisonOperator operator;
	private Set<Integer> effectiveTime;

	private EffectiveTimeFilter() {
	}

	public EffectiveTimeFilter(TimeComparisonOperator operator, Set<Integer> effectiveTime) {
		this.operator = operator;
		this.effectiveTime = effectiveTime;
	}

	public TimeComparisonOperator getOperator() {
		return operator;
	}

	public Set<Integer> getEffectiveTime() {
		return effectiveTime;
	}
}
