package org.snomed.langauges.ecl.domain.refinement;

public class EclAttributeGroup implements Refinement {

	protected EclAttributeSet attributeSet;
	protected Integer cardinalityMin;
	protected Integer cardinalityMax;

	public void setAttributeSet(EclAttributeSet attributeSet) {
		this.attributeSet = attributeSet;
	}

	public void setCardinalityMin(int cardinalityMin) {
		this.cardinalityMin = cardinalityMin;
	}

	public Integer getCardinalityMin() {
		return cardinalityMin;
	}

	public void setCardinalityMax(int cardinalityMax) {
		this.cardinalityMax = cardinalityMax;
	}

	public Integer getCardinalityMax() {
		return cardinalityMax;
	}

	public EclAttributeSet getAttributeSet() {
		return attributeSet;
	}

	@Override
	public String toString() {
		return "EclAttributeGroup{" +
				"attributeSet=" + attributeSet +
				", cardinalityMin=" + cardinalityMin +
				", cardinalityMax=" + cardinalityMax +
				'}';
	}

}
