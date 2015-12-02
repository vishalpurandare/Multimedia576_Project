package com.mult.util;

/*
 * All variables must be defined in ALL_CAPS_UNDERSCORE_SEPARATED
 * 
 */
public class Constants {
	
	public static final int DESC_IMG_HEIGHT = 50;
	public static final int NO_OF_FRAMES = 150;
	public static final int AUDIO_SAMPLES_COUNT = 331081;
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String FILE_PATH_SEPARATOR = System.getProperty("file.separator");
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = 180;
	public static final int MACRO_BLOCK_SIZE = 15;
	
	public static final double VIDEO_WEIGHT = 1;
	public static final double AUDIO_WEIGHT = 1;
	public static final double COLOR_WEIGHT = 1;
	
	public static final int SEARCH_WINDOW_SIZE = 60;
	
	public static final String SERIALIZED_FILE_PATH = "C:" + FILE_PATH_SEPARATOR + "MultimediaData" +  FILE_PATH_SEPARATOR + "Database" + FILE_PATH_SEPARATOR;
	
	public static final String SERIALIZED_FILE_PATH_TEST = "C:" + FILE_PATH_SEPARATOR + "MultimediaData" +  FILE_PATH_SEPARATOR + "TestData" + FILE_PATH_SEPARATOR;
	
	public static final String VIDEO_PATH_PC = "C:" + FILE_PATH_SEPARATOR + "MultimediaData" + FILE_PATH_SEPARATOR + "video_samples";
	
	public static final String AUDIO_PATH_PC = "C:" + FILE_PATH_SEPARATOR + "MultimediaData" + FILE_PATH_SEPARATOR + "audio_wav";
	
	public static final String TEST_PATH_PC = "C:" + FILE_PATH_SEPARATOR + "MultimediaData"  + FILE_PATH_SEPARATOR + "Testing Fall 2015";
	
}
