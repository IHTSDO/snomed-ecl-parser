package org.snomed.langauges.ecl.domain.expressionconstraint;

import org.snomed.langauges.ecl.domain.refinement.EclRefinement;

public class RefinedExpressionConstraint implements ExpressionConstraint {

	protected final SubExpressionConstraint subexpressionConstraint;
	protected final EclRefinement eclRefinement;

	public RefinedExpressionConstraint(SubExpressionConstraint subExpressionConstraint, EclRefinement eclRefinement) {
		this.subexpressionConstraint = subExpressionConstraint;
		this.eclRefinement = eclRefinement;
	}

	public SubExpressionConstraint getSubexpressionConstraint() {
		return subexpressionConstraint;
	}

	public EclRefinement getEclRefinement() {
		return eclRefinement;
	}

	@Override
	public String toString() {
		return "RefinedExpressionConstraint{" +
				"subexpressionConstraint=" + subexpressionConstraint +
				", eclRefinement=" + eclRefinement +
				'}';
	}
}
