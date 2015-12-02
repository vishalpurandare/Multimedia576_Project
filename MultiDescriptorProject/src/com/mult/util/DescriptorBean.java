package com.mult.util;

import java.io.Serializable;
import java.util.List;

public class DescriptorBean implements Serializable {

	/**
	 * The object will be serialized
	 */
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	//list, 0- videoDescriptrArray, 1-audioDescriptorArray, 2- colorDescriptorArray
	private double maxMotion;
	private double minMotion;
	
	public double getMaxMotion() {
		return maxMotion;
	}
	public void setMaxMotion(double maxMotion) {
		this.maxMotion = maxMotion;
	}
	public double getMinMotion() {
		return minMotion;
	}
	public void setMinMotion(double minMotion) {
		this.minMotion = minMotion;
	}
	
	
	private List<int[]> descriptorsList;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<int[]> getDescriptorsList() {
		return descriptorsList;
	}
	public void setDescriptorsList(List<int[]> descriptorsList) {
		this.descriptorsList = descriptorsList;
	}
	
	
}
