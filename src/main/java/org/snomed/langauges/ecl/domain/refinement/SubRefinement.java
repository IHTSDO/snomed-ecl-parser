package org.snomed.langauges.ecl.domain.refinement;

public class SubRefinement implements Refinement {

	protected EclAttributeSet eclAttributeSet;
	protected EclAttributeGroup eclAttributeGroup;
	protected EclRefinement eclRefinement;

	public void setEclAttributeSet(EclAttributeSet eclAttributeSet) {
		this.eclAttributeSet = eclAttributeSet;
	}

	public void setEclAttributeGroup(EclAttributeGroup eclAttributeGroup) {
		this.eclAttributeGroup = eclAttributeGroup;
	}

	public void setEclRefinement(EclRefinement eclRefinement) {
		this.eclRefinement = eclRefinement;
	}

	public EclAttributeSet getEclAttributeSet() {
		return eclAttributeSet;
	}

	public EclAttributeGroup getEclAttributeGroup() {
		return eclAttributeGroup;
	}

	public EclRefinement getEclRefinement() {
		return eclRefinement;
	}

	@Override
	public String toString() {
		return "SubRefinement{" +
				"eclAttributeSet=" + eclAttributeSet +
				", eclAttributeGroup=" + eclAttributeGroup +
				", eclRefinement=" + eclRefinement +
				'}';
	}
}
