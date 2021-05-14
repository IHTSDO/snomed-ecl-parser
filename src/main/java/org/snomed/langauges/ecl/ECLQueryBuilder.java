package org.snomed.langauges.ecl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.snomed.langauges.ecl.domain.expressionconstraint.*;
import org.snomed.langauges.ecl.domain.filter.*;
import org.snomed.langauges.ecl.domain.refinement.*;
import org.snomed.langauges.ecl.generated.ImpotentECLListener;
import org.snomed.langauges.ecl.generated.parser.ECLLexer;
import org.snomed.langauges.ecl.generated.parser.ECLParser;

import java.util.*;
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

		private Filter buildFilter(ECLParser.TermfilterContext termFilterContext) {
			TermFilter termFilter = new TermFilter(termFilterContext.booleancomparisonoperator().getText());
			if (termFilterContext.typedsearchtermset() != null) {
				termFilterContext.typedsearchtermset().typedsearchterm().forEach(term -> termFilter.addTypedSearchTerm(term.getText()));
			} else if (termFilterContext.typedsearchterm() != null) {
				termFilter.addTypedSearchTerm(termFilterContext.typedsearchterm().getText());
			}
			return termFilter;
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
			LanguageFilter languageFilter = new LanguageFilter(languageFilterContext.booleancomparisonoperator().getText());
			if (languageFilterContext.languagecodeset() != null) {
				languageFilterContext.languagecodeset().languagecode().forEach(languageCode -> languageFilter.addLanguageCode(languageCode.getText()));
			} else if (languageFilterContext.languagecode() != null) {
				languageFilter.addLanguageCode(languageFilterContext.languagecode().getText());
			}
			return languageFilter;
		}

		private Filter buildFilter(ECLParser.DialectfilterContext dialectFilterContext) {
			DialectFilter dialectFilter = null;
			if (dialectFilterContext.dialectaliasfilter() != null) {
				// build dialect filter using alias
				dialectFilter = buildFilter(dialectFilterContext.dialectaliasfilter());
			} else if (dialectFilterContext.dialectidfilter() != null) {
				dialectFilter = buildFilter(dialectFilterContext.dialectidfilter());
			}
			// update acceptability here as in some cases they are only available at this context
			if (dialectFilterContext.acceptabilityset() != null && dialectFilter != null) {
				List<Acceptability> acceptabilityList = constructAcceptability(dialectFilterContext.acceptabilityset());
				for (Dialect dialect : dialectFilter.getAcceptabilityMap().keySet()) {
					for (Acceptability acceptability : acceptabilityList) {
						dialectFilter.addDialect(dialect, acceptability);
					}
				}
			}
			return dialectFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectidfilterContext dialectIdFilterContext) {
			final DialectFilter dialectFilter = new DialectFilter(dialectIdFilterContext.booleancomparisonoperator().getText());
			ECLParser.DialectidsetContext dialectIdSetContext = dialectIdFilterContext.dialectidset();
			if (dialectIdSetContext != null) {
				List<ECLParser.AcceptabilitysetContext> acceptabilityContexts = dialectIdSetContext.acceptabilityset();
				List<ECLParser.EclconceptreferenceContext> dialectIdContexts = dialectIdFilterContext.dialectidset().eclconceptreference();
				for (int i = 0; i < dialectIdContexts.size(); i++) {
					Dialect dialect = new Dialect().withDialectId(dialectIdContexts.get(0).conceptid().getText());
					if (!acceptabilityContexts.isEmpty()) {
						constructAcceptability(acceptabilityContexts.get(i)).forEach(acceptability -> dialectFilter.addDialect(dialect, acceptability));
					} else {
						dialectFilter.addDialect(dialect, null);
					}
				}
			} else if (dialectIdFilterContext.dialectid() != null) {
				String dialectId = dialectIdFilterContext.eclconceptreference().conceptid().getText();
				dialectFilter.addDialect(new Dialect().withDialectId(dialectId), null);
			}
			return dialectFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectaliasfilterContext dialectAliasFilterContext) {
			final DialectFilter dialectFilter = new DialectFilter(dialectAliasFilterContext.booleancomparisonoperator().getText());
			if (dialectAliasFilterContext.dialectaliasset() != null) {
				List<ECLParser.AcceptabilitysetContext> acceptabilityContextList = dialectAliasFilterContext.dialectaliasset().acceptabilityset();
				List<ECLParser.DialectaliasContext> dialectAliasContexts = dialectAliasFilterContext.dialectaliasset().dialectalias();
				for (int i = 0; i < dialectAliasContexts.size(); i++) {
					Dialect dialect = new Dialect().withAlias(dialectAliasContexts.get(i).getText());
					if (!acceptabilityContextList.isEmpty()) {
						constructAcceptability(acceptabilityContextList.get(i)).forEach(acceptability -> dialectFilter.addDialect(dialect, acceptability));
					} else {
						dialectFilter.addDialect(dialect, null);
					}
				}
			} else if (dialectAliasFilterContext.dialectalias() != null) {
				// acceptability will be updated later
				dialectFilter.addDialect(new Dialect().withAlias(dialectAliasFilterContext.dialectalias().getText()), null);
			}
			return dialectFilter;
		}

		private List<Acceptability> constructAcceptability(ECLParser.AcceptabilitysetContext context) {
			List<Acceptability> result = new ArrayList<>();
			if (context.acceptabilityidset() != null) {
				context.acceptabilityidset().eclconceptreferenceset().eclconceptreference().forEach(r -> {
					Acceptability acceptability = Acceptability.getAcceptabilityById(r.conceptid().getText());
					if (acceptability == null) {
						throw new IllegalArgumentException(String.format("Unknown acceptability id %s", r.conceptid().getText()));
					}
					result.add(acceptability);
				});
			} else if (context.acceptabilitytokenset() != null) {
				context.acceptabilitytokenset().acceptabilitytoken().forEach(r -> {
					Acceptability acceptability = Acceptability.getAcceptabilityByToken(r.getText());
					if (acceptability == null) {
						throw new IllegalArgumentException(String.format("Unknown acceptability %s", r.acceptable().getText()));
					}
					result.add(acceptability);
				});
			}
			return result;
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
