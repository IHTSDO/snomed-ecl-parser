package org.snomed.langauges.ecl.domain.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterConstraint {

	private final List<Filter> filters;

	public FilterConstraint() {
		filters = new ArrayList<>();
	}

	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}

	public List<Filter> getAllFilters() {
		return filters;
	}

	private <T extends Filter> List<T> getFilters(Class<T> clazz) {
		return filters.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}

	public List<TermFilter> getTermFilters() {
		return getFilters(TermFilter.class);
	}

	public List<DescriptionTypeFilter> getDescriptionTypeFilters() {
		return getFilters(DescriptionTypeFilter.class);
	}

	public List<LanguageFilter> getLanguageFilters() {
		return getFilters(LanguageFilter.class);
	}

	public List<DialectFilter> getDialectFilters() {
		return getFilters(DialectFilter.class);
	}

}
