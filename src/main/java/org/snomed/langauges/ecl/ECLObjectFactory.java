package org.snomed.langauges.ecl;

import org.snomed.langauges.ecl.domain.expressionconstraint.CompoundExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.DottedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.RefinedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;
import org.snomed.langauges.ecl.domain.refinement.*;

public class ECLObjectFactory {

	protected RefinedExpressionConstraint getRefinedExpressionConstraint(SubExpressionConstraint subExpressionConstraint, EclRefinement eclRefinement) {
		return new RefinedExpressionConstraint(subExpressionConstraint, eclRefinement);
	}

	protected CompoundExpressionConstraint getCompoundExpressionConstraint() {
		return new CompoundExpressionConstraint();
	}

	protected DottedExpressionConstraint getDottedExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		return new DottedExpressionConstraint(subExpressionConstraint);
	}

	protected SubExpressionConstraint getSubExpressionConstraint(Operator operator) {
		return new SubExpressionConstraint(operator);
	}

	protected EclRefinement getRefinement() {
		return new EclRefinement();
	}

	protected SubRefinement getSubRefinement() {
		return new SubRefinement();
	}

	protected EclAttributeSet getEclAttributeSet() {
		return new EclAttributeSet();
	}

	protected SubAttributeSet getSubAttributeSet() {
		return new SubAttributeSet();
	}

	protected EclAttributeGroup getAttributeGroup() {
		return new EclAttributeGroup();
	}

	protected EclAttribute getAttribute() {
		return new EclAttribute();
	}
}
