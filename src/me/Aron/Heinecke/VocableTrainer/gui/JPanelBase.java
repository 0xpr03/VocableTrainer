package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import me.Aron.Heinecke.VocableTrainer.store.TrainerSettings;

/**
 * Base class for all dialog panels<br>
 * Providing panel actions, event handling
 * @author Aron Heinecke
 */
public abstract class JPanelBase extends JPanel {

	private static final long serialVersionUID = 809344288129652669L;
	
	protected Logger logger = LogManager.getLogger();
	private PanelController panelcontroller;
	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getDefaultFont()
	 */
	public Font getDefaultFont() {
		return panelcontroller.getDefaultFont();
	}

	/**
	 * Does not trigger the requestExit event !
	 * @param window
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#changeWindow(me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE)
	 */
	protected void changeWindow(WINDOW_STATE window) {
		setDisableExitCheck(true);
		panelcontroller.changeWindow(window);
	}

	/**
	 * @param lpd
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#setListPickerData(me.Aron.Heinecke.VocableTrainer.store.ListPickerData)
	 */
	public void setListPickerData(ListPickerData lpd) {
		panelcontroller.setListPickerData(lpd);
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getListPickerData()
	 */
	public ListPickerData getListPickerData() {
		return panelcontroller.getListPickerData();
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getTrainerSettings()
	 */
	public TrainerSettings getTrainerSettings() {
		return panelcontroller.getTrainerSettings();
	}

	/**
	 * @param ts
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#setTrainerSettings(me.Aron.Heinecke.VocableTrainer.store.TrainerSettings)
	 */
	public void setTrainerSettings(TrainerSettings ts) {
		panelcontroller.setTrainerSettings(ts);
	}

	/**
	 * Does not trigger the requestExit event !
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#showLastWindow()
	 */
	public void showLastWindow() {
		setDisableExitCheck(true);
		panelcontroller.showLastWindow();
	}

	private boolean disableExitCheck = false;
	/**
	 * Create the panel.
	 */
	public JPanelBase(PanelController panelcontroller) {
		super();
		this.panelcontroller = panelcontroller;
	}
	
	/**
	 * Init function, see resetView
	 */
	public void resetViewIntern(){
		setDisableExitCheck(false);
		resetView();
	}
	
	/**
	 * Init function called at least at the init<br>
	 * used to reset the view<br>
	 * Called after disableExitCheck reset
	 */
	protected abstract void resetView();
	
	/**
	 * Called at tab switch
	 */
	public abstract void exit();
	
	/**
	 * To be called before the panel is disposed / removed
	 * @return
	 */
	public boolean requestExit(){
		if (disableExitCheck)
			return false;
		return exitRequest();
	}
	
	/**
	 * Called before a tab is removed<br>
	 * if disablExitCheck is set, this will be ignored
	 * @return true cancels the operation
	 */
	protected abstract boolean exitRequest();
	
	/**
	 * Helper function returning this panel
	 * @return
	 */
	protected JPanel getPanel(){
		return this;
	}
	
	/**
	 * Helper function returning the frame
	 * @return
	 */
	protected JFrame getFrame(){
		return panelcontroller.getFrame();
	}

	/**
	 * Set to true to ignore exit requests
	 * @param disableExitCheck
	 */
	private void setDisableExitCheck(boolean disableExitCheck) {
		this.disableExitCheck = disableExitCheck;
	}

	/**
	 * 
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#saveFonts()
	 */
	public void saveFonts() {
		panelcontroller.saveFonts();
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getMainFont()
	 */
	public Font getMainFont() {
		return panelcontroller.getMainFont();
	}

	/**
	 * @param mainFont
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#setMainFont(java.awt.Font)
	 */
	public void setMainFont(Font mainFont) {
		panelcontroller.setMainFont(mainFont);
	}

	/**
	 * @param editorFont
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#setEditorFont(java.awt.Font)
	 */
	public void setEditorFont(Font editorFont) {
		panelcontroller.setEditorFont(editorFont);
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getTrainerFont()
	 */
	public Font getTrainerFont() {
		return panelcontroller.getTrainerFont();
	}

	/**
	 * @param trainerFont
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#setTrainerFont(java.awt.Font)
	 */
	public void setTrainerFont(Font trainerFont) {
		panelcontroller.setTrainerFont(trainerFont);
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getEditorFont()
	 */
	public Font getEditorFont() {
		return panelcontroller.getEditorFont();
	}

	/**
	 * @return
	 * @see me.Aron.Heinecke.VocableTrainer.gui.PanelController#getFallbackFontTrainer()
	 */
	public Font getFallbackFontTrainer() {
		return panelcontroller.getFallbackFontTrainer();
	}
}
