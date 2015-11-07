package com.mult.util;

import java.io.Serializable;
import java.util.Map;

public class DescriptorBean implements Serializable {

	/**
	 * The object will be serialized
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, int[]> descriptorMap;
	
	public Map<String, int[]> getDescriptorMap() {
		return descriptorMap;
	}
	public void setDescriptorMap(Map<String, int[]> descriptorMap) {
		this.descriptorMap = descriptorMap;
	}
	
}
