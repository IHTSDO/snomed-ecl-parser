package org.snomed.langauges.ecl;

import org.junit.Before;
import org.junit.Test;
import org.snomed.langauges.ecl.domain.expressionconstraint.ExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.RefinedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;
import org.snomed.langauges.ecl.domain.filter.*;
import org.snomed.langauges.ecl.domain.refinement.EclAttribute;
import org.snomed.langauges.ecl.domain.refinement.EclRefinement;
import org.snomed.langauges.ecl.domain.refinement.SubAttributeSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.snomed.langauges.ecl.domain.filter.SearchType.MATCH;
import static org.snomed.langauges.ecl.domain.filter.SearchType.WILDCARD;

public class ECLQueryBuilderWithFiltersTest {

	private ECLQueryBuilder eclQueryBuilder;

	@Before
	public void setUp() {
		eclQueryBuilder = new ECLQueryBuilder(new ECLObjectFactory());
	}

	@Test
	public void testTermFilters() {
		// term filter with multiple terms
		String ecl = "<64572001 |Disease| {{ term = \"heart att\"}}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		assertEquals(1, filterConstraints.get(0).getAllFilters().size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		TermFilter termFilter = termFilters.get(0);
		assertEquals(1, termFilter.getTypedSearchTerms().size());
		String typedSearchTerm = termFilter.getTypedSearchTerms().get(0);
		assertEquals("\"heart att\"", typedSearchTerm);
		assertEquals("heart att", TermFilter.getTerm(typedSearchTerm));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerm));

		// one term per filter
		ecl = "< 64572001 |Disease|  {{ term = \"heart\", term = \"att\"}}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		assertEquals(2, filterConstraints.get(0).getAllFilters().size());
		termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(2, termFilters.size());
		typedSearchTerm = termFilters.get(0).getTypedSearchTerms().get(0);
		assertEquals("heart", TermFilter.getTerm(typedSearchTerm));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerm));

		typedSearchTerm = termFilters.get(1).getTypedSearchTerms().get(0);
		assertEquals("att", TermFilter.getTerm(typedSearchTerm));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerm));
	}

	@Test
	public void testFilterWithSearchedTermSet() {
		// term set search with mixed search types
		String ecl = "< 64572001 |Disease|  {{ term = (match:\"gas\" wild:\"*itis\")}}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		List<String> typedSearchTerms = termFilters.get(0).getTypedSearchTerms();
		assertEquals(2, typedSearchTerms.size());

		assertEquals("gas", TermFilter.getTerm(typedSearchTerms.get(0)));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerms.get(0)));
		assertEquals("*itis", TermFilter.getTerm(typedSearchTerms.get(1)));
		assertEquals(WILDCARD, TermFilter.getSearchType(typedSearchTerms.get(1)));


		// termSet filter
		ecl = "< 64572001 |Disease|  {{ term = (\"heart\" \"card\")}}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());

		termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		typedSearchTerms = termFilters.get(0).getTypedSearchTerms();
		assertEquals(2, typedSearchTerms.size());

		assertEquals("heart", TermFilter.getTerm(typedSearchTerms.get(0)));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerms.get(0)));
		assertEquals("card", TermFilter.getTerm(typedSearchTerms.get(1)));
		assertEquals(MATCH, TermFilter.getSearchType(typedSearchTerms.get(1)));
	}

	@Test
	public void testMultipleFilterConstraints() {
		// multiple filter constraints
		String ecl = "< 64572001 |Disease|  {{ term = (match:\"gas\" wild:\"*itis\")}} {{ term = \"heart att\"}}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		termFilters = filterConstraints.get(1).getTermFilters();
		assertEquals(1, termFilters.size());
	}

	private List<FilterConstraint> getFilterConstraints(String ecl) {
		ExpressionConstraint query = eclQueryBuilder.createQuery(ecl);
		assertTrue(query instanceof SubExpressionConstraint);
		SubExpressionConstraint subExpressionConstraint = (SubExpressionConstraint) query;
		return subExpressionConstraint.getFilterConstraints();
	}

	@Test
	public void testMultipleLanguageFilters() {
		String ecl = "< 64572001 |Disease|  {{ term = \"hjärt\", language = sv }} {{ term = (\"heart\" \"card\"), language = en }}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		List<Filter> filters = filterConstraints.get(0).getAllFilters();
		assertEquals(2, filters.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());

		List<LanguageFilter> languageFilters = filterConstraints.get(0).getLanguageFilters();
		assertEquals(1, languageFilters.size());
		assertEquals("sv", languageFilters.get(0).getLanguageCodes().get(0));

		filters = filterConstraints.get(1).getAllFilters();
		assertEquals(2, filters.size());
		termFilters = filterConstraints.get(1).getTermFilters();
		assertEquals(1, termFilters.size());
		assertEquals(2, termFilters.get(0).getTypedSearchTerms().size());

		languageFilters = filterConstraints.get(1).getLanguageFilters();
		assertEquals(1, languageFilters.size());
		LanguageFilter languageFilter = filterConstraints.get(1).getLanguageFilters().get(0);
		assertEquals("en", languageFilter.getLanguageCodes().get(0));

		// language code set testing purpose
		ecl = "< 64572001 |Disease| {{ term = (\"heart\"), language = (en sv) }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		languageFilters = filterConstraints.get(0).getLanguageFilters();
		assertEquals(1, languageFilters.size());
		assertEquals("en", languageFilters.get(0).getLanguageCodes().get(0));
		assertEquals("sv", languageFilters.get(0).getLanguageCodes().get(1));
	}

	@Test
	public void testDescriptionTypeFilters() {
		// type id filter
		String ecl = "< 64572001 |Disease|  {{ term = \"box\", typeId =  900000000000013009 |Synonym|}}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		List<DescriptionTypeFilter> descriptionTypeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, descriptionTypeFilters.size());
		assertEquals(DescriptionType.SYN, descriptionTypeFilters.get(0).getTypes().get(0));

		// with type token
		ecl = "< 64572001 |Disease|  {{ term = \"box\", language = en, type = syn }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		descriptionTypeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, descriptionTypeFilters.size());
		assertEquals(DescriptionType.SYN, descriptionTypeFilters.get(0).getTypes().get(0));

		// mix with type id and type token
		ecl = "< 56265001 |Heart disease|  {{ term = \"heart\", language = en, typeId =  900000000000013009 |Synonym|, type = fsn }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		descriptionTypeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(2, descriptionTypeFilters.size());
		assertEquals(DescriptionType.SYN, descriptionTypeFilters.get(0).getTypes().get(0));
		assertEquals(DescriptionType.FSN, descriptionTypeFilters.get(1).getTypes().get(0));

		// type set
		ecl = "< 56265001 |Heart disease|  {{ term = \"heart\", type = (syn fsn) }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		descriptionTypeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, descriptionTypeFilters.size());
		assertEquals(2, descriptionTypeFilters.get(0).getTypes().size());
		assertEquals(DescriptionType.SYN, descriptionTypeFilters.get(0).getTypes().get(0));
		assertEquals(DescriptionType.FSN, descriptionTypeFilters.get(0).getTypes().get(1));
	}


	@Test
	public void testDialectFilters() {
		// dialect alias
		String ecl = "< 64572001 |Disease|  {{ dialect = en-gb }}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());

		List<Dialect> dialects  = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());
		assertEquals("en-gb", dialects.iterator().next().getAlias());

		// dialectId
		ecl = "< 64572001 |Disease|  {{ dialectId  = 900000000000508004 |Great Britain English language reference set| }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialects = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());
		assertEquals("900000000000508004", dialects.iterator().next().getDialectId());

		// mixed with alias and dialect id
		ecl = "< 64572001 |Disease|  {{ dialectId  = 900000000000508004 }} {{ dialect = en-ie }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());

		dialects = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());
		assertEquals("900000000000508004", dialects.iterator().next().getDialectId());

		dialectFilters = filterConstraints.get(1).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialects = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());
		assertEquals("en-ie", dialects.iterator().next().getAlias());

		// disjunction set
		ecl = "<  64572001 |Disease|  {{ term = \"card\", dialect = ( en-nhs-clinical en-nhs-pharmacy ) }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialects = dialectFilters.get(0).getDialects();
		assertEquals(2, dialects.size());
		Iterator<Dialect> iterator = dialects.iterator();
		assertEquals("en-nhs-clinical", iterator.next().getAlias());
		assertEquals("en-nhs-pharmacy", iterator.next().getAlias());
	}

	@Test
	public void testAcceptabilityFilters() {
		// one dialect and acceptability
		String ecl = "< 64572001 |Disease|  {{ term = \"box\", typeId =  900000000000013009 |Synonym| , dialect = en-us ( 900000000000548007 |Preferred| ) }}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		assertEquals(3, filterConstraints.get(0).getAllFilters().size());

		assertEquals(1, filterConstraints.get(0).getTermFilters().size());
		assertEquals(1, filterConstraints.get(0).getDescriptionTypeFilters().size());

		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		assertNotNull(dialectFilters.get(0).getAcceptabilityMap());
		List<Dialect> dialects = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());

		assertEquals("en-us", dialects.iterator().next().getAlias());
		Set<Acceptability> acceptabilitySet = dialectFilters.get(0).getAcceptabilityMap().get(dialects.iterator().next());
		assertEquals(1, acceptabilitySet.size());
		assertEquals(Acceptability.PREFERRED, acceptabilitySet.iterator().next());

		// dialect and acceptability set
		ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = ( en-gb en-nhs-clinical ) (prefer) }}";
		filterConstraints = getFilterConstraints(ecl);
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialects = dialectFilters.get(0).getDialects();
		assertEquals(2, dialects.size());
		assertNotNull(dialectFilters.get(0).getAcceptabilityMap());
		Iterator<Dialect> iterator = dialects.iterator();

		assertEquals("en-gb", iterator.next().getAlias());
		assertEquals("en-nhs-clinical", iterator.next().getAlias());

		acceptabilitySet = dialectFilters.get(0).getAcceptabilityMap().get(new Dialect().withAlias("en-gb"));
		assertEquals(1, acceptabilitySet.size());
		assertEquals(Acceptability.PREFERRED, acceptabilitySet.iterator().next());

		acceptabilitySet = dialectFilters.get(0).getAcceptabilityMap().get(new Dialect().withAlias("en-nhs-clinical"));
		assertEquals(1, acceptabilitySet.size());
		assertEquals(Acceptability.PREFERRED, acceptabilitySet.iterator().next());
	}

	@Test
	public void testMultipleDialectFilters() {
		// multiple dialects and acceptability
		String ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = en-nhs-clinical (prefer), dialect = en-gb (accept) }}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(2, dialectFilters.size());
		List<Dialect> dialects = dialectFilters.get(0).getDialects();
		assertEquals(1, dialects.size());
		Dialect dialect = dialects.iterator().next();
		assertEquals("en-nhs-clinical", dialect.getAlias());
		assertEquals(1, dialectFilters.get(0).getAcceptabilityMap().get(dialect).size());
		Acceptability acceptability = dialectFilters.get(0).getAcceptabilityMap().get(dialect).iterator().next();
		assertEquals("prefer", acceptability.getToken());
		dialects = dialectFilters.get(1).getDialects();
		assertEquals(1, dialects.size());
		assertEquals("en-gb", dialects.iterator().next().getAlias());

		// multiple acceptability sets
		ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = ( en-gb (accept) en-nhs-clinical (prefer) ) }}";
		filterConstraints = getFilterConstraints(ecl);
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialects = dialectFilters.get(0).getDialects();
		assertEquals(2, dialects.size());

		assertNotNull(dialectFilters.get(0).getAcceptabilityMap());

		Set<Acceptability> acceptabilitySet = dialectFilters.get(0).getAcceptabilityMap().get(new Dialect().withAlias("en-gb"));
		assertEquals(1, acceptabilitySet.size());
		assertTrue(acceptabilitySet.contains(Acceptability.ACCEPTABLE));
		acceptabilitySet = dialectFilters.get(0).getAcceptabilityMap().get(new Dialect().withAlias("en-nhs-clinical"));
		assertEquals(1, acceptabilitySet.size());
		assertEquals(Acceptability.PREFERRED, acceptabilitySet.iterator().next());
	}

	@Test
	public void testFiltersWithNegation() {
		// term filter negation
		String ecl = "< 125605004 |Fracture of bone|  {{ term != \"fracture\", type = syn, dialect = en-us (prefer)}}";
		List<FilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		assertEquals("!=", termFilters.get(0).getBooleanComparisonOperator());

		List<DescriptionTypeFilter> typeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, typeFilters.size());
		assertEquals("=", typeFilters.get(0).getBooleanComparisonOperator());

		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		assertEquals("=", dialectFilters.get(0).getBooleanComparisonOperator());

		// negation on description type and dialect
		ecl = "< 125605004 |Fracture of bone|  {{ term = \"fracture\", type != syn, dialect != en-us (prefer)}}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		assertEquals("=", termFilters.get(0).getBooleanComparisonOperator());

		typeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, typeFilters.size());
		assertEquals("!=", typeFilters.get(0).getBooleanComparisonOperator());

		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		assertEquals("!=", dialectFilters.get(0).getBooleanComparisonOperator());
	}

	@Test
	public void testFilterConstraints() {
		// filter applies to << 415582006 |Stenosis| only
		String ecl = "< 404684003 |Clinical finding|  : "
									+ "363698007 |Finding site| = << 39057004 |Pulmonary valve structure|  ,"
									+ "116676008 |Associated morphology| = << 415582006 |Stenosis|  {{ term = \"insufficiency\" }}";

		ExpressionConstraint query = eclQueryBuilder.createQuery(ecl);
		assertTrue(query instanceof RefinedExpressionConstraint);
		RefinedExpressionConstraint refinedExpressionConstraint = (RefinedExpressionConstraint) query;
		assertEquals("404684003", refinedExpressionConstraint.getSubexpressionConstraint().getConceptId());
		assertTrue(refinedExpressionConstraint.getSubexpressionConstraint().getFilterConstraints().isEmpty());

		EclRefinement eclRefinement = refinedExpressionConstraint.getEclRefinement();
		EclAttribute attribute = eclRefinement.getSubRefinement().getEclAttributeSet().getSubAttributeSet().getAttribute();
		assertEquals("363698007", attribute.getAttributeName().getConceptId());

		List<SubAttributeSet> attributeSet = eclRefinement.getSubRefinement().getEclAttributeSet().getConjunctionAttributeSet();
		assertEquals(1, attributeSet.size());
		String attributeId = attributeSet.get(0).getAttribute().getAttributeName().getConceptId();
		assertEquals("116676008", attributeId);
		SubExpressionConstraint subExpressionConstraint = attributeSet.get(0).getAttribute().getValue();
		assertNotNull(subExpressionConstraint);
		assertEquals("415582006", subExpressionConstraint.getConceptId());
		assertNotNull(subExpressionConstraint.getFilterConstraints());
		assertFalse(subExpressionConstraint.getFilterConstraints().isEmpty());
		assertEquals(1, subExpressionConstraint.getFilterConstraints().get(0).getAllFilters().size());
		TermFilter termFilter = subExpressionConstraint.getFilterConstraints().get(0).getTermFilters().get(0);
		assertEquals("insufficiency", TermFilter.getTerm(termFilter.getTypedSearchTerms().get(0)));

		// after adding bracket and the filter will apply to descendants of < 404684003 |Clinical finding|
		ecl = "( < 404684003 |Clinical finding|  :"
				+ "363698007 |Finding site| = << 39057004 |Pulmonary valve structure|  ,"
				+ "116676008 |Associated morphology| = << 415582006 |Stenosis| ) {{ term = \"insufficiency\" }}";

		query = eclQueryBuilder.createQuery(ecl);
		assertTrue(query instanceof SubExpressionConstraint);
		subExpressionConstraint = (SubExpressionConstraint) query;
		assertNotNull(subExpressionConstraint.getNestedExpressionConstraint());
		assertNotNull(subExpressionConstraint.getFilterConstraints());
		assertFalse(subExpressionConstraint.getFilterConstraints().isEmpty());
		assertEquals(1, subExpressionConstraint.getFilterConstraints().get(0).getAllFilters().size());
		termFilter = subExpressionConstraint.getFilterConstraints().get(0).getTermFilters().get(0);
		assertEquals(1, termFilter.getTypedSearchTerms().size());
	}
}
