package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.ExpressionConstraint;

public class HistorySupplement {

	// Either
	private HistoryProfile historyProfile;
	// OR
	private ExpressionConstraint historySubset;

	public void setHistoryProfile(HistoryProfile historyProfile) {
		this.historyProfile = historyProfile;
	}

	public HistoryProfile getHistoryProfile() {
		return historyProfile;
	}

	public void setHistorySubset(ExpressionConstraint historySubset) {
		this.historySubset = historySubset;
	}

	public ExpressionConstraint getHistorySubset() {
		return historySubset;
	}

	@Override
	public String toString() {
		return "HistorySupplement{" +
				"historyProfile=" + historyProfile +
				", historySubset=" + historySubset +
				'}';
	}
}
