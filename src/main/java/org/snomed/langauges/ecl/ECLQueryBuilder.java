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

import java.util.ArrayList;
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
				subExpressionConstraint.setFilterConstraints(buildFilterConstraints(ctx.filterconstraint()));
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

		private List<FilterConstraint> buildFilterConstraints(List<ECLParser.FilterconstraintContext> filterConstraintContexts) {
			final List<FilterConstraint> filterConstraints = new ArrayList<>();
			filterConstraintContexts.forEach(constraintContext -> {
				FilterConstraint constraint = new FilterConstraint();
				constraintContext.filter().forEach(filter -> {
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
				});
				filterConstraints.add(constraint);
			});
			return filterConstraints;
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
				StringBuffer buffer = new StringBuffer();
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
					typeFilterContext.typeidfilter().eclconceptreferenceset().eclconceptreference().forEach(concept -> typeFilter.addType(DescriptionType.getTypeById(concept.conceptid().getText())));
				} else if (typeFilterContext.typeidfilter().eclconceptreference() != null) {
					typeFilter.addType(DescriptionType.getTypeById(typeFilterContext.typeidfilter().eclconceptreference().conceptid().getText()));
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
				List<DialectAcceptability> dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
				for (DialectAcceptability dialectAcceptability : dialectAcceptabilities) {
					apply(dialectAcceptability, acceptabilityset);
				}
			}
			return dialectFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectidfilterContext dialectIdFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = dialectIdFilterContext.booleancomparisonoperator();
			ECLParser.EclconceptreferenceContext eclconceptreference = dialectIdFilterContext.eclconceptreference();
			ECLParser.DialectidsetContext dialectidset = dialectIdFilterContext.dialectidset();

			final DialectFilter dialectFilter = new DialectFilter(booleancomparisonoperator.getText());
			if (eclconceptreference != null) {
				dialectFilter.addDialect(new DialectAcceptability(build(eclconceptreference)));
			} else {
				for (ECLParser.EclconceptreferenceContext eclconceptreferenceContext : dialectidset.eclconceptreference()) {
					dialectFilter.addDialect(new DialectAcceptability(build(eclconceptreferenceContext)));
				}
				int i = 0;
				for (ECLParser.AcceptabilitysetContext acceptabilitysetContext : dialectidset.acceptabilityset()) {
					apply(dialectFilter.getDialectAcceptabilities().get(i++), acceptabilitysetContext);
				}
			}
			return dialectFilter;
		}

		private void apply(DialectAcceptability dialectAcceptability, ECLParser.AcceptabilitysetContext acceptabilitysetContext) {
			ECLParser.AcceptabilityidsetContext acceptabilityidset = acceptabilitysetContext.acceptabilityidset();
			ECLParser.AcceptabilitytokensetContext acceptabilitytokenset = acceptabilitysetContext.acceptabilitytokenset();
			if (acceptabilityidset != null) {
				dialectAcceptability.setAcceptabilityIdSet(build(acceptabilityidset));
			} else if (acceptabilitytokenset != null) {
				dialectAcceptability.setAcceptabilityTokenSet(build(acceptabilitytokenset));
			}
		}

		private Set<ConceptReference> build(ECLParser.AcceptabilityidsetContext acceptabilityidset) {
			return acceptabilityidset.eclconceptreferenceset().eclconceptreference().stream().map(this::build).collect(Collectors.toSet());
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
					apply(dialectFilter.getDialectAcceptabilities().get(i++), acceptabilitysetContext);
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
