package org.snomed.langauges.ecl.domain.expressionconstraint;

import java.util.ArrayList;
import java.util.List;

public class DottedExpressionConstraint implements ExpressionConstraint {

	protected final SubExpressionConstraint subExpressionConstraint;
	protected final List<SubExpressionConstraint> dottedAttributes;

	public DottedExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
		dottedAttributes = new ArrayList<>();
	}

	public void addDottedAttribute(SubExpressionConstraint dottedAttribute) {
		dottedAttributes.add(dottedAttribute);
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}

	public List<SubExpressionConstraint> getDottedAttributes() {
		return dottedAttributes;
	}

	@Override
	public String toString() {
		return "DottedExpressionConstraint{" +
				"subExpressionConstraint=" + subExpressionConstraint +
				", dottedAttributes=" + dottedAttributes +
				'}';
	}
}
