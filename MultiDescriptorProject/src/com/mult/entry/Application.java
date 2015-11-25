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

	//false if you are create database, true if you are calculate descriptors for test videos and compare against database
	public static boolean ifTestMode = true;
	
	/**
	 * Application Entry
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
				VideoDescriptorEntry vidEntryObj = new VideoDescriptorEntry();
				AudioIntensityDescriptor audObj = new AudioIntensityDescriptor();
				ColorDescriptor colorObj = new ColorDescriptor();
			
				if (!ifTestMode) {
					Utilities.trace("Create Database Mode");
					Utilities.trace("***** ***** ***** ***** ******");
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
						
						Utilities.serializeObject(descriptorObj, currFileVid.getName(), false);
						
						Utilities.trace("########################################################");
					}
				} else {
					Utilities.trace("Comparison mode, for test video");
					
					File dirVideo = new File(Constants.TEST_PATH_VISHAL_PC);
					File[] directoryListing = dirVideo.listFiles();
					
					//Taking first 2 test files, which are for same file, audio and video files
					File testFileVid = directoryListing[4];
					File testFileAud = directoryListing[5];
					
					//get video motion vector descriptor
					int[] motionVectorDescriptorArray = vidEntryObj.getVideoMotionVectorDescriptor(testFileVid);
					//get audio intensity descriptor
					int[] audioDescriptorArray = audObj.getAudioDescriptor(testFileAud);
					//get video color intensity descriptor (New one)
					int[] colorIntensityDescriptorArray = colorObj.getColorDescriptor(testFileVid);
					
					DescriptorBean descriptorObj = new DescriptorBean();
					List<int[]> descriptorsList = new ArrayList<int[]>();
					
					descriptorsList.add(motionVectorDescriptorArray);
					descriptorsList.add(audioDescriptorArray);
					descriptorsList.add(colorIntensityDescriptorArray);
					
					descriptorObj.setFileName(testFileVid.getName());
					descriptorObj.setDescriptorsList(descriptorsList);
					
					Utilities.serializeObject(descriptorObj, testFileVid.getName(), true);
					
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
