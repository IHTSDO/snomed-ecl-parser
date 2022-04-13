package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;

public class MemberFilterConstraint {

	private List<MemberFieldFilter> memberFieldFilters;
	private List<FieldFilter> moduleFilters;
	private List<EffectiveTimeFilter> effectiveTimeFilters;
	private List<ActiveFilter> activeFilters;

	public void addMemberFieldFilter(MemberFieldFilter memberFieldFilter) {
		if (memberFieldFilters == null) {
			memberFieldFilters = new ArrayList<>();
		}
		memberFieldFilters.add(memberFieldFilter);
	}

	public void addModuleFilter(FieldFilter moduleFilter) {
		if (moduleFilters == null) {
			moduleFilters = new ArrayList<>();
		}
		moduleFilters.add(moduleFilter);
	}

	public void addEffectiveTimeFilter(EffectiveTimeFilter effectiveTimeFilter) {
		if (effectiveTimeFilters == null) {
			effectiveTimeFilters = new ArrayList<>();
		}
		effectiveTimeFilters.add(effectiveTimeFilter);
	}

	public void addActiveFilter(ActiveFilter activeFilter) {
		if (activeFilters == null) {
			activeFilters = new ArrayList<>();
		}
		activeFilters.add(activeFilter);
	}

	public List<MemberFieldFilter> getMemberFieldFilters() {
		return memberFieldFilters;
	}

	public List<FieldFilter> getModuleFilters() {
		return moduleFilters;
	}

	public List<EffectiveTimeFilter> getEffectiveTimeFilters() {
		return effectiveTimeFilters;
	}

	public List<ActiveFilter> getActiveFilters() {
		return activeFilters;
	}

	@Override
	public String toString() {
		return "MemberFilterConstraint{" +
				"memberFieldFilters=" + memberFieldFilters +
				", moduleFilters=" + moduleFilters +
				", effectiveTimeFilters=" + effectiveTimeFilters +
				", activeFilters=" + activeFilters +
				'}';
	}
}
