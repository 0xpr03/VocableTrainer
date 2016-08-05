package me.Aron.Heinecke.VocableTrainer.gui;

import javax.swing.JFrame;

import me.Aron.Heinecke.VocableTrainer.MainWindow;
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
	
	public enum WINDOW_STATE {
		START,LIST_EDIT,LIST_CHOOSE,TRAINER,TRAINING_SETTINGS,PLACEHOLDER_LAST
	}
	private MainWindow mainwindow;
	
	public PanelController(MainWindow bw){
		mainwindow = bw;
		this.listpickerdata = new ListPickerData();
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
}
