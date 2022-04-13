package org.snomed.langauges.ecl.domain.refinement;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;
import org.snomed.langauges.ecl.domain.filter.TypedSearchTerm;

import java.util.List;

public class EclAttribute implements Refinement {

	protected EclAttributeGroup parentGroup;

	protected Integer cardinalityMin = 1;
	protected Integer cardinalityMax;

	protected boolean reverse;
	protected SubExpressionConstraint attributeName;

	// Either
	protected String expressionComparisonOperator;
	protected SubExpressionConstraint value;

	// OR
	private String numericComparisonOperator;
	private String numericValue;

	// OR
	private String stringComparisonOperator;
	private List<TypedSearchTerm> stringValues;

	// OR
	private String booleanComparisonOperator;
	private Boolean booleanValue;

	public void setAttributeName(SubExpressionConstraint attributeName) {
		this.attributeName = attributeName;
	}

	public void setExpressionComparisonOperator(String expressionComparisonOperator) {
		this.expressionComparisonOperator = expressionComparisonOperator;
	}

	public void setValue(SubExpressionConstraint value) {
		this.value = value;
	}

	public void reverse() {
		this.reverse = true;
	}

	public void setCardinalityMin(Integer cardinalityMin) {
		this.cardinalityMin = cardinalityMin;
	}

	public void setCardinalityMax(Integer cardinalityMax) {
		this.cardinalityMax = cardinalityMax;
	}

	public SubExpressionConstraint getAttributeName() {
		return attributeName;
	}

	public String getExpressionComparisonOperator() {
		return expressionComparisonOperator;
	}

	public SubExpressionConstraint getValue() {
		return value;
	}

	public boolean isReverse() {
		return reverse;
	}

	public EclAttributeGroup getParentGroup() {
		return parentGroup;
	}

	public Integer getCardinalityMin() {
		return cardinalityMin;
	}

	public Integer getCardinalityMax() {
		return cardinalityMax;
	}

	public void setParentGroup(EclAttributeGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public void setNumericComparisonOperator(String numericComparisonOperator) {
		this.numericComparisonOperator = numericComparisonOperator;
	}

	public String getNumericComparisonOperator() {
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

	public void setStringValues(List<TypedSearchTerm> stringValues) {
		this.stringValues = stringValues;
	}

	public List<TypedSearchTerm> getStringValues() {
		return stringValues;
	}

	public void setBooleanComparisonOperator(String booleanComparisonOperator) {
		this.booleanComparisonOperator = booleanComparisonOperator;
	}

	public String getBooleanComparisonOperator() {
		return booleanComparisonOperator;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@Override
	public String toString() {
		return "EclAttribute{" +
				"parentGroup=" + parentGroup +
				", cardinalityMin=" + cardinalityMin +
				", cardinalityMax=" + cardinalityMax +
				", reverse=" + reverse +
				", attributeName=" + attributeName +
				", expressionComparisonOperator='" + expressionComparisonOperator + '\'' +
				", value=" + value +
				", numericComparisonOperator='" + numericComparisonOperator + '\'' +
				", numericValue='" + numericValue + '\'' +
				", stringComparisonOperator='" + stringComparisonOperator + '\'' +
				", stringValues=" + stringValues +
				", booleanComparisonOperator='" + booleanComparisonOperator + '\'' +
				", booleanValue=" + booleanValue +
				'}';
	}
}
