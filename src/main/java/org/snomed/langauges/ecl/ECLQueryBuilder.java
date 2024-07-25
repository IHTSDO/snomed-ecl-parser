package org.snomed.langauges.ecl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.snomed.langauges.ecl.domain.ConceptReference;
import org.snomed.langauges.ecl.domain.Pair;
import org.snomed.langauges.ecl.domain.expressionconstraint.*;
import org.snomed.langauges.ecl.domain.filter.*;
import org.snomed.langauges.ecl.domain.refinement.*;
import org.snomed.langauges.ecl.generated.ImpotentECLListener;
import org.snomed.langauges.ecl.generated.parser.ECLLexer;
import org.snomed.langauges.ecl.generated.parser.ECLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ECLQueryBuilder {

	private final ECLObjectFactory eclObjectFactory;

	public ECLQueryBuilder(ECLObjectFactory eclObjectFactory) {
		this.eclObjectFactory = eclObjectFactory;
	}

	public ExpressionConstraint createQuery(String ecl) throws ECLException {
		ecl = ecl.replace(",", " ,");
		final ECLParser parser = getEclParser(ecl);
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				throw new ECLException(String.format("Syntax error at line %s, character %s: %s", line, charPositionInLine,
						msg.replace("extraneous input", "unexpected character")));
			}
		});

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

	private static ECLParser getEclParser(String ecl) {
		ANTLRInputStream inputStream = new ANTLRInputStream(ecl);
		final ECLLexer lexer = new ECLLexer(inputStream);
		final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		final ECLParser parser = new ECLParser(tokenStream);
		parser.removeErrorListeners();
		parser.setErrorHandler(new DefaultErrorStrategy() {
			@Override
			protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
				Token startToken = e.getStartToken();
				if (startToken.getText().equals("<EOF>")) {
					throw new ECLException("ECL is incomplete.");
				} else {
					throw new ECLException(String.format("No viable alternative at line %s, character %s.",
							startToken.getLine(), startToken.getCharPositionInLine()));
				}
			}
		});
		return parser;
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
				compoundExpressionConstraint.setExclusionExpressionConstraints(new Pair<>(
						build(ctx.exclusionexpressionconstraint().subexpressionconstraint(0)), build(ctx.exclusionexpressionconstraint().subexpressionconstraint(1))));
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
			return subExpressionConstraints.stream().map(this::build).toList();
		}

		private SubExpressionConstraint build(ECLParser.SubexpressionconstraintContext ctx) {
			Operator operator = ctx.constraintoperator() != null ? Operator.textLookup(ctx.constraintoperator().getText()) : null;
			boolean isNested = operator != null && ctx.memberof() != null;
			SubExpressionConstraint subExpressionConstraint = eclObjectFactory.getSubExpressionConstraint(ctx.memberof() != null ? Operator.memberOf : operator);
			if (ctx.memberof() != null) {
				if (ctx.memberof().refsetfieldset() != null) {
					subExpressionConstraint.setMemberFieldsToReturn(
							ctx.memberof().refsetfieldset().refsetfield().stream().map(ECLParser.RefsetfieldContext::getText).toList());
				} else {
					subExpressionConstraint.setReturnAllMemberFields(ctx.memberof().wildcard() != null);
				}
			}

			// Focus concept
			addFocusConcept(ctx, subExpressionConstraint);

			// Filters
			addFiltersAndSupplements(ctx, subExpressionConstraint);

			// Check whether ECL expression is nested
			// << ^700043003 |Example problem list concepts reference set|
			if (isNested) {
				SubExpressionConstraint outerExpressionConstraint = eclObjectFactory.getSubExpressionConstraint(operator);
				outerExpressionConstraint.setNestedExpressionConstraint(subExpressionConstraint);
				return outerExpressionConstraint;
			} else {
				return subExpressionConstraint;
			}
		}

		private void addFocusConcept(ECLParser.SubexpressionconstraintContext ctx, SubExpressionConstraint subExpressionConstraint) {
			ECLParser.EclfocusconceptContext eclFocusConcept = ctx.eclfocusconcept();
			if (eclFocusConcept != null) {
				if (eclFocusConcept.wildcard() != null) {
					subExpressionConstraint.setWildcard(true);
				}
				if (eclFocusConcept.eclconceptreference() != null) {
					if (eclFocusConcept.eclconceptreference().term() != null) {
						subExpressionConstraint.setTerm(eclFocusConcept.eclconceptreference().term().getText().replace(" ,", ","));
					}
					subExpressionConstraint.setConceptId(eclFocusConcept.eclconceptreference().conceptid().getText());
				}
			} else {
				subExpressionConstraint.setNestedExpressionConstraint(build(ctx.expressionconstraint()));
			}
		}

		private void addFiltersAndSupplements(ECLParser.SubexpressionconstraintContext ctx, SubExpressionConstraint subExpressionConstraint) {
			// Concept filters
			if (ctx.conceptfilterconstraint() != null) {
				for (ECLParser.ConceptfilterconstraintContext conceptfilterconstraintContext : ctx.conceptfilterconstraint()) {
					ConceptFilterConstraint conceptFilterConstraint = buildConceptFilterConstraint(conceptfilterconstraintContext.conceptfilter());
					subExpressionConstraint.addConceptFilterConstraint(conceptFilterConstraint);
				}
			}
			// Description filters
			if (ctx.descriptionfilterconstraint() != null) {
				for (ECLParser.DescriptionfilterconstraintContext descriptionfilterconstraintContext : ctx.descriptionfilterconstraint()) {
					DescriptionFilterConstraint descriptionFilterConstraint = buildFilterConstraint(descriptionfilterconstraintContext.descriptionfilter());
					subExpressionConstraint.addDescriptionFilterConstraint(descriptionFilterConstraint);
				}
			}
			// Member filters
			if (ctx.memberfilterconstraint() != null) {
				for (ECLParser.MemberfilterconstraintContext memberfilterconstraintContext : ctx.memberfilterconstraint()) {
					MemberFilterConstraint memberFilterConstraint = buildMemberFilterConstraint(memberfilterconstraintContext.memberfilter());
					subExpressionConstraint.addMemberFilterConstraint(memberFilterConstraint);
				}
			}

			// History supplement
			if (ctx.historysupplement() != null) {
				subExpressionConstraint.setHistorySupplement(build(ctx.historysupplement()));
			}
		}

		private ConceptReference build(ECLParser.EclconceptreferenceContext eclconceptreference) {
			ConceptReference conceptReference = new ConceptReference(eclconceptreference.conceptid().getText());
			if (eclconceptreference.term() != null) {
				conceptReference.setTerm(eclconceptreference.term().getText().replace(" ,", ","));
			}
			return conceptReference;
		}

		private ConceptFilterConstraint buildConceptFilterConstraint(List<ECLParser.ConceptfilterContext> conceptFilter) {
			ConceptFilterConstraint constraint = eclObjectFactory.getConceptFilterConstraint();
			for (ECLParser.ConceptfilterContext conceptfilterContext : conceptFilter) {
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
				FieldFilter fieldFilter = eclObjectFactory.getFieldFilter("definitionStatusId", idFilter.booleancomparisonoperator().getText().equals("="));
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
				FieldFilter fieldFilter = eclObjectFactory.getFieldFilter("definitionStatusId", tokenFilter.booleancomparisonoperator().getText().equals("="));
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
			FieldFilter fieldFilter = eclObjectFactory.getFieldFilter("moduleId", modulefilter.booleancomparisonoperator().getText().equals("="));
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
			String operatorText = effectivetimefilter.timecomparisonoperator().getText();
			NumericComparisonOperator operator = NumericComparisonOperator.fromText(operatorText);
			if (operator == null) {
				throw new IllegalArgumentException(String.format("Time comparison operator '%s' not recognised.", operatorText));
			}
			List<Integer> effectiveTimes;
			if (effectivetimefilter.timevalue() != null) {
				effectiveTimes = Collections.singletonList(getEffectiveTime(effectivetimefilter.timevalue()));
			} else {
				effectiveTimes = effectivetimefilter.timevalueset().timevalue().stream()
						.map(this::getEffectiveTime).toList();
			}
			return eclObjectFactory.getEffectiveTimeFilter(operator, effectiveTimes);
		}

		private int getEffectiveTime(ECLParser.TimevalueContext timeValue) {
			return Integer.parseInt(timeValue.getText().replace("\"", ""));
		}

		private ActiveFilter buildFilter(ECLParser.ActivefilterContext activeFilter) {
			return eclObjectFactory.getActiveFilter(activeFilter.booleancomparisonoperator().getText().equals("=") == (activeFilter.activevalue().activetruevalue() != null));
		}

		private DescriptionFilterConstraint buildFilterConstraint(List<ECLParser.DescriptionfilterContext> descriptionFilter) {
			DescriptionFilterConstraint constraint = eclObjectFactory.getDescriptionFilterConstraint();
			for (ECLParser.DescriptionfilterContext filter : descriptionFilter) {
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
				if (filter.modulefilter() != null) {
					constraint.addFilter(buildFilter(filter.modulefilter()));
				}
				if (filter.effectivetimefilter() != null) {
					constraint.addFilter(buildFilter(filter.effectivetimefilter()));
				}
				if (filter.activefilter() != null) {
					constraint.addFilter(buildFilter(filter.activefilter()));
				}
			}
			return constraint;
		}

		private TermFilter buildFilter(ECLParser.TermfilterContext termFilterContext) {
			ECLParser.StringcomparisonoperatorContext stringcomparisonoperator = termFilterContext.stringcomparisonoperator();
			ECLParser.TypedsearchtermContext typedsearchterm = termFilterContext.typedsearchterm();
			ECLParser.TypedsearchtermsetContext typedsearchtermset = termFilterContext.typedsearchtermset();

			TermFilter filter = eclObjectFactory.getTermFilter(stringcomparisonoperator.getText());
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
				return eclObjectFactory.getTypedSearchTerm(SearchType.WILDCARD, typedsearchterm.wildsearchtermset().wildsearchterm().getText());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (ECLParser.MatchsearchtermContext matchsearchtermContext : typedsearchterm.matchsearchtermset().matchsearchterm()) {
					if (!buffer.isEmpty()) {
						buffer.append(" ");
					}
					buffer.append(matchsearchtermContext.getText());
				}
				return eclObjectFactory.getTypedSearchTerm(SearchType.MATCH, buffer.toString());
			}
		}

		private DescriptionTypeFilter buildFilter(ECLParser.TypefilterContext typeFilterContext) {
			if (typeFilterContext.typeidfilter() != null) {
				DescriptionTypeFilter typeFilter = eclObjectFactory.getDescriptionTypeFilter(typeFilterContext.typeidfilter().booleancomparisonoperator().getText());
				if (typeFilterContext.typeidfilter().eclconceptreferenceset() != null) {
					typeFilterContext.typeidfilter().eclconceptreferenceset().eclconceptreference()
							.forEach(concept -> typeFilter.addType(DescriptionType.getTypeById(concept.conceptid().getText())));
				} else if (typeFilterContext.typeidfilter().subexpressionconstraint() != null) {
					typeFilter.setSubExpressionConstraint(build(typeFilterContext.typeidfilter().subexpressionconstraint()));
				}
				return typeFilter;
			} else if (typeFilterContext.typetokenfilter() != null) {
				DescriptionTypeFilter typeFilter = eclObjectFactory.getDescriptionTypeFilter(typeFilterContext.typetokenfilter().booleancomparisonoperator().getText());
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

		private LanguageFilter buildFilter(ECLParser.LanguagefilterContext languageFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = languageFilterContext.booleancomparisonoperator();
			ECLParser.LanguagecodeContext languagecode = languageFilterContext.languagecode();
			ECLParser.LanguagecodesetContext languagecodeset = languageFilterContext.languagecodeset();

			LanguageFilter languageFilter = eclObjectFactory.getLanguageFilter(booleancomparisonoperator.getText());
			if (languagecode != null) {
				languageFilter.addLanguageCode(languagecode.getText());
			} else {
				languagecodeset.languagecode().forEach(languageCode -> languageFilter.addLanguageCode(languageCode.getText()));
			}
			return languageFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectfilterContext dialectFilterContext) {
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
				for (DialectAcceptability dialectAcceptability : dialectFilter.getDialectAcceptabilities()) {
					apply(dialectAcceptability, acceptabilityset);
				}
			}
			return dialectFilter;
		}

		private DialectFilter buildFilter(ECLParser.DialectidfilterContext dialectIdFilterContext) {
			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = dialectIdFilterContext.booleancomparisonoperator();
			// Either
			ECLParser.SubexpressionconstraintContext subexpressionconstraint = dialectIdFilterContext.subexpressionconstraint();
			// Or
			ECLParser.DialectidsetContext dialectidset = dialectIdFilterContext.dialectidset();

			final DialectFilter dialectFilter = eclObjectFactory.getDialectFilter(booleancomparisonoperator.getText(), false);
			if (subexpressionconstraint != null) {
				// Has no acceptabilitySet
				dialectFilter.addDialect(eclObjectFactory.getDialectAcceptability(build(subexpressionconstraint)));
			} else {
				List<ECLParser.EclconceptreferenceContext> eclconceptreferenceList = dialectidset.eclconceptreference();
				List<ECLParser.AcceptabilitysetContext> acceptabilityset = dialectidset.acceptabilityset();

				for (int i = 0; i < eclconceptreferenceList.size(); i++) {
					ECLParser.EclconceptreferenceContext eclconceptreferenceContext = eclconceptreferenceList.get(i);
					DialectAcceptability dialectAcceptability = eclObjectFactory.getDialectAcceptability(build(eclconceptreferenceContext));
					int startIndex = eclconceptreferenceContext.getStart().getStartIndex();
					int stopIndex = eclconceptreferenceList.size() > i + 1 ? eclconceptreferenceList.get(i + 1).getStart().getStartIndex() : Integer.MAX_VALUE;
					ECLParser.AcceptabilitysetContext acceptabilityContext = getAcceptabilityContext(startIndex, stopIndex, acceptabilityset);
					apply(dialectAcceptability, acceptabilityContext);
					dialectFilter.addDialect(dialectAcceptability);
				}
			}
			return dialectFilter;
		}

		private void apply(DialectAcceptability dialectAcceptability, ECLParser.AcceptabilitysetContext acceptabilitysetContext) {
			if (acceptabilitysetContext == null) {
				return;
			}
			ECLParser.AcceptabilityconceptreferencesetContext acceptabilityconceptreferenceset = acceptabilitysetContext.acceptabilityconceptreferenceset();
			ECLParser.AcceptabilitytokensetContext acceptabilitytokenset = acceptabilitysetContext.acceptabilitytokenset();
			if (acceptabilityconceptreferenceset != null) {
				dialectAcceptability.setAcceptabilityIdSet(buildConceptReferences(acceptabilityconceptreferenceset.eclconceptreference()));
			} else if (acceptabilitytokenset != null) {
				dialectAcceptability.setAcceptabilityTokenSet(build(acceptabilitytokenset));
			}
		}

		private MemberFilterConstraint buildMemberFilterConstraint(List<ECLParser.MemberfilterContext> memberfilter) {
			MemberFilterConstraint constraint = eclObjectFactory.getMemberFilterConstraint();
			for (ECLParser.MemberfilterContext memberfilterContext : memberfilter) {
				if (memberfilterContext.memberfieldfilter() != null) {
					MemberFieldFilter memberFieldFilter = buildFilter(memberfilterContext.memberfieldfilter());
					constraint.addMemberFieldFilter(memberFieldFilter);
				}
				if (memberfilterContext.modulefilter() != null) {
					FieldFilter fieldFilter = buildFilter(memberfilterContext.modulefilter());
					constraint.addModuleFilter(fieldFilter);
				}
				if (memberfilterContext.effectivetimefilter() != null) {
					EffectiveTimeFilter effectiveTimeFilter = buildFilter(memberfilterContext.effectivetimefilter());
					constraint.addEffectiveTimeFilter(effectiveTimeFilter);
				}
				if (memberfilterContext.activefilter() != null) {
					ActiveFilter activeFilter = buildFilter(memberfilterContext.activefilter());
					constraint.addActiveFilter(activeFilter);
				}
			}
			return constraint;
		}

		private MemberFieldFilter buildFilter(ECLParser.MemberfieldfilterContext memberfieldfilter) {
			String fieldName = memberfieldfilter.refsetfieldname().getText();
			MemberFieldFilter fieldFilter = eclObjectFactory.getMemberFieldFilter(fieldName);
			if (memberfieldfilter.expressioncomparisonoperator() != null) {
				fieldFilter.setExpressionComparisonOperator(memberfieldfilter.expressioncomparisonoperator().getText());
				SubExpressionConstraint subExpressionConstraint = build(memberfieldfilter.subexpressionconstraint());
				fieldFilter.setSubExpressionConstraint(subExpressionConstraint);
			} else if (memberfieldfilter.numericcomparisonoperator() != null) {
				fieldFilter.setNumericComparisonOperator(NumericComparisonOperator.fromText(memberfieldfilter.numericcomparisonoperator().getText()));
				String numericValue = memberfieldfilter.numericvalue().getText().replace("+", "");
				fieldFilter.setNumericValue(numericValue);
			} else if (memberfieldfilter.stringcomparisonoperator() != null) {
				fieldFilter.setStringComparisonOperator(memberfieldfilter.stringcomparisonoperator().getText());
				List<TypedSearchTerm> searchTerms = build(memberfieldfilter.typedsearchterm(), memberfieldfilter.typedsearchtermset());
				fieldFilter.setSearchTerms(searchTerms);
			} else if (memberfieldfilter.booleancomparisonoperator() != null) {
				fieldFilter.setBooleanComparisonOperator(memberfieldfilter.booleancomparisonoperator().getText());
				fieldFilter.setBooleanValue(memberfieldfilter.booleanvalue().true_1() != null);
			} else {
				NumericComparisonOperator timeComparisonOperator = NumericComparisonOperator.fromText(memberfieldfilter.timecomparisonoperator().getText());
				fieldFilter.setTimeComparisonOperator(timeComparisonOperator);
				List<Integer> timeValues = build(memberfieldfilter.timevalue(), memberfieldfilter.timevalueset());
				fieldFilter.setTimeValues(timeValues);
			}
			return fieldFilter;
		}

		private List<Integer> build(ECLParser.TimevalueContext timevalue, ECLParser.TimevaluesetContext timevalueset) {
			List<Integer> values = new ArrayList<>();
			if (timevalue != null) {
				values.add(Integer.parseInt(timevalue.getText()));
			}
			if (timevalueset != null) {
				for (ECLParser.TimevalueContext timevalueContext : timevalueset.timevalue()) {
					values.add(Integer.parseInt(timevalueContext.getText()));
				}
			}
			return values;
		}

		private HistorySupplement build(ECLParser.HistorysupplementContext historysupplement) {
			HistorySupplement supplement = eclObjectFactory.getHistorySupplement();
			if (historysupplement.historyprofilesuffix() != null) {
				HistoryProfile historyProfile;
				if (historysupplement.historyprofilesuffix().historyminimumsuffix() != null) {
					historyProfile = HistoryProfile.MIN;
				} else if (historysupplement.historyprofilesuffix().historymoderatesuffix() != null) {
					historyProfile = HistoryProfile.MOD;
				} else {
					historyProfile = HistoryProfile.MAX;
				}
				supplement.setHistoryProfile(historyProfile);
			} else if (historysupplement.historysubset() != null) {
				supplement.setHistorySubset(build(historysupplement.historysubset().expressionconstraint()));
			}
			return supplement;
		}

		private List<ConceptReference> buildConceptReferences(List<ECLParser.EclconceptreferenceContext> eclconceptreference) {
			return eclconceptreference.stream().map(this::build).toList();
		}

		private List<Acceptability> build(ECLParser.AcceptabilitytokensetContext acceptabilitytokenset) {
			return acceptabilitytokenset.acceptabilitytoken().stream().map(this::build).toList();
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
			ECLParser.BooleancomparisonoperatorContext booleanComparisonOperator = dialectAliasFilterContext.booleancomparisonoperator();
			// Either
			ECLParser.DialectaliasContext dialectAlias = dialectAliasFilterContext.dialectalias();
			// Or
			ECLParser.DialectaliassetContext dialectAliasSet = dialectAliasFilterContext.dialectaliasset();

			DialectFilter dialectFilter = eclObjectFactory.getDialectFilter(booleanComparisonOperator.getText(), true);
			if (dialectAlias != null) {
				// Has no acceptabilitySet
				dialectFilter.addDialect(eclObjectFactory.getDialectAcceptability(dialectAlias.getText()));
			} else {
				List<ECLParser.DialectaliasContext> dialectaliasList = dialectAliasSet.dialectalias();
				List<ECLParser.AcceptabilitysetContext> acceptabilitySet = dialectAliasSet.acceptabilityset();

				for (int i = 0; i < dialectaliasList.size(); i++) {
					ECLParser.DialectaliasContext dialectaliasContext = dialectaliasList.get(i);
					int accStartIndex = dialectaliasContext.getStop().getStartIndex();
					int accStopIndex = dialectaliasList.size() > i + 1 ? dialectaliasList.get(i + 1).getStart().getStartIndex() : Integer.MAX_VALUE;
					ECLParser.AcceptabilitysetContext acceptabilityContext = getAcceptabilityContext(accStartIndex, accStopIndex, acceptabilitySet);
					DialectAcceptability dialectAcceptability = eclObjectFactory.getDialectAcceptability(dialectaliasContext.getText());
					apply(dialectAcceptability, acceptabilityContext);
					dialectFilter.addDialect(dialectAcceptability);
				}
			}
			return dialectFilter;
		}

		private ECLParser.AcceptabilitysetContext getAcceptabilityContext(int accStartIndex, int accStopIndex, List<ECLParser.AcceptabilitysetContext> acceptabilityset) {
			for (ECLParser.AcceptabilitysetContext acceptabilitysetContext : acceptabilityset) {
				int startIndex = acceptabilitysetContext.getStart().getStartIndex();
				if (accStartIndex <= startIndex && accStopIndex >= startIndex) {
					return acceptabilitysetContext;
				}
			}
			return null;
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
			return subRefinements.stream().map(this::build).toList();
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
			return subAttributeSet.stream().map(ctx -> build(ctx, withinGroup)).toList();
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

				ECLParser.TypedsearchtermContext typedsearchterm = ctx.typedsearchterm();
				ECLParser.TypedsearchtermsetContext typedsearchtermset = ctx.typedsearchtermset();
				List<TypedSearchTerm> values = build(typedsearchterm, typedsearchtermset);
				attribute.setStringValues(values);
			}

			ECLParser.BooleancomparisonoperatorContext booleancomparisonoperator = ctx.booleancomparisonoperator();
			if (booleancomparisonoperator != null) {
				attribute.setBooleanComparisonOperator(booleancomparisonoperator.getText());
				attribute.setBooleanValue(ctx.booleanvalue().true_1() != null);
			}

			return attribute;
		}

		private List<TypedSearchTerm> build(ECLParser.TypedsearchtermContext typedsearchterm, ECLParser.TypedsearchtermsetContext typedSearchTermSet) {
			List<ECLParser.TypedsearchtermContext> typedSearchTermContexts;
			if (typedsearchterm != null) {
				typedSearchTermContexts = Collections.singletonList(typedsearchterm);
			} else {
				typedSearchTermContexts = typedSearchTermSet.typedsearchterm();
			}
			List<TypedSearchTerm> values = new ArrayList<>();
			for (ECLParser.TypedsearchtermContext typedsearchtermContext : typedSearchTermContexts) {
				TypedSearchTerm searchTerm = build(typedsearchtermContext);
				values.add(searchTerm);
			}
			return values;
		}

		ExpressionConstraint getRootExpressionConstraint() {
			return rootExpressionConstraint;
		}

	}

}
