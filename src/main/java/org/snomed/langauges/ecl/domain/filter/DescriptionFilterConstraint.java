package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;

public class DescriptionFilterConstraint {

	private List<TermFilter> termFilters;
	private List<DescriptionTypeFilter> descriptionTypeFilters;
	private List<LanguageFilter> languageFilters;
	private List<DialectFilter> dialectFilters;
	private List<FieldFilter> moduleFilters;
	private List<EffectiveTimeFilter> effectiveTimeFilters;
	private List<ActiveFilter> activeFilters;

	public void addFilter(TermFilter filter) {
		if (termFilters == null) {
			termFilters = new ArrayList<>();
		}
		termFilters.add(filter);
	}

	public void addFilter(DescriptionTypeFilter typeFilter) {
		if (descriptionTypeFilters == null) {
			descriptionTypeFilters = new ArrayList<>();
		}
		descriptionTypeFilters.add(typeFilter);
	}

	public void addFilter(LanguageFilter languageFilter) {
		if (languageFilters == null) {
			languageFilters = new ArrayList<>();
		}
		languageFilters.add(languageFilter);
	}

	public void addFilter(DialectFilter dialectFilter) {
		if (dialectFilters == null) {
			dialectFilters = new ArrayList<>();
		}
		dialectFilters.add(dialectFilter);
	}

	public void addFilter(FieldFilter moduleFilter) {
		if (moduleFilter == null) {
			moduleFilters = new ArrayList<>();
		}
		moduleFilters.add(moduleFilter);
	}

	public void addFilter(EffectiveTimeFilter effectiveTimeFilter) {
		if (effectiveTimeFilters == null) {
			effectiveTimeFilters = new ArrayList<>();
		}
		effectiveTimeFilters.add(effectiveTimeFilter);
	}

	public void addFilter(ActiveFilter activeFilter) {
		if (activeFilters == null) {
			activeFilters = new ArrayList<>();
		}
		activeFilters.add(activeFilter);
	}

	public List<TermFilter> getTermFilters() {
		return termFilters;
	}

	public List<DescriptionTypeFilter> getDescriptionTypeFilters() {
		return descriptionTypeFilters;
	}

	public List<LanguageFilter> getLanguageFilters() {
		return languageFilters;
	}

	public List<DialectFilter> getDialectFilters() {
		return dialectFilters;
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
		return "DescriptionFilterConstraint{" +
				"termFilters=" + termFilters +
				", descriptionTypeFilters=" + descriptionTypeFilters +
				", languageFilters=" + languageFilters +
				", dialectFilters=" + dialectFilters +
				", moduleFilters=" + moduleFilters +
				", effectiveTimeFilters=" + effectiveTimeFilters +
				", activeFilters=" + activeFilters +
				'}';
	}
}
