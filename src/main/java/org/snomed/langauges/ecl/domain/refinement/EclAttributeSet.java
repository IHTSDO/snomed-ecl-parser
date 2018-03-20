package org.snomed.langauges.ecl.domain.refinement;

import java.util.List;

public class EclAttributeSet implements Refinement {

	protected EclAttributeGroup parentGroup;
	protected SubAttributeSet subAttributeSet;
	protected List<SubAttributeSet> conjunctionAttributeSet;
	protected List<SubAttributeSet> disjunctionAttributeSet;

	public void setSubAttributeSet(SubAttributeSet subAttributeSet) {
		this.subAttributeSet = subAttributeSet;
	}

	public SubAttributeSet getSubAttributeSet() {
		return subAttributeSet;
	}

	public void setConjunctionAttributeSet(List<SubAttributeSet> conjunctionAttributeSet) {
		this.conjunctionAttributeSet = conjunctionAttributeSet;
	}

	public void setDisjunctionAttributeSet(List<SubAttributeSet> disjunctionAttributeSet) {
		this.disjunctionAttributeSet = disjunctionAttributeSet;
	}

	public EclAttributeGroup getParentGroup() {
		return parentGroup;
	}

	public List<SubAttributeSet> getConjunctionAttributeSet() {
		return conjunctionAttributeSet;
	}

	public List<SubAttributeSet> getDisjunctionAttributeSet() {
		return disjunctionAttributeSet;
	}

	@Override
	public String toString() {
		return "EclAttributeSet{" +
				"withinGroup=" + (parentGroup != null) +
				", subAttributeSet=" + subAttributeSet +
				", conjunctionAttributeSet=" + conjunctionAttributeSet +
				", disjunctionAttributeSet=" + disjunctionAttributeSet +
				'}';
	}

	public void setParentGroup(EclAttributeGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

}
