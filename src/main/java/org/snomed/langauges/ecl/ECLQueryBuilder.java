package org.snomed.langauges.ecl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.snomed.langauges.ecl.domain.expressionconstraint.*;
import org.snomed.langauges.ecl.domain.refinement.*;
import org.snomed.langauges.ecl.generated.ImpotentECLListener;
import org.snomed.langauges.ecl.generated.parser.ECLLexer;
import org.snomed.langauges.ecl.generated.parser.ECLParser;

import java.util.Collections;
import java.util.List;
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

		private ExpressionConstraint build(ECLParser.ExpressionconstraintContext expressionconstraint) {
			if (expressionconstraint.refinedexpressionconstraint() != null) {
				return build(expressionconstraint.refinedexpressionconstraint());
			}
			if (expressionconstraint.compoundexpressionconstraint() != null) {
				return build(expressionconstraint.compoundexpressionconstraint());
			}
			if (expressionconstraint.dottedexpressionconstraint() != null) {
				return build(expressionconstraint.dottedexpressionconstraint());
			}
			if (expressionconstraint.subexpressionconstraint() != null) {
				return build(expressionconstraint.subexpressionconstraint());
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

			ECLParser.EclfocusconceptContext eclfocusconcept = ctx.eclfocusconcept();
			if (eclfocusconcept != null) {
				if (eclfocusconcept.wildcard() != null) {
					subExpressionConstraint.setWildcard(true);
				}
				if (eclfocusconcept.eclconceptreference() != null) {
					if (eclfocusconcept.eclconceptreference().term() != null) {
						subExpressionConstraint.setTerm(eclfocusconcept.eclconceptreference().term().getText());
					}
					subExpressionConstraint.setConceptId(eclfocusconcept.eclconceptreference().conceptid().getText());
				}
			} else {
				subExpressionConstraint.setNestedExpressionConstraint(build(ctx.expressionconstraint()));
			}

			return subExpressionConstraint;
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

		private List<SubRefinement> buildSubRefinements(List<ECLParser.SubrefinementContext> subrefinements) {
			return subrefinements.stream().map(this::build).collect(Collectors.toList());
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

		private List<SubAttributeSet> buildSubAttributeSet(List<ECLParser.SubattributesetContext> subattributeset, EclAttributeGroup withinGroup) {
			return subattributeset.stream().map(ctx -> build(ctx, withinGroup)).collect(Collectors.toList());
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

			ECLParser.ExpressioncomparisonoperatorContext expressioncomparisonoperator = ctx.expressioncomparisonoperator();
			if (expressioncomparisonoperator != null) {
				attribute.setExpressionComparisonOperator(expressioncomparisonoperator.getText());
				attribute.setValue(build(ctx.subexpressionconstraint()));
			}

			ECLParser.NumericcomparisonoperatorContext numericcomparisonoperator = ctx.numericcomparisonoperator();
			if (numericcomparisonoperator != null) {
				attribute.setNumericComparisonOperator(numericcomparisonoperator.getText());
				attribute.setNumericValue(ctx.numericvalue().getText());
			}

			ECLParser.StringcomparisonoperatorContext stringcomparisonoperator = ctx.stringcomparisonoperator();
			if (stringcomparisonoperator != null) {
				attribute.setStringComparisonOperator(stringcomparisonoperator.getText());
				attribute.setStringValue(ctx.stringvalue().getText());
			}

			return attribute;
		}

		ExpressionConstraint getRootExpressionConstraint() {
			return rootExpressionConstraint;
		}

	}

}
