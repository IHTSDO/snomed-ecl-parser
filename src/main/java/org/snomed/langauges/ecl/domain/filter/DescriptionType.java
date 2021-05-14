package org.snomed.langauges.ecl.domain.filter;

public enum DescriptionType {
	FSN("900000000000003001"),
	SYN("900000000000013009"),
	DEF("900000000000550004");

	private final String typeId;

	DescriptionType(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeId() {
		return typeId;
	}

	public static DescriptionType getTypeById(String typeId) {
		for (DescriptionType type : DescriptionType.values()) {
			if (type.typeId.equals(typeId)) {
				return type;
			}
		}
		return null;
	}


	public static DescriptionType getTypeByToken(String token) {
		for (DescriptionType type : DescriptionType.values()) {
			if (type.name().equalsIgnoreCase(token)) {
				return type;
			}
		}
		return null;
	}
}
