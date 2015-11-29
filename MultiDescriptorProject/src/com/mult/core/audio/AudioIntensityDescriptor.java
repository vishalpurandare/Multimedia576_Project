package com.mult.core.audio;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.mult.util.Constants;
import com.mult.util.Utilities;

public class AudioIntensityDescriptor {

	public int[] getAudioDescriptor(File currFile, JPanel barCodePanel, JFrame frameMain)
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
		
		//Display Bar code
		if (frameMain != null) {
			BufferedImage aDescImage = Utilities.createDescriptorImage(audioDescriptor);
			
			JPanel panel1 = new JPanel();
			JComponent comp1 = new JLabel(new ImageIcon(aDescImage));
			panel1.add(comp1);
			UIManager.getDefaults().put("TitledBorder.titleColor", Color.BLACK);
		    Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		    TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Audio Descriptor");
		    Font titleFont = UIManager.getFont("TitledBorder.font");
	        title.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        panel1.setBorder(title);
	        
			barCodePanel.add(panel1);
			SwingUtilities.updateComponentTreeUI(frameMain);
		}
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		Utilities.trace("Total Time required: " + totalTime + " MS "
				+ totalTime / (1000 * 60) + " Minutes");
		Utilities.trace("Processing File: END " + currFile.getName());

		Utilities.trace("getAudioDescriptor END");
		
		return audioDescriptor;

	}

}
