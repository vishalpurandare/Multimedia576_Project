package com.mult.core.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.mult.util.Constants;

public class AudioIntensityDescriptor {

	public int[] readAudioFile(String path) throws UnsupportedAudioFileException, IOException {
		int totalFramesRead = 0;

		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));

		int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

		if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
			// some audio formats may have unspecified frame size
			// in that case we may read any amount of bytes
			bytesPerFrame = 1;
		}

		// int numBytes = 1024 * bytesPerFrame;
		byte[] audioBytes = new byte[Constants.AUDIO_SAMPLES_COUNT * bytesPerFrame];

		int numBytesRead = 0;
		int numFramesRead = 0;
		// Try to read numBytes bytes from the file.
		while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
			// Calculate the number of frames actually read.
			numFramesRead = numBytesRead / bytesPerFrame;
			totalFramesRead += numFramesRead;
		}

		int[] windows = new int[500];
		int windowCnt = 0;
	    int prevValue = 0;
		int maxValue = Integer.MIN_VALUE;
		for (int byteItr = 0; byteItr < totalFramesRead * bytesPerFrame; byteItr++) {
			int byteCnt = 0;
			int valueAdded = 0;
			while (byteCnt != 2200) {
				if(byteItr >= totalFramesRead * bytesPerFrame) break;
				int currValue = audioBytes[byteItr];
				currValue = (currValue << 8) | audioBytes[byteItr+1]; 
				valueAdded += currValue;
				byteItr+=2;
				prevValue = currValue;
				byteCnt++;
			}
			//int diffValueAdded = Math.abs(prevValueAdded - valueAdded);
			if (maxValue < valueAdded)
				maxValue = valueAdded;
			windows[windowCnt] = valueAdded;
			windowCnt++;
			byteItr--;
		}
		
		int[] audioDescriptor = new int[windowCnt];
		for (int windowItr = 0; windowItr < windowCnt; windowItr++) {
	
			//int computedValue =  Math.abs(prevValue - windows[windowItr]); //(int) Math.floor((windows[windowItr] / (double)maxValue) * 255);
			audioDescriptor[windowItr] = (int) ((windows[windowItr] / (double)maxValue) * 255);
			//prevValue = windows[windowItr];
		}

		return audioDescriptor;

	}

}
