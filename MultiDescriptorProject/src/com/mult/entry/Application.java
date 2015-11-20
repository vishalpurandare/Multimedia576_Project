package com.mult.entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.mult.core.audio.AudioIntensityDescriptor;
import com.mult.core.color.ColorDescriptor;
import com.mult.util.Constants;
import com.mult.util.DescriptorBean;
import com.mult.util.Utilities;

public class Application {

	/**
	 * Application Entry
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			//Get Video Directory
			File dirVideo = new File(Constants.VIDEO_PATH_VISHAL_PC);
			File[] directoryListingVid = dirVideo.listFiles();
			
			//Get Audio Directory
			File dirAudio = new File(Constants.AUDIO_PATH_VISHAL_PC);
			File[] directoryListingAud = dirAudio.listFiles();
			
			if (directoryListingVid.length != directoryListingAud.length) {
				Utilities.trace("Invalid Directories or number of files in directories for videos and audios, they should match");
				return;
			}
			
			VideoDescriptorEntry vidEntryObj = new VideoDescriptorEntry();
			AudioIntensityDescriptor audObj = new AudioIntensityDescriptor();
			ColorDescriptor colorObj = new ColorDescriptor();
			
			//Iterate over all the video files to create descriptors and serialize each in different files
			for (int fileItr = 0; fileItr < directoryListingVid.length; fileItr++) {
				File currFileVid = directoryListingVid[fileItr];
				File currFileAud = directoryListingAud[fileItr];
				
				//get video motion vector descriptor
				int[] motionVectorDescriptorArray = vidEntryObj.getVideoMotionVectorDescriptor(currFileVid);
				//get audio intensity descriptor
				int[] audioDescriptorArray = audObj.getAudioDescriptor(currFileAud);
				//get video color intensity descriptor (New one)
				int[] colorIntensityDescriptorArray = colorObj.getColorDescriptor(currFileVid);
				
				DescriptorBean descriptorObj = new DescriptorBean();
				List<int[]> descriptorsList = new ArrayList<int[]>();
				
				descriptorsList.add(motionVectorDescriptorArray);
				descriptorsList.add(audioDescriptorArray);
				descriptorsList.add(colorIntensityDescriptorArray);
				
				descriptorObj.setFileName(currFileVid.getName());
				descriptorObj.setDescriptorsList(descriptorsList);
				
				Utilities.serializeObject(descriptorObj, currFileVid.getName());
				
				Utilities.trace("########################################################");
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} 

	}

}
