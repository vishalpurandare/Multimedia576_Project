package com.mult.entry;

import java.awt.image.BufferedImage;

import com.mult.util.Utilities;

public class Application {

	public static void main(String[] args) {
		
		int[] vals = new int[255];
		for(int i=0; i<255; i++)
		{
			vals[i] = i;
		}
		
		BufferedImage img = Utilities.createDescriptorImage(vals);
		
		Utilities.displayImage(img, "Testing");
		

	}

}
