package org.snomed.langauges.ecl.domain.filter;

public class ActiveFilter {

	private boolean active;

	private ActiveFilter() {
	}

	public ActiveFilter(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
