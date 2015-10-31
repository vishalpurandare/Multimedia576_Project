package com.mult.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DescriptorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, List<Integer[]>> descriptorMap;
	
	public Map<String, List<Integer[]>> getDescriptorMap() {
		return descriptorMap;
	}
	public void setDescriptorMap(Map<String, List<Integer[]>> descriptorMap) {
		this.descriptorMap = descriptorMap;
	}
	
}
