package org.snomed.langauges.ecl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.snomed.langauges.ecl.domain.expressionconstraint.ExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.RefinedExpressionConstraint;
import org.snomed.langauges.ecl.domain.expressionconstraint.SubExpressionConstraint;
import org.snomed.langauges.ecl.domain.refinement.*;

import java.util.List;

import static org.junit.Assert.*;

public class ECLQueryBuilderTest {

	private ECLQueryBuilder eclQueryBuilder;

	@Before
	public void setUp() throws Exception {
		eclQueryBuilder = new ECLQueryBuilder(new ECLObjectFactory());
	}

	/*
	  This test demonstrates using the query builder to test the validity of ECL strings.
	 */
	@Test(expected = ECLException.class)
	public void parseInvalidSyntax() {
		eclQueryBuilder.createQuery("404684003 |Clinical finding| : 404684003 |Clinical finding|");
	}

	/*
	  This test demonstrates converting an ECL string into navigable Java objects.
	  The Java representation can be built using your own classes by extending ECLObjectFactory
	 */
	@Test
	public void parseNestedAttributeValue() throws Exception {
		ExpressionConstraint query = eclQueryBuilder.createQuery(
				"< 404684003 |Clinical finding| :\n" +
						"    47429007 |Associated with| = " +
						"        (< 404684003 |Clinical finding| : \n" +
						"            116676008 |Associated morphology|  = <<  55641003 |Infarct| )");

		Assert.assertTrue(query instanceof RefinedExpressionConstraint);

		RefinedExpressionConstraint refinedExpressionConstraint = (RefinedExpressionConstraint) query;
		assertEquals(Operator.descendantof, refinedExpressionConstraint.getSubexpressionConstraint().getOperator());
		assertEquals("404684003", refinedExpressionConstraint.getSubexpressionConstraint().getConceptId());
		EclAttribute attribute = refinedExpressionConstraint.getEclRefinement().getSubRefinement().getEclAttributeSet().getSubAttributeSet().getAttribute();
		assertEquals("47429007", attribute.getAttributeName().getConceptId());
		assertEquals("=", attribute.getExpressionComparisonOperator());
		ExpressionConstraint nestedExpressionConstraint = attribute.getValue().getNestedExpressionConstraint();
		Assert.assertTrue(nestedExpressionConstraint instanceof RefinedExpressionConstraint);

		RefinedExpressionConstraint nestedRefinedExpressionConstraint = (RefinedExpressionConstraint) nestedExpressionConstraint;
		SubExpressionConstraint nestedSubexpressionConstraint = nestedRefinedExpressionConstraint.getSubexpressionConstraint();
		assertEquals(Operator.descendantof, nestedSubexpressionConstraint.getOperator());
		assertEquals("404684003", nestedSubexpressionConstraint.getConceptId());

		EclAttribute nestedAttribute = nestedRefinedExpressionConstraint.getEclRefinement().getSubRefinement().getEclAttributeSet().getSubAttributeSet().getAttribute();
		assertEquals("116676008", nestedAttribute.getAttributeName().getConceptId());
		assertEquals("=", nestedAttribute.getExpressionComparisonOperator());
		SubExpressionConstraint nestedAttributeValue = nestedAttribute.getValue();
		assertEquals(Operator.descendantorselfof, nestedAttributeValue.getOperator());
		assertEquals("55641003", nestedAttributeValue.getConceptId());
	}

	@Test
	public void parseDisjunctionSubRefinement() {
		ExpressionConstraint query = eclQueryBuilder.createQuery(
				"<404684003 |Clinical finding|: " +
						"{ 363698007 |Finding site| = * } " +
						"OR { 116676008 |Associated morphology| = * }");

		assertTrue(query instanceof RefinedExpressionConstraint);

		RefinedExpressionConstraint refinedExpressionConstraint = (RefinedExpressionConstraint) query;
		EclRefinement eclRefinement = refinedExpressionConstraint.getEclRefinement();
		assertNotNull(eclRefinement);

		SubRefinement subRefinement = eclRefinement.getSubRefinement();
		assertNotNull(subRefinement);
		EclAttributeGroup eclAttributeGroup = subRefinement.getEclAttributeGroup();
		assertNotNull(eclAttributeGroup);
		EclAttributeSet attributeSet = eclAttributeGroup.getAttributeSet();
		assertNotNull(attributeSet);
		SubAttributeSet subAttributeSet = attributeSet.getSubAttributeSet();
		assertNotNull(subAttributeSet);

		List<SubRefinement> disjunctionSubRefinements = eclRefinement.getDisjunctionSubRefinements();
		assertNotNull(disjunctionSubRefinements);
		assertEquals(1, disjunctionSubRefinements.size());
		SubRefinement subRefinement1 = disjunctionSubRefinements.get(0);
		EclAttributeGroup eclAttributeGroup1 = subRefinement1.getEclAttributeGroup();
		assertNotNull(eclAttributeGroup1);
		EclAttributeSet attributeSet1 = eclAttributeGroup1.getAttributeSet();
		assertNotNull(attributeSet1);
		SubAttributeSet subAttributeSet1 = attributeSet1.getSubAttributeSet();
		assertNotNull(subAttributeSet1);
	}

}