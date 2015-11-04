package com.mult.core.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.mult.util.Constants;
import com.mult.util.Utilities;
import com.mult.util.VideoFrameBean;

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
	 * Generate Video Frames and all it's properties
	 * 
	 * 
	 * @param videoPath
	 * @return List<VideoFrameBean>
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<VideoFrameBean> generateVideoFrames(File videoFile) throws IOException,
			InterruptedException {
		
		Utilities.trace("generateVideoFrames START");
		
		//Create list of frames
		List<VideoFrameBean> videoFrames = new ArrayList<VideoFrameBean>();
		
		//Read From Video File- START
		InputStream videoIS = new FileInputStream(videoFile);
		long len = videoFile.length();
		Utilities.trace("Length of file: " + len);
		byte[] bytes = new byte[(int) len];
		int offset = 0;
		int numRead = 0;
		
		//read raw data into bytes array
		while (offset < bytes.length
				&& (numRead = videoIS.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		videoIS.close();
		//Read From Video File- END

		int ind = 0;
		int cntFrames = 0;
		int[] componentArray = null;
		int[][] framePixels = null;
		
		// Loop for number of frames (150 in our case)
		while (cntFrames < Constants.NO_OF_FRAMES) {
			// Generate Each Frame with required properties
			
			//Created the bean object and each property is assigned here
			VideoFrameBean videoFrame = new VideoFrameBean();
			componentArray = new int[(int) Constants.HEIGHT * Constants.WIDTH * 3];
			framePixels = new int[Constants.WIDTH][ Constants.HEIGHT];
			
			int idx = 0;
			for (int y = 0; y < Constants.HEIGHT; y++) {
				for (int x = 0; x < Constants.WIDTH; x++) {
					int r = bytes[ind];
					int g = bytes[ind + Constants.HEIGHT * Constants.WIDTH];
					int b = bytes[ind + Constants.HEIGHT * Constants.WIDTH * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);

					int rCmp = ((pix >> 16) & 0xff);
					int gCmp = ((pix >> 8) & 0xff);
					int bCmp = ((pix) & 0xff);

					componentArray[idx] = rCmp;
					componentArray[idx + Constants.HEIGHT * Constants.WIDTH] = gCmp;
					componentArray[idx + Constants.HEIGHT * Constants.WIDTH * 2] = bCmp;
					
					framePixels[x][y] = pix;
					
					ind++;
					idx++;
				}
			}
			
			videoFrame.setFramesComponents(componentArray);
			videoFrame.setFramePixels(framePixels);
			videoFrame.setCompMacroBlocksMap(getBlocksComponentsFromImageComponents(componentArray));
			videoFrame.setPixMacroBlocks(getBlocksPixelsFromImageComponents(componentArray));
			
			videoFrames.add(videoFrame);
			
			ind = ind + (Constants.HEIGHT * Constants.WIDTH * 2);
			cntFrames++;
		}

		Utilities.trace("Final Frames Count " + cntFrames + " idx " + ind);
		Utilities.trace("generateVideoFrames END");

		return videoFrames;
	}

	// test code
	public void testBlocks(List<VideoFrameBean> videoFrames, BufferedImage img,
			JFrame frame) throws InterruptedException {
		//test code ---> START
		Thread.sleep(3000);
		//test for components- START
		Utilities.trace("TEST WITH COMPONENTS(R, G, B) MACRO BLOCKS");
		for (VideoFrameBean videoFrame : videoFrames) {
			Map<String, List<int[][]>> blocks = videoFrame.getCompMacroBlocksMap();

			int[] newComponents = getImageComponentsFromBlocks(
					blocks.get("red"), blocks.get("green"), blocks.get("blue")); 
					
			int ind = 0;
			for (int y = 0; y < Constants.HEIGHT; y++) {
				for (int x = 0; x < Constants.WIDTH; x++) {

					int r = newComponents[ind];
					int g = newComponents[ind + Constants.HEIGHT
							* Constants.WIDTH];
					int b = newComponents[ind + Constants.HEIGHT
							* Constants.WIDTH * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);

					img.setRGB(x, y, pix);
					ind++;
				}
			}

			SwingUtilities.updateComponentTreeUI(frame);
			Thread.sleep(10);
		}
		//test for components- END
		
		Thread.sleep(3000);
		//test with pixels- START
		Utilities.trace("TEST WITH PIXEL MACRO BLOCKS");
		for (VideoFrameBean videoFrame : videoFrames) {
			List<int[][]> pixBlocks = videoFrame.getPixMacroBlocks();
			int[] newComponentsPixs = getImagePixelsFromBlocks(pixBlocks);
			
			int ind = 0;
			for (int y = 0; y < Constants.HEIGHT; y++) {
				for (int x = 0; x < Constants.WIDTH; x++) {
					int pix = newComponentsPixs[ind];
					img.setRGB(x, y, pix);
					ind++;
				}
			}
			
			SwingUtilities.updateComponentTreeUI(frame);
			Thread.sleep(10);
		}
		//test with pixels- END
		
		// test code ----> END
	}

	/**
	 * This function returns the map of red, green and blue components of 15x15
	 * blocks in the frame (image) 'red' -> List<int[][]>, the list is list of
	 * arrays for each block so there will be 192 arrays in list for 15x15 block
	 * size, for each of 'red', 'green' and 'blue' component
	 * 
	 * @param bytes
	 * @return Map
	 */
	public Map<String, List<int[][]>> getBlocksComponentsFromImageComponents(int[] bytes) {

		Map<String, List<int[][]>> blocks = new HashMap<String, List<int[][]>>();

		// Create list holding all the blocks for red, green and blue components
		List<int[][]> redBlockList = new ArrayList<int[][]>();
		List<int[][]> greenBlockList = new ArrayList<int[][]>();
		List<int[][]> blueBlockList = new ArrayList<int[][]>();

		int greenStep = Constants.HEIGHT * Constants.WIDTH;
		int blueStep = Constants.HEIGHT * Constants.WIDTH * 2;
		int heightStep = 0;
		int macroBlockSize = Constants.MACRO_BLOCK_SIZE;

		for (int ind = 0; ind < Constants.HEIGHT * Constants.WIDTH;) {
			// create RGB component blocks (15x15)
			int[][] redCompBlock = new int[macroBlockSize][macroBlockSize];
			int[][] greenCompBlock = new int[macroBlockSize][macroBlockSize];
			int[][] blueCompBlock = new int[macroBlockSize][macroBlockSize];

			int step = ind;
			for (int i = 0; i < macroBlockSize; i++) {
				for (int j = 0; j < macroBlockSize; j++) {
					redCompBlock[i][j] = bytes[step + j];
					greenCompBlock[i][j] = bytes[step + greenStep + j];
					blueCompBlock[i][j] = bytes[step + blueStep + j];
				}
				step = step + Constants.WIDTH;
			}

			ind = ind + macroBlockSize;
			if (ind % Constants.WIDTH == 0) {
				heightStep = heightStep + (Constants.WIDTH * macroBlockSize);
				ind = heightStep;
			}

			// Add blocks to list
			redBlockList.add(redCompBlock);
			greenBlockList.add(greenCompBlock);
			blueBlockList.add(blueCompBlock);
		}

		blocks.put("red", redBlockList);
		blocks.put("green", greenBlockList);
		blocks.put("blue", blueBlockList);

		return blocks;
	}

	/**
	 * Similar to above function but returns the blocks as pixels instead of the
	 * components
	 * 
	 * @param bytes
	 * @return
	 */
	public List<int[][]> getBlocksPixelsFromImageComponents(int[] bytes) {
		
		List<int[][]> blocks = new ArrayList<int[][]>();
		
		int greenStep = Constants.HEIGHT * Constants.WIDTH;
		int blueStep =  Constants.HEIGHT * Constants.WIDTH * 2;
		int heightStep = 0;
		int macroBlockSize = Constants.MACRO_BLOCK_SIZE;

		for (int ind = 0; ind < Constants.HEIGHT * Constants.WIDTH;) {

			// create RGB component blocks (15x15)
			int redComp = 0;
			int greenComp = 0;
			int blueComp = 0;
			int[][] pixBlock = new int[macroBlockSize][macroBlockSize];

			int step = ind;
			for (int i = 0; i < macroBlockSize; i++) {
				for (int j = 0; j < macroBlockSize; j++) {
					redComp = bytes[step + j];
					greenComp = bytes[step + greenStep + j];
					blueComp = bytes[step + blueStep + j];
					
					int pix = 0xff000000 | ((redComp & 0xff) << 16)
							| ((greenComp & 0xff) << 8) | (blueComp & 0xff);
				
					pixBlock[i][j] = pix;
				}
				step = step + Constants.WIDTH;
			}

			ind = ind + macroBlockSize;
			if (ind % Constants.WIDTH == 0) {
				heightStep = heightStep + (Constants.WIDTH * macroBlockSize);
				ind = heightStep;
			}

			// Add pixel block to list
			blocks.add(pixBlock);
		}

		return blocks;
	}

	/**
	 * Function returns the original component array from the blocks created in
	 * above method
	 * 
	 * @param redBlockList
	 * @param greenBlockList
	 * @param blueBlockList
	 * @return
	 */
	private int[] getImageComponentsFromBlocks(
			List<int[][]> redBlockList, List<int[][]> greenBlockList,
			List<int[][]> blueBlockList) {

		int[] bytes = new int[Constants.WIDTH * Constants.HEIGHT * 3];
		int idx = 0;
		int greenStep = Constants.WIDTH * Constants.HEIGHT;
		int blueStep = Constants.WIDTH * Constants.HEIGHT * 2;
		int macroBlockSize = Constants.MACRO_BLOCK_SIZE;
		int step = 0;
		int heightStep = 0;

		for (int i = 0; i < redBlockList.size(); i++) {

			int[][] red = redBlockList.get(i);
			int[][] green = greenBlockList.get(i);
			int[][] blue = blueBlockList.get(i);

			if (i != 0 && (i % ((int) Constants.WIDTH / macroBlockSize)) == 0) {
				heightStep = heightStep + (Constants.WIDTH * macroBlockSize);
				idx = heightStep;
				step = 0;
			} else {
				idx = heightStep;
			}

			for (int j = 0; j < macroBlockSize; j++) {
				for (int k = 0; k < macroBlockSize; k++) {
					bytes[k + idx + step] = red[j][k];
					bytes[k + idx + greenStep + step] = green[j][k];
					bytes[k + idx + blueStep + step] = blue[j][k];
				}
				idx = idx + Constants.WIDTH;
			}
			step = step + macroBlockSize;
		}
		return bytes;
	}
	
	/**
	 * Similar to above method getImageComponentsFromBlocks()
	 * but returns the pixels in 1D Array
	 * 
	 * @param blocks
	 * @return
	 */
	private int[] getImagePixelsFromBlocks(
			List<int[][]> blocks) {
		
		int[] bytes = new int[Constants.WIDTH * Constants.HEIGHT];
		
		int idx = 0;
		
		int macroBlockSize = Constants.MACRO_BLOCK_SIZE;
		
		int step = 0;
		int heightStep = 0;

		for (int i = 0; i < blocks.size(); i++) {

			int[][] pixBlock = blocks.get(i);

			if (i != 0 && (i % ((int) Constants.WIDTH / macroBlockSize)) == 0) {
				heightStep = heightStep + (Constants.WIDTH * macroBlockSize);
				idx = heightStep;
				step = 0;
			} else {
				idx = heightStep;
			}

			for (int j = 0; j < macroBlockSize; j++) {
				for (int k = 0; k < macroBlockSize; k++) {
					bytes[k + idx + step] = pixBlock[j][k];
				}
				idx = idx + Constants.WIDTH;
			}
			step = step + macroBlockSize;
		}
		return bytes;
	}


}
