package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.List;

public class MemberFieldFilter {

	private String fieldName;

	private String expressionComparisonOperator;
	private SubExpressionConstraint subExpressionConstraint;

	private NumericComparisonOperator numericComparisonOperator;
	private String numericValue;

	private String stringComparisonOperator;
	private List<TypedSearchTerm> searchTerms;

	private String booleanComparisonOperator;
	private boolean booleanValue;

	private NumericComparisonOperator timeComparisonOperator;
	private List<Integer> timeValues;

	@SuppressWarnings("unused")
	protected MemberFieldFilter() {
		// For JSON
	}

	public MemberFieldFilter(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setExpressionComparisonOperator(String expressionComparisonOperator) {
		this.expressionComparisonOperator = expressionComparisonOperator;
	}

	public String getExpressionComparisonOperator() {
		return expressionComparisonOperator;
	}

	public void setSubExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}

	public void setNumericComparisonOperator(NumericComparisonOperator numericComparisonOperator) {
		this.numericComparisonOperator = numericComparisonOperator;
	}

	public NumericComparisonOperator getNumericComparisonOperator() {
		return numericComparisonOperator;
	}

	public void setNumericValue(String numericValue) {
		this.numericValue = numericValue;
	}

	public String getNumericValue() {
		return numericValue;
	}

	public void setStringComparisonOperator(String stringComparisonOperator) {
		this.stringComparisonOperator = stringComparisonOperator;
	}

	public String getStringComparisonOperator() {
		return stringComparisonOperator;
	}

	public void setSearchTerms(List<TypedSearchTerm> searchTerms) {
		this.searchTerms = searchTerms;
	}

	public List<TypedSearchTerm> getSearchTerms() {
		return searchTerms;
	}

	public void setBooleanComparisonOperator(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
	}

	public String getBooleanComparisonOperator() {
		return booleanComparisonOperator;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public boolean getBooleanValue() {
		return booleanValue;
	}

	public void setTimeComparisonOperator(NumericComparisonOperator timeComparisonOperator) {
		this.timeComparisonOperator = timeComparisonOperator;
	}

	public NumericComparisonOperator getTimeComparisonOperator() {
		return timeComparisonOperator;
	}

	public void setTimeValues(List<Integer> timeValues) {
		this.timeValues = timeValues;
	}

	public List<Integer> getTimeValues() {
		return timeValues;
	}

	@Override
	public String toString() {
		return "MemberFieldFilter{" +
				"fieldName='" + fieldName + '\'' +
				", expressionComparisonOperator='" + expressionComparisonOperator + '\'' +
				", subExpressionConstraint=" + subExpressionConstraint +
				", numericComparisonOperator=" + numericComparisonOperator +
				", numericValue='" + numericValue + '\'' +
				", stringComparisonOperator='" + stringComparisonOperator + '\'' +
				", searchTerms=" + searchTerms +
				", booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", booleanValue=" + booleanValue +
				", timeComparisonOperator=" + timeComparisonOperator +
				", timeValues=" + timeValues +
				'}';
	}
}
