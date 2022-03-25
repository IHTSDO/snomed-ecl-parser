package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.ArrayList;
import java.util.List;

public class FieldFilter {

	private String field;
	private boolean equals;
	private List<ConceptReference> conceptReferences;
	private SubExpressionConstraint subExpressionConstraint;

	private FieldFilter() {
	}

	public FieldFilter(String field, boolean equals) {
		this.field = field;
		this.equals = equals;
	}

	public void addConceptReference(ConceptReference conceptReference) {
		if (conceptReferences == null) {
			conceptReferences = new ArrayList<>();
		}
		conceptReferences.add(conceptReference);
	}

	public String getField() {
		return field;
	}

	public boolean isEquals() {
		return equals;
	}

	public List<ConceptReference> getConceptReferences() {
		return conceptReferences;
	}

	public void setSubExpressionConstraint(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}
}
