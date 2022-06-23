package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;

import java.util.List;

public class DialectAcceptability {

	// One of:
	private ConceptReference dialectId;
	private SubExpressionConstraint subExpressionConstraint;
	private String dialectAlias;

	// One of:
	private List<ConceptReference> acceptabilityIdSet;
	private List<Acceptability> acceptabilityTokenSet;

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

	public List<ConceptReference> getAcceptabilityIdSet() {
		return acceptabilityIdSet;
	}

	public void setAcceptabilityIdSet(List<ConceptReference> acceptabilityIdSet) {
		this.acceptabilityIdSet = acceptabilityIdSet;
	}

	public List<Acceptability> getAcceptabilityTokenSet() {
		return acceptabilityTokenSet;
	}

	public void setAcceptabilityTokenSet(List<Acceptability> acceptabilityTokenSet) {
		this.acceptabilityTokenSet = acceptabilityTokenSet;
	}

	@Override
	public String toString() {
		return "DialectAcceptability{" +
				"dialectId=" + dialectId +
				", subExpressionConstraint=" + subExpressionConstraint +
				", dialectAlias='" + dialectAlias + '\'' +
				", acceptabilityIdSet=" + acceptabilityIdSet +
				", acceptabilityTokenSet=" + acceptabilityTokenSet +
				'}';
	}
}
