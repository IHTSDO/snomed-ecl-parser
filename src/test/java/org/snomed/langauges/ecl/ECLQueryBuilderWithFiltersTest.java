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

import java.util.*;

import static org.junit.Assert.*;
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
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		TermFilter termFilter = termFilters.get(0);
		assertEquals("=", termFilter.getBooleanComparisonOperator());
		List<TypedSearchTerm> searchTerms = termFilter.getTypedSearchTermSet();
		assertEquals(1, searchTerms.size());
		TypedSearchTerm searchTerm = searchTerms.iterator().next();
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("heart att", searchTerm.getTerm());

		// one term per filter
		ecl = "< 64572001 |Disease|  {{ term = \"heart\", term = \"att\"}}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(2, termFilters.size());

		termFilter = termFilters.get(0);
		assertEquals("=", termFilter.getBooleanComparisonOperator());
		searchTerms = termFilter.getTypedSearchTermSet();
		assertEquals(1, searchTerms.size());
		searchTerm = searchTerms.iterator().next();
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("heart", searchTerm.getTerm());

		termFilter = termFilters.get(1);
		assertEquals("=", termFilter.getBooleanComparisonOperator());
		searchTerms = termFilter.getTypedSearchTermSet();
		assertEquals(1, searchTerms.size());
		searchTerm = searchTerms.iterator().next();
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("att", searchTerm.getTerm());
	}

	@Test
	public void testFilterWithSearchedTermSet() {
		// term set search with mixed search types
		String ecl = "< 64572001 |Disease|  {{ term = (match:\"gas\" wild:\"*itis\")}}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());

		TermFilter termFilter = termFilters.get(0);
		assertEquals("=", termFilter.getBooleanComparisonOperator());
		List<TypedSearchTerm> searchTerms = termFilter.getTypedSearchTermSet();
		assertEquals(2, searchTerms.size());

		Iterator<TypedSearchTerm> iterator = searchTerms.iterator();
		TypedSearchTerm searchTerm = iterator.next();
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("gas", searchTerm.getTerm());

		searchTerm = iterator.next();
		assertEquals(WILDCARD, searchTerm.getType());
		assertEquals("*itis", searchTerm.getTerm());


		// termSet filter
		ecl = "< 64572001 |Disease|  {{ term = (\"heart\" \"card\")}}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());

		termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());

		termFilter = termFilters.get(0);
		List<TypedSearchTerm> searchTermsList = new ArrayList<>(termFilter.getTypedSearchTermSet());
		assertEquals(2, searchTermsList.size());
		searchTermsList.sort(Comparator.comparing(TypedSearchTerm::getTerm));

		searchTerm = searchTermsList.get(0);
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("card", searchTerm.getTerm());

		searchTerm = searchTermsList.get(1);
		assertEquals(MATCH, searchTerm.getType());
		assertEquals("heart", searchTerm.getTerm());
	}

	@Test
	public void testMultipleFilterConstraints() {
		// multiple filter constraints
		String ecl = "< 64572001 |Disease|  {{ term = (match:\"gas\" wild:\"*itis\")}} {{ term = \"heart att\"}}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		assertEquals(2, termFilters.get(0).getTypedSearchTermSet().size());
		termFilters = filterConstraints.get(1).getTermFilters();
		assertEquals(1, termFilters.size());
	}

	private List<DescriptionFilterConstraint> getFilterConstraints(String ecl) {
		ExpressionConstraint query = eclQueryBuilder.createQuery(ecl);
		assertTrue(query instanceof SubExpressionConstraint);
		SubExpressionConstraint subExpressionConstraint = (SubExpressionConstraint) query;
		return subExpressionConstraint.getDescriptionFilterConstraints();
	}

	@Test
	public void testMultipleLanguageFilters() {
		String ecl = "< 64572001 |Disease|  {{ term = \"hjärt\", language = sv }} {{ term = (\"heart\" \"card\"), language = en }}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());

		List<LanguageFilter> languageFilters = filterConstraints.get(0).getLanguageFilters();
		assertEquals(1, languageFilters.size());
		assertEquals("sv", languageFilters.get(0).getLanguageCodes().get(0));

		termFilters = filterConstraints.get(1).getTermFilters();
		assertEquals(1, termFilters.size());
		assertEquals(2, termFilters.get(0).getTypedSearchTermSet().size());

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
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<TermFilter> termFilters = filterConstraints.get(0).getTermFilters();
		assertEquals(1, termFilters.size());
		List<DescriptionTypeFilter> descriptionTypeFilters = filterConstraints.get(0).getDescriptionTypeFilters();
		assertEquals(1, descriptionTypeFilters.size());
		assertEquals(DescriptionType.SYN.getTypeId(), descriptionTypeFilters.get(0).getSubExpressionConstraint().getConceptId());

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
		assertEquals(DescriptionType.SYN.getTypeId(), descriptionTypeFilters.get(0).getSubExpressionConstraint().getConceptId());
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
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());

		DialectFilter dialectFilter = dialectFilters.get(0);
		assertEquals("=", dialectFilter.getBooleanComparisonOperator());
		List<DialectAcceptability> dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		assertEquals("en-gb", dialectAcceptabilities.iterator().next().getDialectAlias());

		// dialectId
		ecl = "< 64572001 |Disease|  {{ dialectId  = 900000000000508004 |Great Britain English language reference set| }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialectFilter = dialectFilters.get(0);
		assertEquals("=", dialectFilter.getBooleanComparisonOperator());
		dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		DialectAcceptability dialectAcceptability = dialectAcceptabilities.iterator().next();
		assertEquals("900000000000508004", dialectAcceptability.getSubExpressionConstraint().getConceptId());
		assertEquals("Great Britain English language reference set", dialectAcceptability.getSubExpressionConstraint().getTerm());
		assertNull(dialectAcceptability.getAcceptabilityIdSet());
		assertNull(dialectAcceptability.getAcceptabilityTokenSet());

		// mixed with alias and dialect id
		ecl = "< 64572001 |Disease|  {{ dialectId  = 900000000000508004 }} {{ dialect = en-ie }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(2, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());

		dialectFilter = dialectFilters.get(0);
		assertEquals("=", dialectFilter.getBooleanComparisonOperator());
		dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		dialectAcceptability = dialectAcceptabilities.iterator().next();
		assertEquals("900000000000508004", dialectAcceptability.getSubExpressionConstraint().getConceptId());

		dialectFilters = filterConstraints.get(1).getDialectFilters();
		dialectFilter = dialectFilters.get(0);
		assertEquals(1, dialectFilters.size());
		assertEquals("=", dialectFilter.getBooleanComparisonOperator());
		dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		dialectAcceptability = dialectAcceptabilities.iterator().next();
		assertEquals("en-ie", dialectAcceptability.getDialectAlias());

		// disjunction set
		ecl = "<  64572001 |Disease|  {{ term = \"card\", dialect != ( en-nhs-clinical en-nhs-pharmacy ) }}";
		filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialectFilter = dialectFilters.get(0);
		assertEquals("!=", dialectFilter.getBooleanComparisonOperator());
		dialectAcceptabilities = dialectFilter.getDialectAcceptabilities();
		assertEquals(2, dialectAcceptabilities.size());
		assertEquals("en-nhs-clinical", dialectAcceptabilities.get(0).getDialectAlias());
		assertEquals("en-nhs-pharmacy", dialectAcceptabilities.get(1).getDialectAlias());
		assertNull(dialectAcceptabilities.get(0).getAcceptabilityIdSet());
		assertNull(dialectAcceptabilities.get(0).getAcceptabilityTokenSet());
	}

	@Test
	public void testAcceptabilityFilters() {
		// one dialect and acceptability
		String ecl = "< 64572001 |Disease|  {{ term = \"box\", typeId =  900000000000013009 |Synonym| , dialect = en-us ( 900000000000548007 |Preferred| ) }}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		assertEquals(1, filterConstraints.size());

		assertEquals(1, filterConstraints.get(0).getTermFilters().size());
		assertEquals(1, filterConstraints.get(0).getDescriptionTypeFilters().size());

		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		assertNotNull(dialectFilters.get(0).getDialectAcceptabilities());
		List<DialectAcceptability> dialectAcceptabilities = dialectFilters.get(0).getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());

		DialectAcceptability dialectAcceptability = dialectAcceptabilities.iterator().next();
		assertEquals("en-us", dialectAcceptability.getDialectAlias());
		assertEquals(1, dialectAcceptability.getAcceptabilityIdSet().size());
		assertEquals("900000000000548007", dialectAcceptability.getAcceptabilityIdSet().iterator().next().getConceptId());

		// dialect and acceptability set
		ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = ( en-gb en-nhs-clinical ) (prefer) }}";
		filterConstraints = getFilterConstraints(ecl);
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialectAcceptabilities = dialectFilters.get(0).getDialectAcceptabilities();
		assertEquals(2, dialectAcceptabilities.size());
		dialectAcceptability = dialectAcceptabilities.get(0);
		assertEquals("en-gb", dialectAcceptability.getDialectAlias());
		assertEquals(1, dialectAcceptability.getAcceptabilityTokenSet().size());
		assertEquals(Acceptability.PREFERRED, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());
		dialectAcceptability = dialectAcceptabilities.get(1);
		assertEquals("en-nhs-clinical", dialectAcceptability.getDialectAlias());
		assertEquals(1, dialectAcceptability.getAcceptabilityTokenSet().size());
		assertEquals(Acceptability.PREFERRED, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());
	}

	@Test
	public void testMultipleDialectFilters() {
		// multiple dialects and acceptability
		String ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = en-nhs-clinical (prefer), dialect = en-gb (accept) }}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
		List<DialectFilter> dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(2, dialectFilters.size());

		List<DialectAcceptability> dialectAcceptabilities = dialectFilters.get(0).getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		DialectAcceptability dialectAcceptability = dialectAcceptabilities.get(0);
		assertEquals("en-nhs-clinical", dialectAcceptability.getDialectAlias());
		assertEquals(1, dialectAcceptability.getAcceptabilityTokenSet().size());
		assertEquals(Acceptability.PREFERRED, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());

		dialectAcceptabilities = dialectFilters.get(1).getDialectAcceptabilities();
		assertEquals(1, dialectAcceptabilities.size());
		dialectAcceptability = dialectAcceptabilities.get(0);
		assertEquals("en-gb", dialectAcceptability.getDialectAlias());
		assertEquals(1, dialectAcceptability.getAcceptabilityTokenSet().size());
		assertEquals(Acceptability.ACCEPTABLE, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());

		// multiple acceptability sets
		ecl = "< 64572001 |Disease|  {{ term = \"box\", type = syn, dialect = ( en-gb (accept) en-nhs-clinical (prefer) ) }}";
		filterConstraints = getFilterConstraints(ecl);
		dialectFilters = filterConstraints.get(0).getDialectFilters();
		assertEquals(1, dialectFilters.size());
		dialectAcceptabilities = dialectFilters.get(0).getDialectAcceptabilities();
		assertEquals(2, dialectAcceptabilities.size());
		dialectAcceptability = dialectAcceptabilities.get(0);
		assertEquals("en-gb", dialectAcceptability.getDialectAlias());
		assertEquals(Acceptability.ACCEPTABLE, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());
		dialectAcceptability = dialectAcceptabilities.get(1);
		assertEquals("en-nhs-clinical", dialectAcceptability.getDialectAlias());
		assertEquals(Acceptability.PREFERRED, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());

		// First dialect has no acceptability, second does
		// TODO: Is this valid? Ask the languages group.
