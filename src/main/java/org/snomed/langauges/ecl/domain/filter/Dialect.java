package org.snomed.langauges.ecl.domain.filter;

import java.util.Objects;
import java.util.StringJoiner;

public class Dialect {

	private String alias;

	private String dialectId;

	public Dialect() {
		// default constructor
	}

	public Dialect withAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public Dialect withDialectId(String dialectId) {
		this.dialectId = dialectId;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public String getDialectId() {
		return dialectId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Dialect dialect = (Dialect) o;
		return Objects.equals(alias, dialect.alias) && Objects.equals(dialectId, dialect.dialectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, dialectId);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Dialect.class.getSimpleName() + "[", "]").add("alias='" + alias + "'").add("dialectId='" + dialectId + "'").toString();
	}
}
