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

	public static void main(String[] args) {
		
		File dirVideo = new File(Constants.VIDEO_PATH_VISHAL_PC);
		File[] directoryListing = dirVideo.listFiles();
		
		//currently only working on one video file, later do it for all files in directoryListings
		File currFile = directoryListing[0];
		
		MotionDescriptor mObj = MotionDescriptor.getInstance();
		try {
			//(1) Divide The Video into frames
			List<VideoFrameBean> videoFrames = mObj.generateVideoFrames(currFile);
			
			// Test Image Display code - can be removed later
			Utilities.trace("Number of Frames processed: " + videoFrames.size() );
			
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
						img.setRGB(x, y, framePixels[x][y]);
					}
				}
				
				SwingUtilities.updateComponentTreeUI(frame);
				Thread.sleep(10);
			}
			 
			// call to test code
			mObj.testBlocks(videoFrames, img, frame);
			
			//(2) MPEG Based Motion-Compensation technique
			// (2.1) Divide each Frame into Macro-Block
			/*for (int[] compTempArr : framesComponents) {
				//Will return blocks for each of the frame (image), 15x15 block, 192 for each red, green and blue
				//Map<String, List<int[][]>> compMacroBlocksMap = mObj.getBlocksComponentsFromImageComponents(compTempArr);
				
				//get 2D pixels array from component Array for the frame
				int[][] pixelsArr = mObj.getPixelsFromImageComponents(compTempArr);
				// 192 blocks of 15x15 pixels for the frame
				List<int[][]> pixMacroBlocks = mObj.getBlocksPixelsFromImageComponents(compTempArr);
				
				
				
			}*/
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
