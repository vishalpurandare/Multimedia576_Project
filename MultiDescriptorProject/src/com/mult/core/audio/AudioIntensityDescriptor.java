package com.mult.core.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.mult.util.Constants;
import com.mult.util.Utilities;

public class AudioIntensityDescriptor {

	public int[] getAudioDescriptor(File currFile)
			throws UnsupportedAudioFileException, IOException {

		long startTime = System.currentTimeMillis();
		Utilities.trace("getAudioDescriptor START");
		Utilities.trace("Processing File: START " + currFile.getName());

		int totalFramesRead = 0;

		AudioInputStream audioInputStream = AudioSystem
				.getAudioInputStream(currFile);

		int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

		if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
			// some audio formats may have unspecified frame size
			// in that case we may read any amount of bytes
			bytesPerFrame = 1;
		}

		// int numBytes = 1024 * bytesPerFrame;
		byte[] audioBytes = new byte[Constants.AUDIO_SAMPLES_COUNT
				* bytesPerFrame];

		int numBytesRead = 0;
		int numFramesRead = 0;
		// Try to read numBytes bytes from the file.
		while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
			// Calculate the number of frames actually read.
			numFramesRead = numBytesRead / bytesPerFrame;
			totalFramesRead += numFramesRead;
		}

		long[] windows = new long[500];
		int windowCnt = 0;
		int prevValue = 0;
		
		long maxValue = Long.MIN_VALUE;
		long minValue = Long.MAX_VALUE;
		
		for (int byteItr = 0; byteItr < totalFramesRead * bytesPerFrame; byteItr++) {
			int byteCnt = 0;
			int valueAdded = 0;
			while (byteCnt != 2200) {
				if (byteItr >= totalFramesRead * bytesPerFrame)
					break;
				int currValue = audioBytes[byteItr];
				currValue = (currValue << 8) | audioBytes[byteItr + 1];
				valueAdded += Math.abs(prevValue - currValue);
				byteItr += 2;
				prevValue = currValue;
				byteCnt++;
			}
			// int diffValueAdded = Math.abs(prevValueAdded - valueAdded);
			if (maxValue < valueAdded) {
				maxValue = valueAdded;
			}
			
			if (minValue > valueAdded) {
				minValue = valueAdded;
			}
			windows[windowCnt] = valueAdded;
			windowCnt++;
			byteItr--;
		}

		int[] audioDescriptor = Utilities.getNormalizedDescriptorArray(windows, maxValue, minValue, 1);
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		Utilities.trace("Total Time required: " + totalTime + " MS "
				+ totalTime / (1000 * 60) + " Minutes");
		Utilities.trace("Processing File: END " + currFile.getName());

		Utilities.trace("getAudioDescriptor END");
		
		return audioDescriptor;

	}

}
