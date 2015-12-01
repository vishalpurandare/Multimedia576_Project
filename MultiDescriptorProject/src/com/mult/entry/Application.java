package com.mult.entry;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
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

import com.mult.core.audio.AudioIntensityDescriptor;
import com.mult.core.color.ColorDescriptor;
import com.mult.util.Constants;
import com.mult.util.DescriptorBean;
import com.mult.util.DifferenceBean;
import com.mult.util.Utilities;

public class Application {

	//false if you are create database, true if you are calculate descriptors for test videos and compare against database
	public static boolean ifTestMode = true;
	
	/**
	 * Application Entry
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
				VideoDescriptorEntry vidEntryObj = new VideoDescriptorEntry();
				AudioIntensityDescriptor audObj = new AudioIntensityDescriptor();
				ColorDescriptor colorObj = new ColorDescriptor();
			
				if (!ifTestMode) {
					Utilities.trace("Create Database Mode");
					Utilities.trace("***** ***** ***** ***** ******");
					//Get Video Directory
					File dirVideo = new File(Constants.VIDEO_PATH_PC);
					File[] directoryListingVid = dirVideo.listFiles();
					
					//Get Audio Directory
					File dirAudio = new File(Constants.AUDIO_PATH_PC);
					File[] directoryListingAud = dirAudio.listFiles();
					
					if (directoryListingVid.length != directoryListingAud.length) {
						Utilities.trace("Invalid Directories or number of files in directories for videos and audios, they should match");
						return;
					}
					
					//Iterate over all the video files to create descriptors and serialize each in different files
					for (int fileItr = 0; fileItr < directoryListingVid.length; fileItr++) {
						File currFileVid = directoryListingVid[fileItr];
						File currFileAud = directoryListingAud[fileItr];
						
						//get video motion vector descriptor
						int[] motionVectorDescriptorArray = vidEntryObj.getVideoMotionVectorDescriptor(currFileVid, null, null, null, null, null);
						//get audio intensity descriptor
						int[] audioDescriptorArray = audObj.getAudioDescriptor(currFileAud, null, null);
						//get video color intensity descriptor (New one)
						int[] colorIntensityDescriptorArray = colorObj.getColorDescriptor(currFileVid, null, null);
						
						DescriptorBean descriptorObj = new DescriptorBean();
						List<int[]> descriptorsList = new ArrayList<int[]>();
						
						descriptorsList.add(motionVectorDescriptorArray);
						descriptorsList.add(audioDescriptorArray);
						descriptorsList.add(colorIntensityDescriptorArray);
						
						descriptorObj.setFileName(currFileVid.getName());
						descriptorObj.setDescriptorsList(descriptorsList);
						
						Utilities.serializeObject(descriptorObj, currFileVid.getName(), false);
						
						Utilities.trace("########################################################");
					}
				} else {
					Utilities.trace("Comparison mode, for test video");
					
					File dirVideo = new File(Constants.TEST_PATH_PC);
					File[] directoryListing = dirVideo.listFiles();
					
					//Taking first 2 test files, which are for same file, audio and video files
					File testFileVid = directoryListing[2];
					File testFileAud = directoryListing[3];
					
					// display frame (current and previous) test - START
					BufferedImage prevFrameImg = new BufferedImage(Constants.WIDTH,
							Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

					BufferedImage currFrameImg = new BufferedImage(Constants.WIDTH,
							Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);
					
					JFrame frameMain = new JFrame("Multimedia Descriptor Demo: Processign file: " + testFileVid.getName());
					GridLayout frameLayout = new GridLayout();
					frameMain.getContentPane().setLayout(frameLayout);
					
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
			        motionVectorPanel.setPreferredSize(new Dimension(300, 550));
			        
			        motionVectorPanel.add(panel1);
			        motionVectorPanel.add(panel2);
			        motionVectorPanel.add(panel3);
			        
			        JPanel barCodePanel = new JPanel();
			        barCodePanel.setLayout(new FlowLayout());
			        TitledBorder barCodeTitle = BorderFactory.createTitledBorder(lowerEtched, " Bar Codes for each of the Descriptor ");
			        barCodeTitle.setTitleFont( titleFont.deriveFont(Font.BOLD) );
			        barCodePanel.setBorder(barCodeTitle);
			        
			        JPanel matchedPanel = new JPanel();
			        matchedPanel.setLayout(new GridLayout(0, 1));
			        TitledBorder matchedPanelTitle = BorderFactory.createTitledBorder(lowerEtched, " Matched files best to worst ");
			        matchedPanelTitle.setTitleFont( titleFont.deriveFont(Font.BOLD) );
			        matchedPanel.setBorder(matchedPanelTitle);
			       
			        frameMain.add(motionVectorPanel);
			        frameMain.add(barCodePanel);
			        frameMain.add(matchedPanel);
					//frameMain.add(panel2);
					//frameMain.add(panel3);
					
					frameMain.pack();
					frameMain.setVisible(true);
					frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					// display frame (current and previous) test - END
					
					//get video motion vector descriptor
					int[] motionVectorDescriptorArray = vidEntryObj.getVideoMotionVectorDescriptor(testFileVid, prevFrameImg, currFrameImg, comp3, barCodePanel, frameMain);
					//get audio intensity descriptor
					int[] audioDescriptorArray = audObj.getAudioDescriptor(testFileAud, barCodePanel, frameMain);
					//get video color intensity descriptor (New one)
					int[] colorIntensityDescriptorArray = colorObj.getColorDescriptor(testFileVid, barCodePanel, frameMain);
					
					DescriptorBean descriptorObj = new DescriptorBean();
					List<int[]> descriptorsList = new ArrayList<int[]>();
					
					descriptorsList.add(motionVectorDescriptorArray);
					descriptorsList.add(audioDescriptorArray);
					descriptorsList.add(colorIntensityDescriptorArray);
					
					descriptorObj.setFileName(testFileVid.getName());
					descriptorObj.setDescriptorsList(descriptorsList);
					
					List<DifferenceBean> resultList = Utilities.bestMatchDecriptorToDb(descriptorObj);
					
					JPanel mainPanel = new JPanel();
					
					//Display result list in the file
					for (Iterator<DifferenceBean> iterator = resultList.iterator(); iterator.hasNext();) {
						DifferenceBean differenceBean = (DifferenceBean) iterator.next();
						JLabel compLabel = new JLabel(String.valueOf(0));
						compLabel.setText(differenceBean.getName());
						
						compLabel.addMouseListener(new MouseListener() {
							
							@Override
							public void mouseReleased(MouseEvent e) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void mousePressed(MouseEvent e) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void mouseExited(MouseEvent e) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void mouseEntered(MouseEvent e) {
								// TODO Auto-generated method stub
								compLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
							}
							
							@Override
							public void mouseClicked(MouseEvent e) {
								// TODO Auto-generated method stub
								barCodePanel.remove(mainPanel);
								//JPanel mainPanel = new JPanel();
								try {
									Utilities.createUIBestBean(differenceBean.getName(), barCodePanel, mainPanel, frameMain);
								} catch (ClassNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
						matchedPanel.add(compLabel);
						
						SwingUtilities.updateComponentTreeUI(frameMain);
					}
					
					DifferenceBean bestBean = resultList.get(0);
					Utilities.createUIBestBean(bestBean.getName(), barCodePanel, mainPanel, frameMain);
					
					Utilities.serializeObject(descriptorObj, testFileVid.getName(), true);
					Utilities.trace("########################################################");
				}
				
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
