package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.Set;

public class DialectAcceptability {

	// One of:
	private ConceptReference dialectId;
	private SubExpressionConstraint subExpressionConstraint;
	private String dialectAlias;

	// One of:
	private Set<ConceptReference> acceptabilityIdSet;
	private Set<Acceptability> acceptabilityTokenSet;

	@SuppressWarnings("unused")
	// For JSON
	protected DialectAcceptability() {
	}

	public DialectAcceptability(ConceptReference dialectId) {
		this.dialectId = dialectId;
	}

	public DialectAcceptability(SubExpressionConstraint subExpressionConstraint) {
		this.subExpressionConstraint = subExpressionConstraint;
	}

	public DialectAcceptability(String dialectAlias) {
		this.dialectAlias = dialectAlias;
	}

	public ConceptReference getDialectId() {
		return dialectId;
	}

	public SubExpressionConstraint getSubExpressionConstraint() {
		return subExpressionConstraint;
	}

	public String getDialectAlias() {
		return dialectAlias;
	}

	public Set<ConceptReference> getAcceptabilityIdSet() {
		return acceptabilityIdSet;
	}

	public void setAcceptabilityIdSet(Set<ConceptReference> acceptabilityIdSet) {
		this.acceptabilityIdSet = acceptabilityIdSet;
	}

	public Set<Acceptability> getAcceptabilityTokenSet() {
		return acceptabilityTokenSet;
	}

	public void setAcceptabilityTokenSet(Set<Acceptability> acceptabilityTokenSet) {
		this.acceptabilityTokenSet = acceptabilityTokenSet;
	}

}
