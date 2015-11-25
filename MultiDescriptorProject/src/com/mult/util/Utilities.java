package com.mult.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utilities {

	public static BufferedImage createDescriptorImage(int[] descriptorValues) {

		BufferedImage descImage = new BufferedImage(Constants.NO_OF_FRAMES,
				Constants.DESC_IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);

		for (int valueItr = 0; valueItr < Constants.NO_OF_FRAMES; valueItr++) {
			int descVal = descriptorValues[valueItr];
			int redVal = descVal << 16;
			int greenVal = descVal << 8;
			int blueVal = descVal;
			int descValPixel = redVal | greenVal | blueVal;
			for (int hItr = 0; hItr < Constants.DESC_IMG_HEIGHT; hItr++) {
				descImage.setRGB(valueItr, hItr, descValPixel);
			}
		}

		return descImage;
	}

	public static void displayImage(BufferedImage descImg, String typeDesc) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(descImg)));
		JFrame frame = new JFrame(typeDesc);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void serializeObject(Object obj, String fileName, boolean ifTestData) throws IOException {
		
		FileOutputStream fileOut = null;
		if (ifTestData) {
			fileOut = new FileOutputStream(Constants.SERIALIZED_FILE_PATH_TEST + fileName + ".ser");
		} else {
			fileOut = new FileOutputStream(Constants.SERIALIZED_FILE_PATH + fileName + ".ser");
		}
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
		Utilities.trace("Serialized --- " + fileOut.toString());
	}

	public static Object deSerializeObject(String fileName, boolean ifTestData) throws IOException, ClassNotFoundException {
		
		FileInputStream fileIn = null;
		
		if (ifTestData) {
			fileIn = new FileInputStream(Constants.SERIALIZED_FILE_PATH_TEST + fileName);
		} else {
			fileIn = new FileInputStream(Constants.SERIALIZED_FILE_PATH + fileName);
		}
		
		ObjectInputStream in = new ObjectInputStream(fileIn);
        Object descriptorObj =  in.readObject();
        in.close();
        fileIn.close();
		//Utilities.trace("De-Serialized --- " + fileIn.toString());
		return descriptorObj;
	}
	
	/**
	 * Using mean square error for getting difference between arrays
	 * @param desc1
	 * @param desc2
	 * @return
	 */
	public static int getDescriptorDifference(int[] desc1, int[] desc2) {
		
		int meanErrorValue = 0;
		
		for (int descItr1 = 0; descItr1 < Constants.NO_OF_FRAMES; descItr1++) {
			int diffVal = Math.abs(desc1[descItr1] - desc2[descItr1]);
			int squareVal = diffVal * diffVal;
			meanErrorValue += squareVal;
		}

		return (int) (meanErrorValue / Constants.NO_OF_FRAMES);
		
		/*
		int diffValTot = 0;
		for (int descItr1 = 0; descItr1 < Constants.NO_OF_FRAMES; descItr1++) {
			diffValTot += Math.abs(desc1[descItr1] - desc2[descItr1]);
		}
		if (minDiff > diffValTot)
			minDiff = diffValTot;

		if (minDiff == 0)
			return minDiff;

		for (int descItr1 = 0; descItr1 < Constants.NO_OF_FRAMES; descItr1++) {
			int diffVal = 0;
			int desc1Ptr = descItr1;
			int lastDesc2Ptr = 0;

			for (int descItr2 = 0; desc1Ptr < Constants.NO_OF_FRAMES; descItr2++) {
				diffVal += Math.abs(desc1[desc1Ptr] - desc2[descItr2]);
				desc1Ptr++;
				lastDesc2Ptr = descItr2;
			}
			diffVal += ((Constants.NO_OF_FRAMES - lastDesc2Ptr) * 128);
			if (minDiff > diffVal)
				minDiff = diffVal;
		}

		for (int descItr2 = 0; descItr2 < Constants.NO_OF_FRAMES; descItr2++) {
			int diffVal = 0;
			int desc2Ptr = descItr2;
			int lastDesc1Ptr = 0;

			for (int descItr1 = 0; desc2Ptr < Constants.NO_OF_FRAMES; descItr1++) {
				diffVal += Math.abs(desc2[desc2Ptr] - desc1[descItr1]);
				desc2Ptr++;
				lastDesc1Ptr = descItr1;
			}
			diffVal += ((Constants.NO_OF_FRAMES - lastDesc1Ptr) * 128);
			if (minDiff > diffVal)
				minDiff = diffVal;
		}
		*/		
	}
	
	public static void bestMatchDecriptorToDb(DescriptorBean descriptorObj) throws ClassNotFoundException, IOException {
		File serializedDir = new File(Constants.SERIALIZED_FILE_PATH);
		File[] serFiles = serializedDir.listFiles();
		
		List<int[]> testDescriptors = descriptorObj.getDescriptorsList();
		int[] motionDescriptorTest = testDescriptors.get(0);
		int[] audioDescriptorTest = testDescriptors.get(1);
		int[] colorDescriptorTest = testDescriptors.get(2);
		
		int cumulativeDiffValue = Integer.MAX_VALUE;
		String bestMatchedFileName = "";
		
		for (int serFilesItr = 0; serFilesItr < serFiles.length; serFilesItr++) {
			File currSerFile = serFiles[serFilesItr];
			DescriptorBean currBeanObj = (DescriptorBean) deSerializeObject(currSerFile.getName(), false);
			
			List<int[]> currDescriptorList = currBeanObj.getDescriptorsList();
			
			int[] motionDescirptor = currDescriptorList.get(0);
			int[] audioDescirptor = currDescriptorList.get(1);
			int[] colorDescriptor = currDescriptorList.get(2);
		
			System.out.println(currSerFile.getName());
			
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", motionDescirptor[j]) + " ");
			}
			System.out.println();
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", motionDescriptorTest[j]) + " ");
			}
			
			System.out.println();
			System.out.println();
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", audioDescirptor[j]) + " ");
			}
			System.out.println();
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", audioDescriptorTest[j]) + " ");
			}
			
			System.out.println();
			System.out.println();
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", colorDescriptor[j]) + " ");
			}
			System.out.println();
			for (int j = 0; j < Constants.NO_OF_FRAMES; j++) {
				System.out.print(String.format("%03d", colorDescriptorTest[j]) + " ");
			}
			
			System.out.println();
			
			//int[] motionDescTemp = motionDescirptor.clone();
			//motionDescTemp[20] = 20;
			
			int currMotionMinDiff = getDescriptorDifference(motionDescriptorTest, motionDescirptor);
			int currAudioMinDiff = getDescriptorDifference(audioDescriptorTest, audioDescirptor);
			int currColorMinDiff = getDescriptorDifference(colorDescriptorTest, colorDescriptor);
			
			int currMinDiff = currMotionMinDiff + currAudioMinDiff + currColorMinDiff;
			System.out.println(" motion: " + currMotionMinDiff + " audio: " + currAudioMinDiff + " color: " + currColorMinDiff);
			System.out.println("Current Min value: " + currMinDiff);
			System.out.println("#####################################################################");
			
			if (cumulativeDiffValue > currMinDiff) {
				cumulativeDiffValue = currMinDiff;
				bestMatchedFileName = currSerFile.getName();
			}
		}
		
		System.out.println(descriptorObj.getFileName() + " : Best Matched to : " + bestMatchedFileName);
		
	}
	
	public static void main(String[] arg) {
		try {
			//String testFileName = "drama_test.rgb.ser"; // best matched to sports3.v576.rgb.ser, run again for ranks
			//String testFileName = "interview_test.rgb.ser"; // best matched to interview3.v576.rgb.ser, run again for ranks
			String testFileName = "sports_test.rgb.ser"; // best matched to commercial1.v576.rgb.ser, run again for ranks
			
			DescriptorBean testObj = (DescriptorBean) deSerializeObject(testFileName, true);
			bestMatchDecriptorToDb(testObj);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int[] getNormalizedDescriptorArray(long[] currLongArr, long maxValue, long minValue, int startFrom) {
		int[] descriptorArray = new int[150];
		descriptorArray[0] = 0;
		
		for (int windowItr = startFrom; windowItr < descriptorArray.length; windowItr++) {
			descriptorArray[windowItr] = (int) (((currLongArr[windowItr] - minValue) / (double) (maxValue - minValue)) * 255);
		}
		return descriptorArray;
	}
	
	public static void trace(String msg) {
		System.out.println(msg);
	}

}
