package com.mult.util;

import java.util.List;
import java.util.Map;

public class VideoFrameBean {

	private int[] framesComponents;
	private int[][] framePixels;
	private Map<String, List<int[][]>> compMacroBlocksMap;
	private List<int[][]> pixMacroBlocks;
	
	
	public int[] getFramesComponents() {
		return framesComponents;
	}
	public void setFramesComponents(int[] framesComponents) {
		this.framesComponents = framesComponents;
	}
	public int[][] getFramePixels() {
		return framePixels;
	}
	public void setFramePixels(int[][] framePixels) {
		this.framePixels = framePixels;
	}
	public Map<String, List<int[][]>> getCompMacroBlocksMap() {
		return compMacroBlocksMap;
	}
	public void setCompMacroBlocksMap(Map<String, List<int[][]>> compMacroBlocksMap) {
		this.compMacroBlocksMap = compMacroBlocksMap;
	}
	public List<int[][]> getPixMacroBlocks() {
		return pixMacroBlocks;
	}
	public void setPixMacroBlocks(List<int[][]> pixMacroBlocks) {
		this.pixMacroBlocks = pixMacroBlocks;
	}
	
	
	
}
