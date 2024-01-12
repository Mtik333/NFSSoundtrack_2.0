package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum Role {
	COMPOSER("COMPOSER"),
	SUBCOMPOSER("SUBCOMPOSER"),
	FEAT("FEAT"),
	REMIX("REMIX");

	public String value() {
		return value;
	}

	private final String value;

	Role(String value) {
		this.value = value;
	}
}