//		ecl = "< 64572001 |Disease|  {{ dialect = ( en-us en-gb (prefer) ) }}";
//		filterConstraints = getFilterConstraints(ecl);
//		dialectFilters = filterConstraints.get(0).getDialectFilters();
//		assertEquals(1, dialectFilters.size());
//		dialectAcceptabilities = dialectFilters.get(0).getDialectAcceptabilities();
//		assertEquals(2, dialectAcceptabilities.size());
//		dialectAcceptability = dialectAcceptabilities.get(0);
//		assertEquals("en-us", dialectAcceptability.getDialectAlias());
//		assertNull(dialectAcceptability.getAcceptabilityTokenSet());
//		assertNull(dialectAcceptability.getAcceptabilityIdSet());
//		dialectAcceptability = dialectAcceptabilities.get(1);
//		assertEquals("en-gb", dialectAcceptability.getDialectAlias());
//		assertEquals(Acceptability.PREFERRED, dialectAcceptability.getAcceptabilityTokenSet().iterator().next());
	}

	@Test
	public void testFiltersWithNegation() {
		// term filter negation
		String ecl = "< 125605004 |Fracture of bone|  {{ term != \"fracture\", type = syn, dialect = en-us (prefer)}}";
		List<DescriptionFilterConstraint> filterConstraints = getFilterConstraints(ecl);
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
		assertNull(refinedExpressionConstraint.getSubexpressionConstraint().getDescriptionFilterConstraints());

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
		assertNotNull(subExpressionConstraint.getDescriptionFilterConstraints());
		assertFalse(subExpressionConstraint.getDescriptionFilterConstraints().isEmpty());
		TermFilter termFilter = subExpressionConstraint.getDescriptionFilterConstraints().get(0).getTermFilters().get(0);
		assertEquals("insufficiency", termFilter.getTypedSearchTermSet().iterator().next().getTerm());

		// after adding bracket and the filter will apply to descendants of < 404684003 |Clinical finding|
		ecl = "( < 404684003 |Clinical finding|  :"
				+ "363698007 |Finding site| = << 39057004 |Pulmonary valve structure|  ,"
				+ "116676008 |Associated morphology| = << 415582006 |Stenosis| ) {{ term = \"insufficiency\" }}";

		query = eclQueryBuilder.createQuery(ecl);
		assertTrue(query instanceof SubExpressionConstraint);
		subExpressionConstraint = (SubExpressionConstraint) query;
		assertNotNull(subExpressionConstraint.getNestedExpressionConstraint());
		assertNotNull(subExpressionConstraint.getDescriptionFilterConstraints());
		assertFalse(subExpressionConstraint.getDescriptionFilterConstraints().isEmpty());
		termFilter = subExpressionConstraint.getDescriptionFilterConstraints().get(0).getTermFilters().get(0);
		assertEquals(1, termFilter.getTypedSearchTermSet().size());
	}
}
