package com.mult.entry;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.mult.core.audio.AudioIntensityDescriptor;
import com.mult.util.Utilities;

public class Application {

	public static void main(String[] args) {
		
		AudioIntensityDescriptor audio = new AudioIntensityDescriptor();
		
		int[] vals;
		try {
			vals = audio.readAudioFile("F://Fall '15//CS 576//Final Project//audio_wav//animation3.wav");
			BufferedImage img = Utilities.createDescriptorImage(vals);
			
			Utilities.displayImage(img, "Testing");
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
