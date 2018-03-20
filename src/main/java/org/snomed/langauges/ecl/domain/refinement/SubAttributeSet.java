package org.snomed.langauges.ecl.domain.refinement;

public class SubAttributeSet implements Refinement {

	protected EclAttribute attribute;
	protected EclAttributeSet attributeSet;

	public void setAttribute(EclAttribute attribute) {
		this.attribute = attribute;
	}

	public void setAttributeSet(EclAttributeSet attributeSet) {
		this.attributeSet = attributeSet;
	}

	public EclAttribute getAttribute() {
		return attribute;
	}

	public EclAttributeSet getAttributeSet() {
		return attributeSet;
	}

	@Override
	public String toString() {
		return "SubAttributeSet{" +
				"attribute=" + attribute +
				", attributeSet=" + attributeSet +
				'}';
	}
}
