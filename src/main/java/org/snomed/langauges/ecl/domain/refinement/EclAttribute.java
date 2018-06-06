package org.snomed.langauges.ecl.domain.refinement;

import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

public class EclAttribute implements Refinement {

	protected SubExpressionConstraint attributeName;
	protected String expressionComparisonOperator;
	protected SubExpressionConstraint value;
	protected boolean reverse;
	protected EclAttributeGroup parentGroup;
	protected Integer cardinalityMin = 1;
	protected Integer cardinalityMax;
	private String numericComparisonOperator;
	private String numericValue;
	private String stringComparisonOperator;
	private String stringValue;

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

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	@Override
	public String toString() {
		return "EclAttribute{" +
				"attributeName=" + attributeName +
				", expressionComparisonOperator='" + expressionComparisonOperator + '\'' +
				", value=" + value +
				", reverse=" + reverse +
				", cardinalityMin=" + cardinalityMin +
				", cardinalityMax=" + cardinalityMax +
				", withinGroup=" + (parentGroup != null) +
				", groupCardinalityMin=" + (parentGroup != null ? parentGroup.getCardinalityMin() : null) +
				", groupCardinalityMax=" + (parentGroup != null ? parentGroup.getCardinalityMax() : null) +
				", withinGroup=" + (parentGroup != null) +
				'}';
	}
}
