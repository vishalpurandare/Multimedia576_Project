package com.mult.entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.mult.core.video.MotionDescriptor;
import com.mult.util.Constants;

public class VideoDescriptorEntry {

	public static void main(String[] args) {
		
		File dirVideo = new File(Constants.VIDEO_PATH_VISHAL_PC);
		File[] directoryListing = dirVideo.listFiles();
		
		//currently only working on one video file, later do it for all files in directoryListings
		File currFile = directoryListing[0];
		
		MotionDescriptor mObj = MotionDescriptor.getInstance();
		try {
			//(1) Divide The Video into frames
			List<int[]> framesComponents = mObj.generateVideoFrames(currFile);
			
			//(2) MPEG Based Motion-Compensation technique
			// (2.1) Divide each Frame into Macro-Block
			for (int[] compTempArr : framesComponents) {
				//Will return blocks for each of the frame (image), 15x15 block, 192 for each red, green and blue
				Map<String, List<int[][]>> macroBlocksMap = mObj.getBlocksFromImageComponents(
						Constants.WIDTH, Constants.HEIGHT, compTempArr);
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
