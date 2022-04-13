package org.snomed.langauges.ecl.domain.filter;

public class ActiveFilter {

	private boolean active;

	@SuppressWarnings("unused")
	protected ActiveFilter() {
		// For JSON
	}

	public ActiveFilter(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		return "ActiveFilter{" +
				"active=" + active +
				'}';
	}
}
