package org.snomed.langauges.ecl.domain.expressionconstraint;

import org.snomed.langauges.ecl.domain.Pair;

import java.util.List;

public class CompoundExpressionConstraint implements ExpressionConstraint {

	protected List<SubExpressionConstraint> conjunctionExpressionConstraints;
	protected List<SubExpressionConstraint> disjunctionExpressionConstraints;
	protected Pair<SubExpressionConstraint> exclusionExpressionConstraints;

	public void setConjunctionExpressionConstraints(List<SubExpressionConstraint> conjunctionExpressionConstraints) {
		this.conjunctionExpressionConstraints = conjunctionExpressionConstraints;
	}

	public void setDisjunctionExpressionConstraints(List<SubExpressionConstraint> disjunctionExpressionConstraints) {
		this.disjunctionExpressionConstraints = disjunctionExpressionConstraints;
	}

	public void setExclusionExpressionConstraints(Pair<SubExpressionConstraint> exclusionExpressionConstraints) {
		this.exclusionExpressionConstraints = exclusionExpressionConstraints;
	}

	public List<SubExpressionConstraint> getConjunctionExpressionConstraints() {
		return conjunctionExpressionConstraints;
	}

	public List<SubExpressionConstraint> getDisjunctionExpressionConstraints() {
		return disjunctionExpressionConstraints;
	}

	public Pair<SubExpressionConstraint> getExclusionExpressionConstraints() {
		return exclusionExpressionConstraints;
	}

	@Override
	public String toString() {
		return "CompoundExpressionConstraint{" +
				"conjunctionExpressionConstraints=" + conjunctionExpressionConstraints +
				", disjunctionExpressionConstraints=" + disjunctionExpressionConstraints +
				", exclusionExpressionConstraints=" + exclusionExpressionConstraints +
				'}';
	}
}
