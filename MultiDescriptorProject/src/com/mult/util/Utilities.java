package com.mult.util;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

	public static void serializeObject(Object obj, String fileName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(Constants.SERIALIZED_FILE_PATH + fileName + ".ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
		Utilities.trace("Serialized --- " + fileOut.toString());
	}

	public static Object deSerializeObject(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(Constants.SERIALIZED_FILE_PATH + fileName + ".ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Object descriptorObj =  in.readObject();
        in.close();
        fileIn.close();
		Utilities.trace("De-Serialized --- " + fileIn.toString());
		return descriptorObj;
	}
	
	public static int getDescriptorDifference(int[] desc1, int[] desc2) {
		int minDiff = Integer.MAX_VALUE;

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
		return minDiff;
	}
	
	public static void trace(String msg) {
		System.out.println(msg);
	}

}
