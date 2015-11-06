package com.mult.entry;

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

public class VideoDescriptorEntry {

	private List<VideoFrameBean> videoFrames;

	public List<VideoFrameBean> getVideoFrames() {
		return videoFrames;
	}

	public void setVideoFrames(List<VideoFrameBean> videoFrames) {
		this.videoFrames = videoFrames;
	}

	public static void main(String[] args) {

		File dirVideo = new File(Constants.VIDEO_PATH_VISHAL_PC);
		File[] directoryListing = dirVideo.listFiles();

		// currently only working on one video file, later do it for all files
		// in directoryListings
		File currFile = directoryListing[0];
		System.out.println(currFile);

		MotionDescriptor mObj = MotionDescriptor.getInstance();
		VideoDescriptorEntry thisObj = new VideoDescriptorEntry();

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

			// Actual Motion Vector Descpriptor Code
			int[] motionVectorDescriptorArray = new int[150];
			long[] motionVectorLongArray = new long[150];
			thisObj.setVideoFrames(videoFrames);
			long maxMotionVector = Long.MIN_VALUE;

			// display frame test - START
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
			// display frame test - END

			// initialized for 1st frame
			motionVectorDescriptorArray[0] = 0;

			for (int frameItr = 1; frameItr < videoFrames.size(); frameItr++) {

				long fMotionValue = thisObj.getFrameMotionValue(frameItr);
				if (maxMotionVector < fMotionValue)
					maxMotionVector = fMotionValue;
				motionVectorLongArray[frameItr] = fMotionValue;
				System.out.println(fMotionValue);

				// test-code for displaying frame - START
				VideoFrameBean prevFrame = videoFrames.get(frameItr - 1);
				int[][] prevFramePixels = prevFrame.getFramePixels();
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						prevFrameImg.setRGB(x, y, prevFramePixels[y][x]);
					}
				}
				SwingUtilities.updateComponentTreeUI(frame1);

				VideoFrameBean currFrame = videoFrames.get(frameItr);
				int[][] currFramePixels = currFrame.getFramePixels();
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						currFrameImg.setRGB(x, y, currFramePixels[y][x]);
					}
				}
				SwingUtilities.updateComponentTreeUI(frame2);
				// test-code for displaying frame - END

			}

			for (int windowItr = 0; windowItr < motionVectorDescriptorArray.length; windowItr++) {

				motionVectorDescriptorArray[windowItr] = (int) ((motionVectorLongArray[windowItr] / (double) maxMotionVector) * 255);

			}

			BufferedImage vDescImage = Utilities
					.createDescriptorImage(motionVectorDescriptorArray);
			Utilities.displayImage(vDescImage, "VideoDescriptor");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private long getFrameMotionValue(int frameItr) {
		long finalMotionValue = 0;

		VideoFrameBean previousFrame = videoFrames.get(frameItr - 1);
		VideoFrameBean currentFrame = videoFrames.get(frameItr);

		int[][] prevFramePixels = previousFrame.getFramePixels();
		List<int[][]> currFrameBlocks = currentFrame.getPixMacroBlocks();
		int matchedHeight = 0;
		int matchedWidth = 0;

		int blockCount = 0;
		int blockIndexWidth = 0;
		int blockIndexHeight = 0;

		// Loop over all the blocks of current frame
		for (int[][] currentFrameBlock : currFrameBlocks) {

			long currentBlockMinDiff = Long.MAX_VALUE;

			// Loop over previous full frame
			for (int hItr = 0; hItr < Constants.HEIGHT
					- Constants.MACRO_BLOCK_SIZE; hItr++) {
				for (int wItr = 0; wItr < Constants.WIDTH
						- Constants.MACRO_BLOCK_SIZE; wItr++) {

					// initialized sum
					long cumulativeSum = 0;

					// Loop
					int currFrameHeight = 0;
					int currFrameWidth = 0;

					for (int prevFrameHItr = hItr; prevFrameHItr < hItr
							+ Constants.MACRO_BLOCK_SIZE; prevFrameHItr++) {
						currFrameWidth = 0;
						for (int prevFrameWItr = wItr; prevFrameWItr < wItr
								+ Constants.MACRO_BLOCK_SIZE; prevFrameWItr++) {
							long pixDiff = Math
									.abs(currentFrameBlock[currFrameHeight][currFrameWidth]
											- prevFramePixels[prevFrameHItr][prevFrameWItr]);
							cumulativeSum += pixDiff;
							if (cumulativeSum < 0)
								cumulativeSum = Long.MAX_VALUE;
							currFrameWidth++;
						}
						currFrameHeight++;
					}

					// Storing the pixel (start of the block best matched with
					// currentBlock of the currentFrame)
					if (currentBlockMinDiff >= cumulativeSum) {

						currentBlockMinDiff = cumulativeSum;
						matchedHeight = hItr;
						matchedWidth = wItr;

					}

				}
			}

			int dx = Math.abs(blockIndexWidth - matchedWidth);
			int dy = Math.abs(blockIndexHeight - matchedHeight);

			finalMotionValue += (dx + dy);

			blockCount++;
			if (blockCount
					% ((int) Constants.WIDTH / Constants.MACRO_BLOCK_SIZE) == 0) {
				blockIndexWidth = 0;
				blockIndexHeight += Constants.MACRO_BLOCK_SIZE;
			} else {
				blockIndexWidth += Constants.MACRO_BLOCK_SIZE;
			}

		}

		return finalMotionValue;
	}

}
