package com.xcompwiz.mystcraft.api.impl;

public class APIWrapper {
	private String	modname;

	public APIWrapper(String modname) {
		this.modname = modname;
	}

	public String getOwnerMod() {
		return modname;
	}

}
