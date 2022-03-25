package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;

public class ConceptFilterConstraint {

	private List<ActiveFilter> activeFilters;
	private List<FieldFilter> definitionStatusFilters;
	private List<EffectiveTimeFilter> effectiveTimeFilters;
	private List<FieldFilter> moduleFilters;

	public void addActiveFilter(ActiveFilter filter) {
		if (activeFilters == null) {
			activeFilters = new ArrayList<>();
		}
		activeFilters.add(filter);
	}

	public void addDefinitionStatusFilter(FieldFilter filter) {
		if (definitionStatusFilters == null) {
			definitionStatusFilters = new ArrayList<>();
		}
		definitionStatusFilters.add(filter);
	}

	public void addEffectiveTimeFilter(EffectiveTimeFilter effectiveTimeFilter) {
		if (effectiveTimeFilters == null) {
			effectiveTimeFilters = new ArrayList<>();
		}
		effectiveTimeFilters.add(effectiveTimeFilter);
	}

	public void addModuleFilter(FieldFilter moduleFilter) {
		if (moduleFilters == null) {
			moduleFilters = new ArrayList<>();
		}
		moduleFilters.add(moduleFilter);
	}

	public List<FieldFilter> getDefinitionStatusFilters() {
		return definitionStatusFilters;
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
}
