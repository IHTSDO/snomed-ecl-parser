package org.snomed.langauges.ecl.domain.filter;

import java.util.StringJoiner;

public enum Acceptability {

	PREFERRED("prefer", "900000000000548007"),
	ACCEPTABLE("accept", "900000000000549004");

	private final String token;
	private final String acceptabilityId;

	Acceptability(String token, String acceptabilityId) {
		this.token = token;
		this.acceptabilityId = acceptabilityId;
	}

	public String getAcceptabilityId() {
		return acceptabilityId;
	}

	public String getToken() {
		return token;
	}

	public static Acceptability getAcceptabilityByToken(String token) {
		for (Acceptability acceptability : Acceptability.values()) {
			if (acceptability.token.equalsIgnoreCase(token)) {
				return acceptability;
			}
		}
		return null;
	}

	public static Acceptability getAcceptabilityById(String acceptabilityId) {
		for (Acceptability acceptability : Acceptability.values()) {
			if (acceptability.acceptabilityId.equals(acceptabilityId)) {
				return acceptability;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Acceptability.class.getSimpleName()
				+ "[", "]").add("token='" + token + "'")
				.add("acceptabilityId='" + acceptabilityId + "'").toString();
	}
}
