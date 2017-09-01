/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.gui.JExportPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JImportPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JListChoosePanel;
import me.Aron.Heinecke.VocableTrainer.gui.JListEditPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JPanelBase;
import me.Aron.Heinecke.VocableTrainer.gui.JSettingsPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JStartPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JTrainerPanel;
import me.Aron.Heinecke.VocableTrainer.gui.JTrainingSettingsPanel;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;

/**
 * Main application window
 * @author Aron Heinecke
 */
public class MainWindow {
	
	private final Logger logger = LogManager.getLogger();
	
	private JFrame frame;
	private JMenuBar menuBar;
	private final String version;
	private JPanelBase LAST_TAB_PANEL = null;
	private JPanelBase CURRENT_TAB_PANEL = null;
	private WINDOW_STATE LAST_TAB = null;
	private WINDOW_STATE CURRENT_TAB = null;
	public final String SETTINGS_FONT_GENERAL = "font_general";
	public final String SETTINGS_FONT_EDITOR = "font_editor";
	public final String SETTINGS_FONT_TRAINER = "font_trainer";
	
	
	// consts
	private final String K_WINDOW_SIZE_HEIGHT = "window_size_height";
	private final String K_WINDOW_SIZE_WIDTH = "window_size_width";
	private final String K_WINDOW_STATE = "window_state";
	private final String K_WINDOW_POS_X = "window_pos_x";
	private final String K_WINDOW_POS_Y = "window_pos_y";
	
	private PanelController panelcontroller;

	/**
	 * Create the application.
	 */
	public MainWindow(String version) {
		this.version = version;
		initialize();
		this.panelcontroller = new PanelController(this);
		switchTab(WINDOW_STATE.START, false);
		frame.pack();
		loadWindowSettings();
		this.frame.setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("VocableTrainer - "+version);
		frame.setMinimumSize(new Dimension(500, 450));
		frame.setPreferredSize(new Dimension(800,430));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				logger.entry();
				exit();
			}
		});
		frame.setBounds(100, 100, 366, 274);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mnMenu.add(mntmSettings);
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelcontroller.changeWindow(WINDOW_STATE.SETTINGS);
			}
		});
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnMenu.add(mntmExit);
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "VocableTrainer version "+version+"\nDB version "+Database.getDBVersion()+"\nSQLite "+Database.getSQLiteVersion()+"\nCopyright Aron Heinecke 2016", "About", JOptionPane.INFORMATION_MESSAGE, null);
			}
		});
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
	}
	
	/**
	 * Function for switching between tabs
	 * @param state new tab
	 * @param showLastTab if true, state will be ignored
	 */
	public void switchTab(WINDOW_STATE state, boolean showLastTab){
		logger.entry(state, showLastTab);
		if (CURRENT_TAB_PANEL != null){
			if (CURRENT_TAB_PANEL.requestExit()){
				logger.debug("Tab switch canceled.");
				return;
			}
			CURRENT_TAB_PANEL.exit();
		}
		if(showLastTab){
			if (LAST_TAB_PANEL == null)
				logger.fatal("LastTab event on last panel = null!");
			{
				WINDOW_STATE temp = CURRENT_TAB;
				CURRENT_TAB = LAST_TAB;
				LAST_TAB = temp;
			}
			{
				JPanelBase temp = CURRENT_TAB_PANEL;
				CURRENT_TAB_PANEL = LAST_TAB_PANEL;
				LAST_TAB_PANEL = temp;
			}
			switchPane();
		}else{
			JPanelBase pb;
			switch(state){
			case LIST_EDIT:
				pb = new JListEditPanel(panelcontroller);
				break;
			case LIST_CHOOSE:
				pb = new JListChoosePanel(panelcontroller);
				break;
			case TRAINING_SETTINGS:
				pb = new JTrainingSettingsPanel(panelcontroller);
				break;
			case TRAINER:
				pb = new JTrainerPanel(panelcontroller);
				break;
			case SETTINGS:
				pb = new JSettingsPanel(panelcontroller);
				break;
			default:
				logger.error("Unknown tab state {}",state);
			case START:
				pb = new JStartPanel(panelcontroller);
				break;
			case EXPORT:
				pb = new JExportPanel(panelcontroller);
				break;
			case IMPORT:
				pb = new JImportPanel(panelcontroller);
				break;
			}
			LAST_TAB_PANEL = CURRENT_TAB_PANEL;
			LAST_TAB = CURRENT_TAB;
			CURRENT_TAB_PANEL = pb;
			CURRENT_TAB = state;
			switchPane();
			pb.resetViewIntern();
		}
		logger.exit();
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	/**
	 * Exit and show confirm dialog for some specific tabs
	 */
	private void exit(){
		if (CURRENT_TAB_PANEL.requestExit())
			return;
		CURRENT_TAB_PANEL.exit();
		frame.setEnabled(false);
		saveWindowSettings();
		System.exit(0);
	}
	
	/**
	 * Removes old panel, adds new panel
	 */
	private void switchPane(){
			logger.entry(LAST_TAB_PANEL != null);
			if (LAST_TAB_PANEL != null)
				frame.getContentPane().remove(LAST_TAB_PANEL);
			frame.getContentPane().add(CURRENT_TAB_PANEL);
			frame.validate();
			frame.repaint();
	}
	
	/**
	 * Load window size from preferences
	 */
	private void loadWindowSettings(){
		DBResult<String> rs_height = Database.getSettingsValue(K_WINDOW_SIZE_HEIGHT);
		DBResult<String> rs_width = Database.getSettingsValue(K_WINDOW_SIZE_WIDTH);
		if(rs_height.hasValue() && rs_width.hasValue()){
			frame.setSize(Integer.valueOf(rs_width.value), Integer.valueOf(rs_height.value));
		}else{
			logger.debug("No stored window size.");
		}
		DBResult<String> rs_state = Database.getSettingsValue(K_WINDOW_STATE);
		if(rs_state.hasValue()){
			frame.setState(Integer.valueOf(rs_state.value));
		}
		DBResult<String> rs_posX = Database.getSettingsValue(K_WINDOW_POS_X);
		DBResult<String> rs_posY = Database.getSettingsValue(K_WINDOW_POS_Y);
		if(rs_posX.hasValue() && rs_posY.hasValue()){
			frame.setLocation(Integer.valueOf(rs_posX.value), Integer.valueOf(rs_posY.value));
		}
	}
	
	/**
	 * Save window size to preferences
	 */
	private void saveWindowSettings(){
		logger.entry();
		Dimension wDim = frame.getSize();
		Point wP = frame.getLocationOnScreen();
		Database.setSettingsValue(K_WINDOW_SIZE_WIDTH, String.valueOf(wDim.width));
		Database.setSettingsValue(K_WINDOW_SIZE_HEIGHT, String.valueOf(wDim.height));
		Database.setSettingsValue(K_WINDOW_STATE, String.valueOf(frame.getState()));
		Database.setSettingsValue(K_WINDOW_POS_X, String.valueOf(wP.x));
		Database.setSettingsValue(K_WINDOW_POS_Y, String.valueOf(wP.y));
	}
}
