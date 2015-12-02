package com.mult.entry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.mult.core.video.MotionDescriptor;
import com.mult.util.Constants;
import com.mult.util.DescriptorBean;
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
	
	/**
	 * main method
	 * used to testing video motion vector descriptor
	 * @param args
	 */
	public static void main(String[] args) {

		//File dirVideo = new File("C:\\Data\\imp-Data\\USC Data\\Courses-Fall 2015\\CSCI576-Multimedia System Design\\Final-Project\\Testing Fall 2015");
		File dirVideo = new File(Constants.VIDEO_PATH_PC);
		File[] directoryListing = dirVideo.listFiles();

		// currently only working on one video file, later do it for all files
		// in directoryListings
		File currFile = directoryListing[9];
		System.out.println(currFile);

		MotionDescriptor mObj = new MotionDescriptor();
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
			
			// Actual Motion Vector Descriptor Code
			long[] motionVectorLongArray = new long[150];
			
			thisObj.setVideoFrames(videoFrames);
			long maxMotionVector = Long.MIN_VALUE;
			long minMotionVector = Long.MAX_VALUE;

			// display frame (current and previous) test - START
			BufferedImage prevFrameImg = new BufferedImage(Constants.WIDTH,
					Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

			BufferedImage currFrameImg = new BufferedImage(Constants.WIDTH,
					Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);
			
			JFrame frameMain = new JFrame("Video Descriptor Demo");
			frameMain.setLayout(new FlowLayout());
			
			JPanel motionVectorPanel = new JPanel();
			motionVectorPanel.setLayout(new FlowLayout());
			
			JPanel panel1 = new JPanel();
			JComponent comp1 = new JLabel(new ImageIcon(prevFrameImg));
			panel1.add(comp1);
			panel1.setToolTipText("Previous Frame");
			UIManager.getDefaults().put("TitledBorder.titleColor", Color.BLACK);
		    Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		    TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, " Previous Frame ");
		    Font titleFont = UIManager.getFont("TitledBorder.font");
	        title.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        panel1.setBorder(title);
			
			JPanel panel2 = new JPanel();
			JComponent comp2 = new JLabel(new ImageIcon(currFrameImg));
			panel2.add(comp2);
			panel2.setToolTipText("Current Frame");
		    TitledBorder title2 = BorderFactory.createTitledBorder(lowerEtched, " Current Frame ");
	        title2.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        panel2.setBorder(title2);

			JPanel panel3 = new JPanel();
			JLabel comp3 = new JLabel(String.valueOf(0));
			panel3.add(comp3);
			panel3.setPreferredSize(new Dimension(240, 50));
			panel3.setToolTipText("Motion Vector for Frame");
		    TitledBorder title3 = BorderFactory.createTitledBorder(lowerEtched, " Motion Vector value for Frame ");
	        title3.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        panel3.setBorder(title3);
	        
	        TitledBorder motionTitle = BorderFactory.createTitledBorder(lowerEtched, " Descriptor based on Motion Vector ");
	        motionTitle.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        motionVectorPanel.setBorder(motionTitle);
	        
	        motionVectorPanel.add(panel1);
	        motionVectorPanel.add(panel2);
	        motionVectorPanel.add(panel3);
	        
			frameMain.add(motionVectorPanel);
			//frameMain.add(panel2);
			//frameMain.add(panel3);
			
			frameMain.pack();
			frameMain.setLocation(100, 100);
			frameMain.setVisible(true);
			frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// display frame (current and previous) test - END
			
			for (int frameItr = 1; frameItr < videoFrames.size(); frameItr++) {

				long fMotionValue = thisObj.getFrameMotionValue(frameItr);
				
				if (maxMotionVector < fMotionValue) {
					maxMotionVector = fMotionValue;
				}
				
				if (minMotionVector > fMotionValue) {
					minMotionVector = fMotionValue;
				}
				
				motionVectorLongArray[frameItr] = fMotionValue;
				System.out.println(fMotionValue);
				
				comp3.setText(String.valueOf(fMotionValue));
				// test-code for displaying frame (current and previous) - START
				VideoFrameBean prevFrame = videoFrames.get(frameItr - 1);
				int[][] prevFramePixels = prevFrame.getFramePixels();
				
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						prevFrameImg.setRGB(x, y, prevFramePixels[y][x]);
					}
				}
				
				VideoFrameBean currFrame = videoFrames.get(frameItr);
				int[][] currFramePixels = currFrame.getFramePixels();
				
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						currFrameImg.setRGB(x, y, currFramePixels[y][x]);
					}
				}
				SwingUtilities.updateComponentTreeUI(frameMain);
				// test-code for displaying frame (current and previous) - END
			}
			
			int[] motionVectorDescriptorArray = Utilities.getNormalizedDescriptorArray(motionVectorLongArray, maxMotionVector, minMotionVector, 1); 
			
			Map<String, int[]> descriptorMap = new HashMap<String, int[]>();
			//fileName -> arrayVal
			descriptorMap.put(currFile.getAbsolutePath(), motionVectorDescriptorArray);
			
			DescriptorBean descriptorObj = new DescriptorBean();
			List<int[]> descriptorsList = new ArrayList<int[]>();
			descriptorsList.add(motionVectorDescriptorArray);
			
			descriptorObj.setFileName(currFile.getAbsolutePath());
			descriptorObj.setDescriptorsList(descriptorsList);
			
			Utilities.serializeObject(descriptorObj, currFile.getName(), false);
			
			//Deserialize object
			/*DescriptorBean descriptorBeanRead = (DescriptorBean) Utilities.deSerializeObject(currFile.getName());
			List<int[]> descriptorList = descriptorBeanRead.getDescriptorsList();
			//get video descriptor for current video
			int[] motionVectorDescriptorArraySer = descriptorList.get(0);
			
			System.out.println("Are Equals: " + Arrays.equals(motionVectorDescriptorArray, motionVectorDescriptorArraySer));
			
			//Sample from serialized file
			BufferedImage vSerDescImage = Utilities
					.createDescriptorImage(motionVectorDescriptorArraySer);
			Utilities.displayImage(vSerDescImage, "VideoDescriptor");*/
			
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

	/**
	 * return motion vector descriptor called from Application
	 * @param currFile
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public int[] getVideoMotionVectorDescriptor(DescriptorBean descObj, File currFile, BufferedImage prevFrameImg, BufferedImage currFrameImg,JLabel motionValueComp,  JPanel barCodePanel, JFrame frameMain, boolean ifTest) throws IOException, InterruptedException, ClassNotFoundException {
		
		long startTime = System.currentTimeMillis();
		
		Utilities.trace("getVideoMotionVectorDescriptor START");
		Utilities.trace("Processing File: START " + currFile.getName() );
		
		boolean ifPresentInCache = false;
		int[] motionDescSerArr = null;
		if (ifTest) {
			//Check if the file is already serialized, then only take from file
			DescriptorBean descriptorObj = (DescriptorBean) Utilities.deSerializeObject(currFile.getName()+".ser", true);
			if (descriptorObj != null) {
				ifPresentInCache = true;
				List<int[]> descList = descriptorObj.getDescriptorsList();
				motionDescSerArr = descList.get(0);// get motion descriptor array
			} 
		}
		
		MotionDescriptor mObj = new MotionDescriptor();
		
		// (1) Divide The Video into frames
		List<VideoFrameBean> videoFrames = mObj
				.generateVideoFrames(currFile);

		// Actual Motion Vector Descriptor Code
		long[] motionVectorLongArray = new long[150];
		
		setVideoFrames(videoFrames);
		
		long maxMotionVector = Long.MIN_VALUE;
		long minMotionVector = Long.MAX_VALUE;

		for (int frameItr = 1; frameItr < videoFrames.size(); frameItr++) {
			
			long fMotionValue = 0;
			
			if (!ifPresentInCache) { //if not present in cache, calculate
				fMotionValue = getFrameMotionValue(frameItr);
			} else {
				fMotionValue = motionDescSerArr[frameItr];
				Thread.sleep(100);
			}
			
			if (maxMotionVector < fMotionValue) {
				maxMotionVector = fMotionValue;
			}
			
			if (minMotionVector > fMotionValue) {
				minMotionVector = fMotionValue;
			}
			
			//UI- Display
			if (frameMain != null) {
				
				motionValueComp.setText(String.valueOf(fMotionValue));
				
				// test-code for displaying frame (current and previous) - START
				VideoFrameBean prevFrame = videoFrames.get(frameItr - 1);
				int[][] prevFramePixels = prevFrame.getFramePixels();
				
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						prevFrameImg.setRGB(x, y, prevFramePixels[y][x]);
					}
				}
				
				VideoFrameBean currFrame = videoFrames.get(frameItr);
				int[][] currFramePixels = currFrame.getFramePixels();
				
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						currFrameImg.setRGB(x, y, currFramePixels[y][x]);
					}
				}
				SwingUtilities.updateComponentTreeUI(frameMain);
				// test-code for displaying frame (current and previous) - END
			}
			
			motionVectorLongArray[frameItr] = fMotionValue;
			
		}

		descObj.setMaxMotion(maxMotionVector);
		descObj.setMinMotion(minMotionVector);
		//Normalize values to 0-255
		int[] motionVectorDescriptorArray = Utilities.getNormalizedDescriptorArray(motionVectorLongArray, maxMotionVector, minMotionVector, 1);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		
		//UI- Display
		if (frameMain != null) {
			//Display Bar Code
			BufferedImage vDescImage = Utilities.createDescriptorImage(motionVectorDescriptorArray);
			
			JPanel panel1 = new JPanel();
			JComponent comp1 = new JLabel(new ImageIcon(vDescImage));
			panel1.add(comp1);
			panel1.setToolTipText("Motion Bar Code");
			UIManager.getDefaults().put("TitledBorder.titleColor", Color.BLACK);
		    Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		    TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, " Motion Descriptor ");
		    Font titleFont = UIManager.getFont("TitledBorder.font");
	        title.setTitleFont( titleFont.deriveFont(Font.BOLD) );
	        panel1.setBorder(title);
	        
			barCodePanel.add(panel1);
			SwingUtilities.updateComponentTreeUI(frameMain);
		}
		
		
		Utilities.trace("Total Time required: " + totalTime + " MS " + totalTime/(1000*60) + " Minutes");
		Utilities.trace("Processing File: END " + currFile.getName() );
		Utilities.trace("getVideoMotionVectorDescriptor END");
		
		return motionVectorDescriptorArray;
	}
	
	
	
	/**
	 * Returns the motion vector value for the frame
	 * @param frameItr
	 * @return
	 */
	private long getFrameMotionValue(int frameItr) {
		//Initialize the finalMotionValue
		long finalMotionValue = 0;
		
		//previous and current frames taken from videoFrames list
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
			
			long initialSum = 0;
			for (int hFItr = 0; hFItr < Constants.MACRO_BLOCK_SIZE; hFItr++) {
				for (int wFItr = 0; wFItr < Constants.MACRO_BLOCK_SIZE; wFItr++) {
					long pixDiff = Math
							.abs(currentFrameBlock[hFItr][wFItr]
									- prevFramePixels[hFItr + blockIndexHeight][wFItr + blockIndexWidth]);
					initialSum += pixDiff;
				}
			}
			if (initialSum == 0) {
				finalMotionValue += 0;
				continue;
			}
			
			long currentBlockMinDiff = Long.MAX_VALUE;
			int upVal = blockIndexHeight - Constants.SEARCH_WINDOW_SIZE;
			int downVal = blockIndexHeight + Constants.SEARCH_WINDOW_SIZE;
			
			int leftVal = blockIndexWidth - Constants.SEARCH_WINDOW_SIZE;
			int rightVal = blockIndexWidth + Constants.SEARCH_WINDOW_SIZE;
			
			int hItrStart = upVal < 0 ? 0 : upVal;
			int hItrEnd = downVal > Constants.HEIGHT ? Constants.HEIGHT : downVal;
			
			int wItrStart = leftVal < 0 ? 0 : leftVal;
			int wItrEnd = rightVal > Constants.WIDTH ? Constants.WIDTH : rightVal;
			
			// Loop over previous full frame
			for (int hItr = hItrStart; hItr < hItrEnd - Constants.MACRO_BLOCK_SIZE; hItr++) {
				
				for (int wItr = wItrStart; wItr < wItrEnd - Constants.MACRO_BLOCK_SIZE; wItr++) {

					// initialized sum
					long cumulativeSum = 0;

					// Loop
					int currFrameHeight = 0;
					int currFrameWidth = 0;

					for (int prevFrameHItr = hItr; prevFrameHItr < hItr + Constants.MACRO_BLOCK_SIZE; prevFrameHItr++) {
						
						currFrameWidth = 0;
						for (int prevFrameWItr = wItr; prevFrameWItr < wItr + Constants.MACRO_BLOCK_SIZE; prevFrameWItr++) {
							
							long pixDiff = Math
									.abs(currentFrameBlock[currFrameHeight][currFrameWidth]
											- prevFramePixels[prevFrameHItr][prevFrameWItr]);
							cumulativeSum += pixDiff;
							
							if (cumulativeSum < 0) {
								cumulativeSum = Long.MAX_VALUE;
							}
							
							currFrameWidth++;
						}
						currFrameHeight++;
					}
					
					//Mean Absolute Difference cumulativeSum
					cumulativeSum = Math.abs(cumulativeSum / (Constants.MACRO_BLOCK_SIZE * Constants.MACRO_BLOCK_SIZE));
					
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
			if (blockCount % ((int) Constants.WIDTH / Constants.MACRO_BLOCK_SIZE) == 0) {
				blockIndexWidth = 0;
				blockIndexHeight += Constants.MACRO_BLOCK_SIZE;
			} else {
				blockIndexWidth += Constants.MACRO_BLOCK_SIZE;
			}

		}

		return finalMotionValue;
	}

}
