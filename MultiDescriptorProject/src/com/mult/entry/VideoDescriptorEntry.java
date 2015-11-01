package com.mult.entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mult.core.video.MotionDescriptor;
import com.mult.util.Constants;

public class VideoDescriptorEntry {

	public static void main(String[] args) {
		
		File dirVideo = new File(Constants.VIDEO_PATH_VISHAL_PC);
		File[] directoryListing = dirVideo.listFiles();
		
		//currently only working on one video file, later do it for all files in directoryListings
		File currFile = directoryListing[1];
		
		MotionDescriptor mObj = MotionDescriptor.getInstance();
		try {
			mObj.generateVideoFrames(currFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
