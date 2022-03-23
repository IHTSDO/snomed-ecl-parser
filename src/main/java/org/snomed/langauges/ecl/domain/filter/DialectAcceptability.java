package org.snomed.langauges.ecl.domain.filter;

import org.snomed.langauges.ecl.domain.ConceptReference;

import java.util.Objects;
import java.util.Set;

public class DialectAcceptability {

	// One of:
	private ConceptReference dialectId;
	private String dialectAlias;

	// One of:
	private Set<ConceptReference> acceptabilityIdSet;
	private Set<Acceptability> acceptabilityTokenSet;

	private DialectAcceptability() {
	}

	public DialectAcceptability(ConceptReference dialectId) {
		this.dialectId = dialectId;
	}

	public DialectAcceptability(String dialectAlias) {
		this.dialectAlias = dialectAlias;
	}

	public void addAcceptabilityId(ConceptReference acceptabilityId) {
		acceptabilityIdSet.add(acceptabilityId);
	}

	public void addAcceptabilityToken(Acceptability acceptabilityToken) {
		acceptabilityTokenSet.add(acceptabilityToken);
	}

	public ConceptReference getDialectId() {
		return dialectId;
	}

	public void setDialectId(ConceptReference dialectId) {
		this.dialectId = dialectId;
	}

	public String getDialectAlias() {
		return dialectAlias;
	}

	public void setDialectAlias(String dialectAlias) {
		this.dialectAlias = dialectAlias;
	}

	public Set<ConceptReference> getAcceptabilityIdSet() {
		return acceptabilityIdSet;
	}

	public void setAcceptabilityIdSet(Set<ConceptReference> acceptabilityIdSet) {
		this.acceptabilityIdSet = acceptabilityIdSet;
	}

	public Set<Acceptability> getAcceptabilityTokenSet() {
		return acceptabilityTokenSet;
	}

	public void setAcceptabilityTokenSet(Set<Acceptability> acceptabilityTokenSet) {
		this.acceptabilityTokenSet = acceptabilityTokenSet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DialectAcceptability that = (DialectAcceptability) o;
		return Objects.equals(dialectId, that.dialectId) && Objects.equals(dialectAlias, that.dialectAlias) && Objects.equals(acceptabilityIdSet, that.acceptabilityIdSet) && Objects.equals(acceptabilityTokenSet, that.acceptabilityTokenSet);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dialectId, dialectAlias, acceptabilityIdSet, acceptabilityTokenSet);
	}
}
