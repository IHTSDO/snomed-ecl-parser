package org.snomed.langauges.ecl.domain;

public class ConceptReference {

	private String conceptId;
	private String term;

	private ConceptReference() {
	}

	public ConceptReference(String conceptId) {
		this.conceptId = conceptId;
	}

	public ConceptReference(String conceptId, String term) {
		this.conceptId = conceptId;
		this.term = term;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public String toString() {
		return "ConceptReference{" +
				"conceptId='" + conceptId + '\'' +
				'}';
	}
}
