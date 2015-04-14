package com.mva.model;

import java.util.HashMap;
import java.util.Map;

public class Contact {
	private Map<String, Object> attributes;

	public Contact() {
		this.attributes = new HashMap<String, Object>();
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}