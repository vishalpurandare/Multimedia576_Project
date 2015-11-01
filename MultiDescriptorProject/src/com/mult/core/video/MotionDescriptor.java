package com.mult.core.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mult.util.Constants;
import com.mult.util.Utilities;

public class MotionDescriptor {

	// Singleton object
	private static MotionDescriptor motionDescObj;

	private MotionDescriptor() {

	}

	/**
	 * Returns the instance of the class {@link MotionDescriptor}
	 * 
	 * @return {@link MotionDescriptor}
	 */
	public static MotionDescriptor getInstance() {

		if (motionDescObj == null) {
			motionDescObj = new MotionDescriptor();
		}

		return motionDescObj;
	}

	/**
	 * Processes Video Frame by Frame
	 * 
	 * @param videoPath
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void generateVideoFrames(File videoFile) throws IOException, InterruptedException {
		Utilities.trace("generateVideoFrames START");

		InputStream videoIS = new FileInputStream(videoFile);
		long len = videoFile.length();

		Utilities.trace("Length of file: " + len);

		byte[] bytes = new byte[(int) len];
		int offset = 0;
		int numRead = 0;

		// Reading the number of bytes into the array bytes
		while (offset < bytes.length
				&& (numRead = videoIS
						.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		BufferedImage img = new BufferedImage(Constants.WIDTH,
				Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

		JPanel panel = new JPanel();
		JComponent comp = new JLabel(new ImageIcon(img));
		panel.add(comp);
		JFrame frame = new JFrame("Test");
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(300, 300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int ind = 0;
		
		while ((ind + Constants.HEIGHT * Constants.WIDTH * 2) < len) {
			for (int y = 0; y < Constants.HEIGHT; y++) {
				for (int x = 0; x < Constants.WIDTH; x++) {

					int r = bytes[ind];
					int g = bytes[ind + Constants.HEIGHT * Constants.WIDTH];
					int b = bytes[ind + Constants.HEIGHT * Constants.WIDTH * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);

					img.setRGB(x, y, pix);
					ind++;
				}
			}
			SwingUtilities.updateComponentTreeUI(frame);
			//Thread.sleep(10);
		}
		
		Utilities.trace("Final Idx " + (ind + Constants.HEIGHT * Constants.WIDTH * 2));
		videoIS.close();
		Utilities.trace("generateVideoFrames END");
	}

}
