package com.mult.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
		int descValue1Add = 0;
		int descValue2Add = 0;
		int descValue1 = 0;
		int descValue2 = 0;
		for (int descItr1 = 0; descItr1 < Constants.NO_OF_FRAMES; descItr1++) {
		/*	int diffVal = Math.abs(desc1[descItr1] - desc2[descItr1]);
			int squareVal = diffVal * diffVal;
			meanErrorValue += squareVal;
	
		*/
			if(descItr1 != Constants.NO_OF_FRAMES - 1)
			descValue1 = Math.abs(desc1[descItr1] - desc1[descItr1 + 1]);
			descValue1Add += desc1[descItr1];
		}
		
		for (int descItr2 = 0; descItr2 < Constants.NO_OF_FRAMES; descItr2++) {
			
			if(descItr2 != Constants.NO_OF_FRAMES - 1)
				descValue2 = Math.abs(desc2[descItr2] - desc2[descItr2 + 1]);	
			
		descValue2Add += desc2[descItr2];
	}
		
		descValue1 = descValue1 * (descValue1Add/Constants.NO_OF_FRAMES);
		
		descValue2 = descValue2 * (descValue2Add / Constants.NO_OF_FRAMES);
		
		System.out.println("Desc1 Value " + descValue1);
		System.out.println("Desc2 Value " + descValue2);

		return Math.abs(descValue1 - descValue2); 
		//return (int) (meanErrorValue / Constants.NO_OF_FRAMES);
		
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
	public static List<DifferenceBean> bestMatchDecriptorToDb(DescriptorBean descriptorObj) throws ClassNotFoundException, IOException {
		File serializedDir = new File(Constants.SERIALIZED_FILE_PATH);
		File[] serFiles = serializedDir.listFiles();
		HashMap<Integer, List<DifferenceBean>> diffMap = new HashMap<Integer, List<DifferenceBean>>();
		List<int[]> testDescriptors = descriptorObj.getDescriptorsList();
		int[] motionDescriptorTest = testDescriptors.get(0);
		int[] audioDescriptorTest = testDescriptors.get(1);
		int[] colorDescriptorTest = testDescriptors.get(2);
		
		double cumulativeDiffValue = Double.MAX_VALUE;
		
		List<DifferenceBean> result = new ArrayList<DifferenceBean>();
		
		for(int itr = 0; itr < 7; itr++) {
			List<DifferenceBean> newList = new ArrayList<DifferenceBean>();
			diffMap.put(itr, newList);
		}
		
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
			
			int currMotionDiff = getDescriptorDifference(motionDescriptorTest, motionDescirptor);
			int currAudioDiff = getDescriptorDifference(audioDescriptorTest, audioDescirptor);
			int currColorDiff = getDescriptorDifference(colorDescriptorTest, colorDescriptor);
			
			double currDiff = (Constants.VIDEO_WEIGHT * currMotionDiff) + (Constants.AUDIO_WEIGHT* currAudioDiff) + (Constants.COLOR_WEIGHT *  currColorDiff);
			DifferenceBean diffBean = new DifferenceBean();
			diffBean.setName( currSerFile.getName());
			diffBean.setValue(currDiff);
			
			System.out.println(" motion: " + currMotionDiff + " audio: " + currAudioDiff + " color: " + currColorDiff);
			System.out.println("Current Min value: " + currDiff);
			System.out.println("#####################################################################");
			
			result.add(diffBean);
			
			if (cumulativeDiffValue > currDiff) {
				cumulativeDiffValue = currDiff;
			}
		}
		
		Collections.sort(result, new Comparator<DifferenceBean>(){

			@Override
			public int compare(DifferenceBean arg0, DifferenceBean arg1) {
				// TODO Auto-generated method stub
				return Double.compare(arg0.getValue(), arg1.getValue());
			}
			
		});
		
		System.out.println("Matched videos ranked from best match to worst match : ");
		
		for (Iterator<DifferenceBean> iterator = result.iterator(); iterator.hasNext();) {
			DifferenceBean differenceBean = (DifferenceBean) iterator.next();
			System.out.println(differenceBean.getName());
		}
		
		return result;
		//System.out.println(descriptorObj.getFileName() + " : Best Matched to : " + bestMatchedFileName);
		
	}
	
	public static void main(String[] arg) {
		try {
			String testFileName = "sports_test.rgb.ser"; // best matched to sports3.v576.rgb.ser, run again for ranks
			//String testFileName = "interview_test.rgb.ser"; // best matched to interview3.v576.rgb.ser, run again for ranks
			//String testFileName = "drama_test.rgb.ser"; // best matched to commercial1.v576.rgb.ser, run again for ranks
			
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
	

	public static void createUIBestBean(DifferenceBean bestBean, JPanel barCodePanel, JFrame frameMain) throws ClassNotFoundException, IOException {
		DescriptorBean bestObj = (DescriptorBean) deSerializeObject(bestBean.getName(), false);
		
		List<int[]> descList = bestObj.getDescriptorsList();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1));
		BufferedImage vDescImage = Utilities.createDescriptorImage(descList.get(0));
		JPanel panel1 = new JPanel();
		JComponent comp1 = new JLabel(new ImageIcon(vDescImage));
		panel1.add(comp1);
		UIManager.getDefaults().put("TitledBorder.titleColor", Color.BLACK);
	    Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	    TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Motion Descriptor");
	    Font titleFont = UIManager.getFont("TitledBorder.font");
        title.setTitleFont( titleFont.deriveFont(Font.BOLD) );
        panel1.setBorder(title);
        
        BufferedImage aDescImage = Utilities.createDescriptorImage(descList.get(1));
		JPanel panel2 = new JPanel();
		JComponent comp2 = new JLabel(new ImageIcon(aDescImage));
		panel2.add(comp2);
	    TitledBorder title2 = BorderFactory.createTitledBorder(lowerEtched, "Audio Descriptor");
        title2.setTitleFont( titleFont.deriveFont(Font.BOLD) );
        panel2.setBorder(title2);
        
        BufferedImage cDescImage = Utilities.createDescriptorImage(descList.get(2));
		JPanel panel3 = new JPanel();
		JComponent comp3 = new JLabel(new ImageIcon(cDescImage));
		panel3.add(comp3);
	    TitledBorder title3 = BorderFactory.createTitledBorder(lowerEtched, "Color Descriptor");
        title3.setTitleFont( titleFont.deriveFont(Font.BOLD) );
        panel3.setBorder(title3);
        
	    TitledBorder titleMain = BorderFactory.createTitledBorder(lowerEtched, "Descriptors of best Matched file");
	    titleMain.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	    mainPanel.setBorder(titleMain);
	    
        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(panel3);
        
		barCodePanel.add(mainPanel);
		SwingUtilities.updateComponentTreeUI(frameMain);
		
	}
	
	public static void trace(String msg) {
		System.out.println(msg);
	}

}
