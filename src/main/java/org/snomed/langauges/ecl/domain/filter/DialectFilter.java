package org.snomed.langauges.ecl.domain.filter;

import java.util.*;

public class DialectFilter implements Filter {

	private final String booleanComparisonOperator;

	private final List<Dialect> dialects;

	private final Map<Dialect, Set<Acceptability>> acceptabilityMap;

	public DialectFilter(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
		this.acceptabilityMap = new HashMap<>();
		dialects = new ArrayList<>();
	}

	public Map<Dialect, Set<Acceptability>> getAcceptabilityMap() {
		return acceptabilityMap;
	}


	public void addDialect(Dialect dialect, Acceptability acceptability) {
		if (acceptability == null) {
			acceptabilityMap.computeIfAbsent(dialect, k -> new HashSet<>());
		} else {
			acceptabilityMap.computeIfAbsent(dialect, k -> new HashSet<>()).add(acceptability);
		}
		if (!dialects.contains(dialect)) {
			dialects.add(dialect);
		}
	}


	public List<Dialect> getDialects() {
		return dialects;
	}

	@Override
	public String getBooleanComparisonOperator() {
		return this.booleanComparisonOperator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DialectFilter that = (DialectFilter) o;
		return Objects.equals(booleanComparisonOperator, that.booleanComparisonOperator) && Objects.equals(dialects, that.dialects) && Objects.equals(acceptabilityMap, that.acceptabilityMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanComparisonOperator, dialects, acceptabilityMap);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DialectFilter.class.getSimpleName() + "[", "]")
				.add("booleanComparisonOperator='" + booleanComparisonOperator + "'")
				.add("dialects=" + dialects)
				.add("acceptabilityMap=" + acceptabilityMap).toString();
	}
}
