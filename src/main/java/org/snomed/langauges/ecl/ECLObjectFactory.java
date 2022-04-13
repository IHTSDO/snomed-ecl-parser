package org.snomed.langauges.ecl;

import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.expressionconstraint.CompoundExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.DottedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.RefinedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;
import org.snomed.langauges.ecl.domain.filter.*;
import org.snomed.langauges.ecl.domain.refinement.*;

import java.util.Set;

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

	public ConceptFilterConstraint getConceptFilterConstraint() {
		return new ConceptFilterConstraint();
	}

	public FieldFilter getFieldFilter(String fieldName, boolean equals) {
		return new FieldFilter(fieldName, equals);
	}

	public EffectiveTimeFilter getEffectiveTimeFilter(NumericComparisonOperator operator, Set<Integer> effectiveTimes) {
		return new EffectiveTimeFilter(operator, effectiveTimes);
	}

	public DescriptionFilterConstraint getDescriptionFilterConstraint() {
		return new DescriptionFilterConstraint();
	}

	public TypedSearchTerm getTypedSearchTerm(SearchType searchType, String text) {
		return new TypedSearchTerm(searchType, text);
	}

	public DialectFilter getDialectFilter(String text, boolean dialectAliasFilter) {
		return new DialectFilter(text, dialectAliasFilter);
	}

	public ActiveFilter getActiveFilter(boolean b) {
		return new ActiveFilter(b);
	}

	public TermFilter getTermFilter(String text) {
		return new TermFilter(text);
	}

	public DescriptionTypeFilter getDescriptionTypeFilter(String text) {
		return new DescriptionTypeFilter(text);
	}

	public LanguageFilter getLanguageFilter(String text) {
		return new LanguageFilter(text);
	}

	public DialectAcceptability getDialectAcceptability(ConceptReference conceptReference) {
		return new DialectAcceptability(conceptReference);
	}

	public DialectAcceptability getDialectAcceptability(SubExpressionConstraint expressionConstraint) {
		return new DialectAcceptability(expressionConstraint);
	}

	public DialectAcceptability getDialectAcceptability(String alias) {
		return new DialectAcceptability(alias);
	}

	public MemberFilterConstraint getMemberFilterConstraint() {
		return new MemberFilterConstraint();
	}

	public MemberFieldFilter getMemberFieldFilter(String fieldName) {
		return new MemberFieldFilter(fieldName);
	}

	public HistorySupplement getHistorySupplement() {
		return new HistorySupplement();
	}
}
