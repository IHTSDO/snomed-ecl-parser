package org.snomed.langauges.ecl.domain.refinement;

import java.util.ArrayList;
import java.util.List;

public class EclRefinement implements Refinement {

	protected SubRefinement subRefinement;
	protected List<SubRefinement> conjunctionSubRefinements;
	protected List<SubRefinement> disjunctionSubRefinements;

	public EclRefinement() {
		conjunctionSubRefinements = new ArrayList<>();
	}

	public void setSubRefinement(SubRefinement subRefinement) {
		this.subRefinement = subRefinement;
	}

	public void setConjunctionSubRefinements(List<SubRefinement> conjunctionSubRefinements) {
		this.conjunctionSubRefinements = conjunctionSubRefinements;
	}

	public void setDisjunctionSubRefinements(List<SubRefinement> disjunctionSubRefinements) {
		this.disjunctionSubRefinements = disjunctionSubRefinements;
	}

	public SubRefinement getSubRefinement() {
		return subRefinement;
	}

	public List<SubRefinement> getConjunctionSubRefinements() {
		return conjunctionSubRefinements;
	}

	public List<SubRefinement> getDisjunctionSubRefinements() {
		return disjunctionSubRefinements;
	}

	@Override
	public String toString() {
		return "EclRefinement{" +
				"subRefinement=" + subRefinement +
				", conjunctionSubRefinements=" + conjunctionSubRefinements +
				", disjunctionSubRefinements=" + disjunctionSubRefinements +
				'}';
	}
}
