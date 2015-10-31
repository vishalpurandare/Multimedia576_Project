package com.mult.util;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utilities {

	public static BufferedImage createDescriptorImage(int[] descriptorValues) {

		BufferedImage descImage = new BufferedImage(Constants.NO_OF_FRAMES, Constants.DESC_IMG_HEIGHT,
				BufferedImage.TYPE_INT_RGB);

		for (int valueItr = 0; valueItr < Constants.NO_OF_FRAMES; valueItr++) {
			int descVal = descriptorValues[valueItr];
			int redVal = descVal << 16;
			int greenVal = descVal << 8;
			int blueVal = descVal ;
			int descValPixel = redVal | greenVal | blueVal;
			for (int hItr = 0; hItr < Constants.DESC_IMG_HEIGHT; hItr++) {
				descImage.setRGB(valueItr, hItr, descValPixel);
			}
		}

		return descImage;
	}
	
	public static void displayImage(BufferedImage descImg, String typeDesc)
	{
		JPanel panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(descImg)));
		JFrame frame = new JFrame(typeDesc);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	


}
