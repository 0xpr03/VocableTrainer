package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Font;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.MainWindow;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import me.Aron.Heinecke.VocableTrainer.store.TrainerSettings;

/**
 * Panel-Controller<br>
 * Storing cross-panel values
 * @author Aron Heinecke
 */
public class PanelController {
	
	private ListPickerData listpickerdata;
	private TrainerSettings trainersettings;
	private Font mainFont;
	private Font editorFont;
	private Font trainerFont;
	private Font fallbackFont = new Font("Dialog",Font.PLAIN,12);
	private Font fallbackFontTrainer = new Font("Tahoma", Font.PLAIN, 13);
	
	Logger logger = LogManager.getLogger();
	
	private final String DB_FONT_MAIN = "font_main";
	private final String DB_FONT_EDITOR = "font_editor";
	private final String DB_FONT_TRAINER = "font_trainer";
	private final String DB_FONT_1 = "1";
	private final String DB_FONT_2 = "2";
	private final String DB_FONT_3 = "3";
	
	public enum WINDOW_STATE {
		START,LIST_EDIT,LIST_CHOOSE,TRAINER,TRAINING_SETTINGS,SETTINGS,PLACEHOLDER_LAST
	}
	private MainWindow mainwindow;
	
	public PanelController(MainWindow bw){
		mainwindow = bw;
		this.listpickerdata = new ListPickerData();
		loadFonts();
	}
	
	public void changeWindow ( WINDOW_STATE window ) {
		mainwindow.switchTab(window, false);
	}
	
	public void showLastWindow(){
		mainwindow.switchTab(WINDOW_STATE.PLACEHOLDER_LAST, true);
	}
	
	public void setListPickerData(ListPickerData lpd){
		this.listpickerdata = lpd;
	}
	public ListPickerData getListPickerData() {
		return this.listpickerdata;
	}
	public TrainerSettings getTrainerSettings(){
		return trainersettings;
	}
	public void setTrainerSettings(TrainerSettings ts){
		this.trainersettings = ts;
	}
	public JFrame getFrame(){
		return mainwindow.getFrame();
	}
	
	public void loadFonts(){
		Font f_main = loadFont(DB_FONT_MAIN,fallbackFont);
		if(f_main != null)
			mainFont = f_main;
		Font f_editor = loadFont(DB_FONT_EDITOR,fallbackFont);
		if(f_editor != null)
			editorFont = f_editor;
		Font f_trainer = loadFont(DB_FONT_TRAINER,fallbackFontTrainer);
		if(f_editor != null)
			trainerFont = f_trainer;
	}
	
	private Font loadFont(String fontName, Font fallbackFont){
		logger.entry();
		DBResult<String> res1 = Database.getSettingsValue(fontName+DB_FONT_1);
		DBResult<String> res2 = Database.getSettingsValue(fontName+DB_FONT_2);
		DBResult<String> res3 = Database.getSettingsValue(fontName+DB_FONT_3);
		
		if(res1.isError || res2.isError || res3.isError || res1.value == null || res2.value == null || res3.value == null){
			return fallbackFont;
		}else{
			return new Font(res1.value, Integer.valueOf(res2.value), Integer.valueOf(res3.value));
		}
	}
	
	private boolean saveFont(Font font, String key){
		logger.entry();
		boolean bRet = false;
		if(!Database.setSettingsValue(key+DB_FONT_1, font.getFontName()).isError){
			if(!Database.setSettingsValue(key+DB_FONT_2, String.valueOf(font.getStyle())).isError){
				bRet = !Database.setSettingsValue(key+DB_FONT_3, String.valueOf(font.getSize())).isError;
			}
		}
		if(!bRet)
			logger.warn("Error on saving font {} as {}",font,key);
		return bRet;
	}
	
	public void saveFonts(){
		saveFont(mainFont, DB_FONT_MAIN);
		saveFont(editorFont, DB_FONT_EDITOR);
		saveFont(trainerFont, DB_FONT_TRAINER);
	}

	/**
	 * @return the mainFont
	 */
	public Font getMainFont() {
		return mainFont;
	}

	/**
	 * @param mainFont the mainFont to set
	 */
	public void setMainFont(Font mainFont) {
		this.mainFont = mainFont;
	}

	/**
	 * @return the editorFont
	 */
	public Font getEditorFont() {
		return editorFont;
	}

	/**
	 * @param editorFont the editorFont to set
	 */
	public void setEditorFont(Font editorFont) {
		this.editorFont = editorFont;
	}

	/**
	 * @return the trainerFont
	 */
	public Font getTrainerFont() {
		return trainerFont;
	}
	
	/**
	 * Returns the default font for fallbacks
	 * @return
	 */
	public Font getDefaultFont() {
		return fallbackFont;
	}

	/**
	 * @param trainerFont the trainerFont to set
	 */
	public void setTrainerFont(Font trainerFont) {
		this.trainerFont = trainerFont;
	}

	/**
	 * @return the fallbackFontTrainer
	 */
	public Font getFallbackFontTrainer() {
		return fallbackFontTrainer;
	}
}
