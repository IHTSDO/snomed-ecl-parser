package org.snomed.langauges.ecl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.expressionconstraint.*;
import org.snomed.langauges.ecl.domain.filter.*;
import org.snomed.langauges.ecl.domain.refinement.*;
import org.snomed.langauges.ecl.generated.ImpotentECLListener;
import org.snomed.langauges.ecl.generated.parser.ECLLexer;
import org.snomed.langauges.ecl.generated.parser.ECLParser;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ECLQueryBuilder {

	private final ECLObjectFactory eclObjectFactory;

	public ECLQueryBuilder(ECLObjectFactory eclObjectFactory) {
		this.eclObjectFactory = eclObjectFactory;
	}

	public ExpressionConstraint createQuery(String ecl) throws ECLException {
		ANTLRInputStream inputStream = new ANTLRInputStream(ecl);
		final ECLLexer lexer = new ECLLexer(inputStream);
		final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		final ECLParser parser = new ECLParser(tokenStream);
		parser.setErrorHandler(new BailErrorStrategy());

		ParserRuleContext tree;
		try {
			tree = parser.expressionconstraint();
		} catch (NullPointerException | RecognitionException | ParseCancellationException e) {
			throw new ECLException("Failed to parse ECL '" + ecl + "'", e);
		}
		final ParseTreeWalker walker = new ParseTreeWalker();
		final ECLListenerImpl listener = new ECLListenerImpl(eclObjectFactory);
		walker.walk(listener, tree);

		return listener.getRootExpressionConstraint();
	}

	private static final class ECLListenerImpl extends ImpotentECLListener {

		private final ECLObjectFactory eclObjectFactory;
		private ExpressionConstraint rootExpressionConstraint;

		private ECLListenerImpl(ECLObjectFactory eclObjectFactory) {
			this.eclObjectFactory = eclObjectFactory;
		}

		@Override
		public void enterExpressionconstraint(ECLParser.ExpressionconstraintContext ctx) {
			ExpressionConstraint expressionConstraint = build(ctx);
			if (rootExpressionConstraint == null) {
				rootExpressionConstraint = expressionConstraint;
			}
		}

		private ExpressionConstraint build(ECLParser.ExpressionconstraintContext expressionConstraint) {
			if (expressionConstraint.refinedexpressionconstraint() != null) {
				return build(expressionConstraint.refinedexpressionconstraint());
			}
			if (expressionConstraint.compoundexpressionconstraint() != null) {
				return build(expressionConstraint.compoundexpressionconstraint());
			}
			if (expressionConstraint.dottedexpressionconstraint() != null) {
				return build(expressionConstraint.dottedexpressionconstraint());
			}
			if (expressionConstraint.subexpressionconstraint() != null) {
				return build(expressionConstraint.subexpressionconstraint());
			}
			return null;
		}

		public RefinedExpressionConstraint build(ECLParser.RefinedexpressionconstraintContext ctx) {
			SubExpressionConstraint subExpressionConstraint = build(ctx.subexpressionconstraint());
			EclRefinement eclRefinement = build(ctx.eclrefinement());
			return eclObjectFactory.getRefinedExpressionConstraint(subExpressionConstraint, eclRefinement);
		}

		public CompoundExpressionConstraint build(ECLParser.CompoundexpressionconstraintContext ctx) {
			CompoundExpressionConstraint compoundExpressionConstraint = eclObjectFactory.getCompoundExpressionConstraint();
			if (ctx.conjunctionexpressionconstraint() != null) {
				compoundExpressionConstraint.setConjunctionExpressionConstraints(build(ctx.conjunctionexpressionconstraint().subexpressionconstraint()));
			} else if (ctx.disjunctionexpressionconstraint() != null) {
				compoundExpressionConstraint.setDisjunctionExpressionConstraints(build(ctx.disjunctionexpressionconstraint().subexpressionconstraint()));
			} else if (ctx.exclusionexpressionconstraint() != null) {
				compoundExpressionConstraint.setConjunctionExpressionConstraints(Collections.singletonList(build(ctx.exclusionexpressionconstraint().subexpressionconstraint(0))));
				compoundExpressionConstraint.setExclusionExpressionConstraint(build(ctx.exclusionexpressionconstraint().subexpressionconstraint(1)));
			}
			return compoundExpressionConstraint;
		}

		public DottedExpressionConstraint build(ECLParser.DottedexpressionconstraintContext ctx) {
			SubExpressionConstraint subExpressionConstraint = build(ctx.subexpressionconstraint());
			DottedExpressionConstraint dottedExpressionConstraint = eclObjectFactory.getDottedExpressionConstraint(subExpressionConstraint);
			for (ECLParser.DottedexpressionattributeContext dotCtx : ctx.dottedexpressionattribute()) {
				SubExpressionConstraint attributeSubExpressionConstraint = build(dotCtx.eclattributename().subexpressionconstraint());
				dottedExpressionConstraint.addDottedAttribute(attributeSubExpressionConstraint);
			}
			return dottedExpressionConstraint;
		}

		private List<SubExpressionConstraint> build(List<ECLParser.SubexpressionconstraintContext> subExpressionConstraints) {
			return subExpressionConstraints.stream().map(this::build).collect(Collectors.toList());
		}

		private SubExpressionConstraint build(ECLParser.SubexpressionconstraintContext ctx) {
			Operator operator = ctx.constraintoperator() != null ? Operator.textLookup(ctx.constraintoperator().getText()) : null;
			if (ctx.memberof() != null) {
				operator = Operator.memberOf;
			}

			SubExpressionConstraint subExpressionConstraint = eclObjectFactory.getSubExpressionConstraint(operator);

			ECLParser.EclfocusconceptContext eclFocusConcept = ctx.eclfocusconcept();
			if (eclFocusConcept != null) {
				if (eclFocusConcept.wildcard() != null) {
					subExpressionConstraint.setWildcard(true);
				}
				if (eclFocusConcept.eclconceptreference() != null) {
					if (eclFocusConcept.eclconceptreference().term() != null) {
						subExpressionConstraint.setTerm(eclFocusConcept.eclconceptreference().term().getText());
					}
					subExpressionConstraint.setConceptId(eclFocusConcept.eclconceptreference().conceptid().getText());
				}
			} else {
				subExpressionConstraint.setNestedExpressionConstraint(build(ctx.expressionconstraint()));
			}
			// add filter constraint if available
			if (ctx.filterconstraint() != null) {
				for (ECLParser.FilterconstraintContext filterconstraintContext : ctx.filterconstraint()) {
					if (filterconstraintContext.conceptfilterconstraint() != null) {
						ConceptFilterConstraint conceptFilterConstraint = buildConceptFilterConstraint(filterconstraintContext.conceptfilterconstraint().conceptfilter());
						subExpressionConstraint.addConceptFilterConstraint(conceptFilterConstraint);
					} else {
						DescriptionFilterConstraint descriptionFilterConstraint = buildFilterConstraint(filterconstraintContext.descriptionfilterconstraint().descriptionfilter());
						subExpressionConstraint.addDescriptionFilterConstraint(descriptionFilterConstraint);
					}
				}
			}
			return subExpressionConstraint;
		}

		private ConceptReference build(ECLParser.EclconceptreferenceContext eclconceptreference) {
			ConceptReference conceptReference = new ConceptReference(eclconceptreference.conceptid().getText());
			if (eclconceptreference.term() != null) {
				conceptReference.setTerm(eclconceptreference.term().getText());
			}
			return conceptReference;
		}

		private ConceptFilterConstraint buildConceptFilterConstraint(List<ECLParser.ConceptfilterContext> conceptfilter) {
			ConceptFilterConstraint constraint = new ConceptFilterConstraint();
			for (ECLParser.ConceptfilterContext conceptfilterContext : conceptfilter) {
				if (conceptfilterContext.definitionstatusfilter() != null) {
					constraint.addDefinitionStatusFilter(buildFilter(conceptfilterContext.definitionstatusfilter()));
				}
				if (conceptfilterContext.modulefilter() != null) {
					constraint.addModuleFilter(buildFilter(conceptfilterContext.modulefilter()));
				}
				if (conceptfilterContext.effectivetimefilter() != null) {
					constraint.addEffectiveTimeFilter(buildFilter(conceptfilterContext.effectivetimefilter()));
				}
				if (conceptfilterContext.activefilter() != null) {
					constraint.addActiveFilter(buildFilter(conceptfilterContext.activefilter()));
				}
			}
			return constraint;
		}

		private FieldFilter buildFilter(ECLParser.DefinitionstatusfilterContext definitionstatusfilter) {
			if (definitionstatusfilter.definitionstatusidfilter() != null) {
				ECLParser.DefinitionstatusidfilterContext idFilter = definitionstatusfilter.definitionstatusidfilter();
				FieldFilter fieldFilter = new FieldFilter("definitionStatusId", idFilter.booleancomparisonoperator().getText().equals("="));
				if (idFilter.subexpressionconstraint() != null) {
					fieldFilter.setSubExpressionConstraint(build(idFilter.subexpressionconstraint()));
				} else {
					for (ECLParser.EclconceptreferenceContext eclconceptreferenceContext : idFilter.eclconceptreferenceset().eclconceptreference()) {
						fieldFilter.addConceptReference(build(eclconceptreferenceContext));
					}
				}
				return fieldFilter;
			} else {
				ECLParser.DefinitionstatustokenfilterContext tokenFilter = definitionstatusfilter.definitionstatustokenfilter();
				FieldFilter fieldFilter = new FieldFilter("definitionStatusId", tokenFilter.booleancomparisonoperator().getText().equals("="));
				if (tokenFilter.definitionstatustoken() != null) {
					if (tokenFilter.definitionstatustoken().definedtoken() != null) {
						fieldFilter.addConceptReference(new ConceptReference("900000000000073002", "Defined"));
					} else {
						fieldFilter.addConceptReference(new ConceptReference("900000000000074008", "Primitive"));
					}
				}
				return fieldFilter;
			}
		}

		private FieldFilter buildFilter(ECLParser.ModulefilterContext modulefilter) {
			FieldFilter fieldFilter = new FieldFilter("moduleId", modulefilter.booleancomparisonoperator().getText().equals("="));
			if (modulefilter.subexpressionconstraint() != null) {
				fieldFilter.setSubExpressionConstraint(build(modulefilter.subexpressionconstraint()));
			} else {
				for (ECLParser.EclconceptreferenceContext eclconceptreferenceContext : modulefilter.eclconceptreferenceset().eclconceptreference()) {
					fieldFilter.addConceptReference(build(eclconceptreferenceContext));
				}
			}
			return fieldFilter;
		}

		private EffectiveTimeFilter buildFilter(ECLParser.EffectivetimefilterContext effectivetimefilter) {
			TimeComparisonOperator operator = TimeComparisonOperator.fromGrammar(effectivetimefilter.timecomparisonoperator().getText());
			Set<Integer> effectiveTimes;
			if (effectivetimefilter.timevalue() != null) {
				effectiveTimes = Collections.singleton(getEffectiveTime(effectivetimefilter.timevalue()));
			} else {
				effectiveTimes = effectivetimefilter.timevalueset().timevalue().stream()
						.map(this::getEffectiveTime).collect(Collectors.toSet());
			}
			return new EffectiveTimeFilter(operator, effectiveTimes);
		}

		private int getEffectiveTime(ECLParser.TimevalueContext timevalue) {
			return Integer.parseInt(timevalue.toString());
		}

		private ActiveFilter buildFilter(ECLParser.ActivefilterContext activefilter) {
			return new ActiveFilter(activefilter.booleancomparisonoperator().getText().equals("=") == (activefilter.activevalue().activetruevalue() != null));
		}

		private DescriptionFilterConstraint buildFilterConstraint(List<ECLParser.DescriptionfilterContext> descriptionfilter) {
			DescriptionFilterConstraint constraint = new DescriptionFilterConstraint();
			for (ECLParser.DescriptionfilterContext filter : descriptionfilter) {
				if (filter.termfilter() != null) {
					constraint.addFilter(buildFilter(filter.termfilter()));
				}
				if (filter.typefilter() != null) {
					constraint.addFilter(buildFilter(filter.typefilter()));
				}
				if (filter.languagefilter() != null) {
					constraint.addFilter(buildFilter(filter.languagefilter()));
				}
				if (filter.dialectfilter() != null) {
					constraint.addFilter(buildFilter(filter.dialectfilter()));
				}
			}
			return constraint;
		}

		private TermFilter buildFilter(ECLParser.TermfilterContext termFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = termFilterContext.booleancomparisonoperator();
			ECLParser.TypedsearchtermContext typedsearchterm = termFilterContext.typedsearchterm();
			ECLParser.TypedsearchtermsetContext typedsearchtermset = termFilterContext.typedsearchtermset();

			TermFilter filter = new TermFilter(booleancomparisonoperator.getText());
			if (typedsearchterm != null) {
				filter.addTypedSearchTerm(build(typedsearchterm));
			} else {
				for (ECLParser.TypedsearchtermContext typedsearchtermContext : typedsearchtermset.typedsearchterm()) {
					filter.addTypedSearchTerm(build(typedsearchtermContext));
				}
			}
			return filter;
		}

		private TypedSearchTerm build(ECLParser.TypedsearchtermContext typedsearchterm) {
			if (typedsearchterm.wild() != null) {
				return new TypedSearchTerm(SearchType.WILDCARD, typedsearchterm.wildsearchtermset().wildsearchterm().getText());
			} else {
				// Match is the default
				StringBuilder buffer = new StringBuilder();
				for (ECLParser.MatchsearchtermContext matchsearchtermContext : typedsearchterm.matchsearchtermset().matchsearchterm()) {
					if (buffer.length() > 0) {
						buffer.append(" ");
					}
					buffer.append(matchsearchtermContext.getText());
				}
				return new TypedSearchTerm(SearchType.MATCH, buffer.toString());
			}
		}

		private Filter buildFilter(ECLParser.TypefilterContext typeFilterContext) {
			if (typeFilterContext.typeidfilter() != null) {
				DescriptionTypeFilter typeFilter = new DescriptionTypeFilter(typeFilterContext.typeidfilter().booleancomparisonoperator().getText());
				if (typeFilterContext.typeidfilter().eclconceptreferenceset() != null) {
					typeFilterContext.typeidfilter().eclconceptreferenceset().eclconceptreference()
							.forEach(concept -> typeFilter.addType(DescriptionType.getTypeById(concept.conceptid().getText())));
				} else if (typeFilterContext.typeidfilter().subexpressionconstraint() != null) {
					typeFilter.setSubExpressionConstraint(build(typeFilterContext.typeidfilter().subexpressionconstraint()));
				}
				return typeFilter;
			} else if (typeFilterContext.typetokenfilter() != null) {
				DescriptionTypeFilter typeFilter = new DescriptionTypeFilter(typeFilterContext.typetokenfilter().booleancomparisonoperator().getText());
				if (typeFilterContext.typetokenfilter().typetokenset() != null) {
					typeFilterContext.typetokenfilter().typetokenset().typetoken().forEach(token ->
						typeFilter.addType(DescriptionType.getTypeByToken(token.getText())));
				} else if (typeFilterContext.typetokenfilter().typetoken() != null) {
					typeFilter.addType(DescriptionType.getTypeByToken(typeFilterContext.typetokenfilter().typetoken().getText()));
				}
				return typeFilter;
			}
			return null;
		}

		private Filter buildFilter(ECLParser.LanguagefilterContext languageFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = languageFilterContext.booleancomparisonoperator();
			ECLParser.LanguagecodeContext languagecode = languageFilterContext.languagecode();
			ECLParser.LanguagecodesetContext languagecodeset = languageFilterContext.languagecodeset();

			LanguageFilter languageFilter = new LanguageFilter(booleancomparisonoperator.getText());
			if (languagecode != null) {
				languageFilter.addLanguageCode(languagecode.getText());
			} else {
				languagecodeset.languagecode().forEach(languageCode -> languageFilter.addLanguageCode(languageCode.getText()));
			}
			return languageFilter;
		}

		private Filter buildFilter(ECLParser.DialectfilterContext dialectFilterContext) {
			// Either this
			ECLParser.DialectidfilterContext dialectidfilter = dialectFilterContext.dialectidfilter();
			// Or this
			ECLParser.DialectaliasfilterContext dialectaliasfilter = dialectFilterContext.dialectaliasfilter();

			// Could be null, if not null apply to all DialectAcceptability
			ECLParser.AcceptabilitysetContext acceptabilityset = dialectFilterContext.acceptabilityset();

			DialectFilter dialectFilter;
			if (dialectidfilter != null) {
				dialectFilter = buildFilter(dialectidfilter);
			} else {
				dialectFilter = buildFilter(dialectaliasfilter);
			}
			if (acceptabilityset != null) {
				// Apply to all
				apply(dialectFilter.getDialectAcceptabilities(), 0, acceptabilityset);
			}
			return dialectFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectidfilterContext dialectIdFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = dialectIdFilterContext.booleancomparisonoperator();
			// Either
			ECLParser.SubexpressionconstraintContext subexpressionconstraint = dialectIdFilterContext.subexpressionconstraint();
			// Or
			ECLParser.DialectidsetContext dialectidset = dialectIdFilterContext.dialectidset();

			final DialectFilter dialectFilter = new DialectFilter(booleancomparisonoperator.getText());
			if (subexpressionconstraint != null) {
				dialectFilter.addDialect(new DialectAcceptability(build(subexpressionconstraint)));
			} else {
				for (ECLParser.EclconceptreferenceContext eclconceptreferenceContext : dialectidset.eclconceptreference()) {
					dialectFilter.addDialect(new DialectAcceptability(build(eclconceptreferenceContext)));
				}
				int i = 0;
				for (ECLParser.AcceptabilitysetContext acceptabilitysetContext : dialectidset.acceptabilityset()) {
					apply(dialectFilter.getDialectAcceptabilities(), i++, acceptabilitysetContext);
				}
			}
			return dialectFilter;
		}

		private void apply(List<DialectAcceptability> dialectAcceptabilities, int startIndex, ECLParser.AcceptabilitysetContext acceptabilitysetContext) {
			for (int i = startIndex; i < dialectAcceptabilities.size(); i++) {
				DialectAcceptability dialectAcceptability = dialectAcceptabilities.get(i);
				ECLParser.EclconceptreferencesetContext eclconceptreferenceset = acceptabilitysetContext.eclconceptreferenceset();
				ECLParser.AcceptabilitytokensetContext acceptabilitytokenset = acceptabilitysetContext.acceptabilitytokenset();
				if (eclconceptreferenceset != null) {
					dialectAcceptability.setAcceptabilityIdSet(buildConceptReferences(eclconceptreferenceset.eclconceptreference()));
				} else if (acceptabilitytokenset != null) {
					dialectAcceptability.setAcceptabilityTokenSet(build(acceptabilitytokenset));
				}
			}
		}

		private Set<ConceptReference> buildConceptReferences(List<ECLParser.EclconceptreferenceContext> eclconceptreference) {
			return eclconceptreference.stream().map(this::build).collect(Collectors.toSet());
		}

		private Set<Acceptability> build(ECLParser.AcceptabilitytokensetContext acceptabilitytokenset) {
			return acceptabilitytokenset.acceptabilitytoken().stream().map(this::build).collect(Collectors.toSet());
		}

		private Acceptability build(ECLParser.AcceptabilitytokenContext acceptabilitytokenContext) {
			if (acceptabilitytokenContext.acceptable() != null) {
				return Acceptability.ACCEPTABLE;
			} else if (acceptabilitytokenContext.preferred() != null) {
				return Acceptability.PREFERRED;
			}
			return null;
		}

		private DialectFilter buildFilter(ECLParser.DialectaliasfilterContext dialectAliasFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = dialectAliasFilterContext.booleancomparisonoperator();
			// Either
			ECLParser.DialectaliasContext dialectalias = dialectAliasFilterContext.dialectalias();
			// Or
			ECLParser.DialectaliassetContext dialectaliasset = dialectAliasFilterContext.dialectaliasset();

			DialectFilter dialectFilter = new DialectFilter(booleancomparisonoperator.getText());
			if (dialectalias != null) {
				dialectFilter.addDialect(new DialectAcceptability(dialectalias.getText()));
			} else {
				List<ECLParser.DialectaliasContext> dialectaliasList = dialectaliasset.dialectalias();
				List<ECLParser.AcceptabilitysetContext> acceptabilityset = dialectaliasset.acceptabilityset();

				for (ECLParser.DialectaliasContext dialectaliasContext : dialectaliasList) {
					dialectFilter.addDialect(new DialectAcceptability(dialectaliasContext.getText()));
				}
				int i = 0;
				for (ECLParser.AcceptabilitysetContext acceptabilitysetContext : acceptabilityset) {
					apply(dialectFilter.getDialectAcceptabilities(), i++, acceptabilitysetContext);
				}
			}
			return dialectFilter;
		}

		private EclRefinement build(ECLParser.EclrefinementContext ctx) {
			if (ctx == null) {
				return null;
			}
			EclRefinement refinement = eclObjectFactory.getRefinement();
			refinement.setSubRefinement(build(ctx.subrefinement()));
			if (ctx.conjunctionrefinementset() != null) {
				refinement.setConjunctionSubRefinements(buildSubRefinements(ctx.conjunctionrefinementset().subrefinement()));
			}
			if (ctx.disjunctionrefinementset() != null) {
				refinement.setDisjunctionSubRefinements(buildSubRefinements(ctx.disjunctionrefinementset().subrefinement()));
			}
			return refinement;
		}

		private List<SubRefinement> buildSubRefinements(List<ECLParser.SubrefinementContext> subRefinements) {
			return subRefinements.stream().map(this::build).collect(Collectors.toList());
		}

		private SubRefinement build(ECLParser.SubrefinementContext ctx) {
			SubRefinement subRefinement = eclObjectFactory.getSubRefinement();
			subRefinement.setEclAttributeSet(build(ctx.eclattributeset(), null));
			subRefinement.setEclAttributeGroup(build(ctx.eclattributegroup()));
			subRefinement.setEclRefinement(build(ctx.eclrefinement()));
			return subRefinement;
		}

		private EclAttributeSet build(ECLParser.EclattributesetContext ctx, EclAttributeGroup withinGroup) {
			if (ctx == null) return null;
			EclAttributeSet eclAttributeSet = eclObjectFactory.getEclAttributeSet();
			eclAttributeSet.setParentGroup(withinGroup);
			eclAttributeSet.setSubAttributeSet(build(ctx.subattributeset(), withinGroup));
			if (ctx.conjunctionattributeset() != null) {
				eclAttributeSet.setConjunctionAttributeSet(buildSubAttributeSet(ctx.conjunctionattributeset().subattributeset(), withinGroup));
			}
			if (ctx.disjunctionattributeset() != null) {
				eclAttributeSet.setDisjunctionAttributeSet(buildSubAttributeSet(ctx.disjunctionattributeset().subattributeset(), withinGroup));
			}
			return eclAttributeSet;
		}

		private List<SubAttributeSet> buildSubAttributeSet(List<ECLParser.SubattributesetContext> subAttributeSet, EclAttributeGroup withinGroup) {
			return subAttributeSet.stream().map(ctx -> build(ctx, withinGroup)).collect(Collectors.toList());
		}

		private SubAttributeSet build(ECLParser.SubattributesetContext ctx, EclAttributeGroup withinGroup) {
			if (ctx == null) return null;
			SubAttributeSet subAttributeSet = eclObjectFactory.getSubAttributeSet();
			subAttributeSet.setAttribute(build(ctx.eclattribute(), withinGroup));
			subAttributeSet.setAttributeSet(build(ctx.eclattributeset(), withinGroup));
			return subAttributeSet;
		}

		private EclAttributeGroup build(ECLParser.EclattributegroupContext ctx) {
			if (ctx == null) return null;
			EclAttributeGroup attributeGroup = eclObjectFactory.getAttributeGroup();
			attributeGroup.setAttributeSet(build(ctx.eclattributeset(), attributeGroup));

			ECLParser.CardinalityContext cardinality = ctx.cardinality();
			if (cardinality != null) {
				if (cardinality.minvalue().nonnegativeintegervalue() != null) {
					attributeGroup.setCardinalityMin(Integer.parseInt(cardinality.minvalue().nonnegativeintegervalue().getText()));
				}
				if (cardinality.maxvalue().nonnegativeintegervalue() != null) {
					attributeGroup.setCardinalityMax(Integer.parseInt(cardinality.maxvalue().nonnegativeintegervalue().getText()));
				}
			}

			return attributeGroup;
		}

		private EclAttribute build(ECLParser.EclattributeContext ctx, EclAttributeGroup withinGroup) {
			if (ctx == null) return null;
			EclAttribute attribute = eclObjectFactory.getAttribute();
			attribute.setParentGroup(withinGroup);

			ECLParser.CardinalityContext cardinality = ctx.cardinality();
			if (cardinality != null) {
				if (cardinality.minvalue().nonnegativeintegervalue() != null) {
					attribute.setCardinalityMin(Integer.parseInt(cardinality.minvalue().nonnegativeintegervalue().getText()));
				} else {
					attribute.setCardinalityMin(null);
				}
				if (cardinality.maxvalue().nonnegativeintegervalue() != null) {
					attribute.setCardinalityMax(Integer.parseInt(cardinality.maxvalue().nonnegativeintegervalue().getText()));
				} else {
					attribute.setCardinalityMax(null);
				}
			}

			if (ctx.reverseflag() != null) {
				attribute.reverse();
			}

			attribute.setAttributeName(build(ctx.eclattributename().subexpressionconstraint()));

			ECLParser.ExpressioncomparisonoperatorContext expressionComparisonOperator = ctx.expressioncomparisonoperator();
			if (expressionComparisonOperator != null) {
				attribute.setExpressionComparisonOperator(expressionComparisonOperator.getText());
				attribute.setValue(build(ctx.subexpressionconstraint()));
			}

			ECLParser.NumericcomparisonoperatorContext numericComparisonOperator = ctx.numericcomparisonoperator();
			if (numericComparisonOperator != null) {
				attribute.setNumericComparisonOperator(numericComparisonOperator.getText());
				attribute.setNumericValue(ctx.numericvalue().getText());
			}

			ECLParser.StringcomparisonoperatorContext stringComparisonOperator = ctx.stringcomparisonoperator();
			if (stringComparisonOperator != null) {
				attribute.setStringComparisonOperator(stringComparisonOperator.getText());
				attribute.setStringValue(ctx.stringvalue().getText());
			}

			return attribute;
		}

		ExpressionConstraint getRootExpressionConstraint() {
			return rootExpressionConstraint;
		}

	}

}
