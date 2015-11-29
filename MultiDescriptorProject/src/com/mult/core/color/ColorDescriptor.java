package com.mult.core.color;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mult.core.video.MotionDescriptor;
import com.mult.util.Constants;
import com.mult.util.Utilities;
import com.mult.util.VideoFrameBean;

public class ColorDescriptor {

	private List<VideoFrameBean> videoFrames;

	public List<VideoFrameBean> getVideoFrames() {
		return videoFrames;
	}

	public void setVideoFrames(List<VideoFrameBean> videoFrames) {
		this.videoFrames = videoFrames;
	}

	/**
	 * Test Code Main
	 * 
	 * @param arg
	 */
	public static void main(String[] arg) {

		File dirVideo = new File(Constants.VIDEO_PATH_PC);
		File[] directoryListing = dirVideo.listFiles();

		// currently only working on one video file, later do it for all files
		// in directoryListings
		File currFile = directoryListing[1];
		System.out.println(currFile.getName());

		MotionDescriptor mObj = new MotionDescriptor();
		ColorDescriptor thisObj = new ColorDescriptor();

		try {
			// (1) Divide The Video into frames
			List<VideoFrameBean> videoFrames = mObj
					.generateVideoFrames(currFile);

			// Test Image Display code - can be removed later
			Utilities
					.trace("Number of Frames processed: " + videoFrames.size());

			BufferedImage img = new BufferedImage(Constants.WIDTH,
					Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

			JPanel panel = new JPanel();
			JComponent comp = new JLabel(new ImageIcon(img));
			panel.add(comp);
			JFrame frame = new JFrame("Test");
			frame.getContentPane().add(panel);
			frame.pack();
			frame.setLocation(300, 200);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			for (VideoFrameBean videoFrame : videoFrames) {
				int[][] framePixels = videoFrame.getFramePixels();

				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						img.setRGB(x, y, framePixels[y][x]);
					}
				}

				SwingUtilities.updateComponentTreeUI(frame);
				Thread.sleep(100);
			}

			// call to test code
			// mObj.testFrames(videoFrames, img, frame);
			// test-code ends

			frame.dispose();

			// Actual Motion Vector Descriptor Code
			long[] colorLongArray = new long[150];

			thisObj.setVideoFrames(videoFrames);

			// display frame (current and previous) test - START
			BufferedImage prevFrameImg = new BufferedImage(Constants.WIDTH,
					Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

			BufferedImage currFrameImg = new BufferedImage(Constants.WIDTH,
					Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

			JPanel panel1 = new JPanel();
			JComponent comp1 = new JLabel(new ImageIcon(prevFrameImg));
			panel1.add(comp1);
			JFrame frame1 = new JFrame("Previous Frame");
			frame1.getContentPane().add(panel1);
			frame1.pack();
			frame1.setLocation(600, 200);
			frame1.setVisible(true);
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JPanel panel2 = new JPanel();
			JComponent comp2 = new JLabel(new ImageIcon(currFrameImg));
			panel2.add(comp2);
			JFrame frame2 = new JFrame("Current Frame");
			frame2.getContentPane().add(panel2);
			frame2.pack();
			frame2.setLocation(900, 200);
			frame2.setVisible(true);
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// display frame (current and previous) test - END

			long maxColorValue = Integer.MIN_VALUE;
			long minColorValue = Integer.MAX_VALUE;
			
			for (int frameItr = 0; frameItr < videoFrames.size(); frameItr++) {

				long fColorValue = thisObj.getFrameColorValue(frameItr);

				if (maxColorValue < fColorValue) {
					maxColorValue = fColorValue;
				}
				
				if (minColorValue > fColorValue) {
					minColorValue = fColorValue;
				}
				
				colorLongArray[frameItr] = fColorValue;

				System.out.println(fColorValue);

			}

			//Normalize value
			int[] colorDescriptorArray = Utilities.getNormalizedDescriptorArray(colorLongArray, maxColorValue, minColorValue, 0);
			
			BufferedImage vDescImage = Utilities
					.createDescriptorImage(colorDescriptorArray);
			Utilities.displayImage(vDescImage, "VideoDescriptor");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * return motion vector descriptor called from Application
	 * 
	 * @param currFile
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int[] getColorDescriptor(File currFile)
			throws IOException, InterruptedException {
		
		long startTime = System.currentTimeMillis();

		Utilities.trace("getColorDescriptor START");
		Utilities.trace("Processing File: START " + currFile.getName());
		
		MotionDescriptor mObj = new MotionDescriptor();

		// (1) Divide The Video into frames
		List<VideoFrameBean> videoFrames = mObj.generateVideoFrames(currFile);

		// Actual Motion Vector Descriptor Code
		int[] colorDescriptorArray = new int[150];
		long[] colorLongArray = new long[150];

		setVideoFrames(videoFrames);

		// initialized for 1st frame
		long maxColorValue = Integer.MIN_VALUE;
		for (int frameItr = 0; frameItr < videoFrames.size(); frameItr++) {

			long fColorValue = getFrameColorValue(frameItr);

			if (maxColorValue < fColorValue) {
				maxColorValue = fColorValue;
			}
			colorLongArray[frameItr] = fColorValue;

		}

		for (int windowItr = 0; windowItr < colorDescriptorArray.length; windowItr++) {
			colorDescriptorArray[windowItr] = (int) ((colorLongArray[windowItr] / (double) maxColorValue) * 255);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		Utilities.trace("Total Time required: " + totalTime + " MS "
				+ totalTime / (1000 * 60) + " Minutes");
		Utilities.trace("Processing File: END " + currFile.getName());
		Utilities.trace("getColorDescriptor END");

		return colorDescriptorArray;
	}

	/**
	 * Return color intensity value for current frame
	 * 
	 * @param frameItr
	 * @return
	 */
	private long getFrameColorValue(int frameItr) {
		// Initialize the finalMotionValue
		long frameValue = 0;
		VideoFrameBean currentFrame = videoFrames.get(frameItr);
		long[] colBrightsArray = new long[Constants.WIDTH];
		int[][] currFramePixels = currentFrame.getFramePixels();
		for (int wItr = 0; wItr < Constants.WIDTH; wItr++) {
			long colBrights = 0;
			for (int hItr = 0; hItr < Constants.HEIGHT; hItr++) {
				int pixVal = currFramePixels[hItr][wItr];

				int rCmp = ((pixVal >> 16) & 0xff);
				int gCmp = ((pixVal >> 8) & 0xff);
				int bCmp = ((pixVal) & 0xff);

				float[] hsv = new float[3];
				Color.RGBtoHSB(rCmp, gCmp, bCmp, hsv);

				// Defined the threshold of 50% for intense values
				if (hsv[2] * 100 > 50) {
					colBrights++;
				}
			}
			colBrightsArray[wItr] = colBrights;
		}
		// long frameValue = 0;
		for (int wItr = 1; wItr < Constants.WIDTH; wItr++) {
			frameValue += Math.abs(colBrightsArray[wItr]
					- colBrightsArray[wItr - 1]);
		}

		return frameValue;
	}

}
