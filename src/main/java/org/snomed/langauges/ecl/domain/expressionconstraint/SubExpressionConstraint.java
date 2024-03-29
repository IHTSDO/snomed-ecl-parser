package org.snomed.langauges.ecl.domain.expressionconstraint;

import org.snomed.langauges.ecl.domain.filter.ConceptFilterConstraint;
import org.snomed.langauges.ecl.domain.filter.DescriptionFilterConstraint;
import org.snomed.langauges.ecl.domain.filter.HistorySupplement;
import org.snomed.langauges.ecl.domain.filter.MemberFilterConstraint;
import org.snomed.langauges.ecl.domain.refinement.Operator;

import java.util.ArrayList;
import java.util.List;

public class SubExpressionConstraint implements ExpressionConstraint {

	protected final Operator operator;
	protected String conceptId;
	protected String term;
	protected boolean wildcard;
	protected ExpressionConstraint nestedExpressionConstraint;
	protected List<ConceptFilterConstraint> conceptFilterConstraints;
	protected List<DescriptionFilterConstraint> descriptionFilterConstraints;
	protected List<MemberFilterConstraint> memberFilterConstraints;
	private HistorySupplement historySupplement;
	private List<String> memberFieldsToReturn;
	private boolean returnAllMemberFields;

	public SubExpressionConstraint(Operator operator) {
		this.operator = operator;
	}

	public void addConceptFilterConstraint(ConceptFilterConstraint conceptFilterConstraint) {
		if (conceptFilterConstraints == null) {
			conceptFilterConstraints = new ArrayList<>();
		}
		conceptFilterConstraints.add(conceptFilterConstraint);
	}

	public void addDescriptionFilterConstraint(DescriptionFilterConstraint descriptionFilterConstraint) {
		if (descriptionFilterConstraints == null) {
			descriptionFilterConstraints = new ArrayList<>();
		}
		descriptionFilterConstraints.add(descriptionFilterConstraint);
	}

	public void addMemberFilterConstraint(MemberFilterConstraint memberFilterConstraint) {
		if (memberFilterConstraints == null) {
			memberFilterConstraints = new ArrayList<>();
		}
		memberFilterConstraints.add(memberFilterConstraint);
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public void setTerm(String term) { this.term = term; }

	public void setWildcard(boolean wildcard) {
		this.wildcard = wildcard;
	}

	public void setNestedExpressionConstraint(ExpressionConstraint nestedExpressionConstraint) {
		this.nestedExpressionConstraint = nestedExpressionConstraint;
	}

	public void setDescriptionFilterConstraints(List<DescriptionFilterConstraint> descriptionFilterConstraints) {
		this.descriptionFilterConstraints = descriptionFilterConstraints;
	}

	public void setMemberFilterConstraints(List<MemberFilterConstraint> memberFilterConstraints) {
		this.memberFilterConstraints = memberFilterConstraints;
	}

	public void setHistorySupplement(HistorySupplement historySupplement) {
		this.historySupplement = historySupplement;
	}

	public void setMemberFieldsToReturn(List<String> memberFieldsToReturn) {
		this.memberFieldsToReturn = memberFieldsToReturn;
	}

	public void setReturnAllMemberFields(boolean returnAllMemberFields) {
		this.returnAllMemberFields = returnAllMemberFields;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getConceptId() {
		return conceptId;
	}

	public String getTerm() { return term; }

	public boolean isWildcard() {
		return wildcard;
	}

	public ExpressionConstraint getNestedExpressionConstraint() {
		return nestedExpressionConstraint;
	}

	public List<ConceptFilterConstraint> getConceptFilterConstraints() {
		return conceptFilterConstraints;
	}

	public List<DescriptionFilterConstraint> getDescriptionFilterConstraints() {
		return descriptionFilterConstraints;
	}

	public List<MemberFilterConstraint> getMemberFilterConstraints() {
		return memberFilterConstraints;
	}

	public HistorySupplement getHistorySupplement() {
		return historySupplement;
	}

	public List<String> getMemberFieldsToReturn() {
		return memberFieldsToReturn;
	}

	public boolean isReturnAllMemberFields() {
		return returnAllMemberFields;
	}

	@Override
	public String toString() {
		return "SubExpressionConstraint{" +
				"operator=" + operator +
				", conceptId='" + conceptId + '\'' +
				", term='" + term + '\'' +
				", wildcard=" + wildcard +
				", nestedExpressionConstraint=" + nestedExpressionConstraint +
				", conceptFilterConstraints=" + conceptFilterConstraints +
				", descriptionFilterConstraints=" + descriptionFilterConstraints +
				", memberFilterConstraints=" + memberFilterConstraints +
				", historySupplement=" + historySupplement +
				", memberFieldsToReturn=" + memberFieldsToReturn +
				", returnAllMemberFields=" + returnAllMemberFields +
				'}';
	}
}
