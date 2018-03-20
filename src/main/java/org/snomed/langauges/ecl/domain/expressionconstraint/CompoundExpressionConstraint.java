package org.snomed.langauges.ecl.domain.expressionconstraint;

import java.util.List;

public class CompoundExpressionConstraint implements ExpressionConstraint {

	protected List<SubExpressionConstraint> conjunctionExpressionConstraints;
	protected List<SubExpressionConstraint> disjunctionExpressionConstraints;
	protected SubExpressionConstraint exclusionExpressionConstraint;

	public void setConjunctionExpressionConstraints(List<SubExpressionConstraint> conjunctionExpressionConstraints) {
		this.conjunctionExpressionConstraints = conjunctionExpressionConstraints;
	}

	public void setDisjunctionExpressionConstraints(List<SubExpressionConstraint> disjunctionExpressionConstraints) {
		this.disjunctionExpressionConstraints = disjunctionExpressionConstraints;
	}

	public void setExclusionExpressionConstraint(SubExpressionConstraint exclusionExpressionConstraint) {
		this.exclusionExpressionConstraint = exclusionExpressionConstraint;
	}

	public List<SubExpressionConstraint> getConjunctionExpressionConstraints() {
		return conjunctionExpressionConstraints;
	}

	public List<SubExpressionConstraint> getDisjunctionExpressionConstraints() {
		return disjunctionExpressionConstraints;
	}

	public SubExpressionConstraint getExclusionExpressionConstraint() {
		return exclusionExpressionConstraint;
	}

	@Override
	public String toString() {
		return "CompoundExpressionConstraint{" +
				"conjunctionExpressionConstraints=" + conjunctionExpressionConstraints +
				", disjunctionExpressionConstraints=" + disjunctionExpressionConstraints +
				", exclusionExpressionConstraint=" + exclusionExpressionConstraint +
				'}';
	}
}
