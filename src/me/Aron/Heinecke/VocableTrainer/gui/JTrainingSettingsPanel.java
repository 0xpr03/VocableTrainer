package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import me.Aron.Heinecke.VocableTrainer.Trainer.TestMode;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.store.TrainerSettings;
import net.miginfocom.swing.MigLayout;

/**
 * Training settings dialog panel<br>
 * Uses controller's TrainerSettings
 * @author Aron Heinecke
 */
public class JTrainingSettingsPanel extends JPanelBase {
	
	private static final long serialVersionUID = 6340634208416831983L;
	private JTable table_1;
	private JRadioButton chckbxRepeatAllVocables;
	private JRadioButton chckbxRefresh;
	private JComboBox<TestMode> comboTrainerMode;
	private JSpinner spinnerShowXTimes;
	private JSpinner spinnerMaxDays;

	public JTrainingSettingsPanel(PanelController pc) {
		super(pc);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow,leading]", "[][160.00,grow][grow][]"));
		
		JLabel lblStartTrainingInfo = new JLabel("Training Settings");
		this.add(lblStartTrainingInfo, "cell 0 0,alignx center");
		
		JScrollPane scrollPane_4 = new JScrollPane();
		this.add(scrollPane_4, "cell 0 1,grow");
		
		table_1 = new JTable();
		scrollPane_4.setViewportView(table_1);
		
		JPanel panel_7 = new JPanel();
		this.add(panel_7, "cell 0 2,grow");
		panel_7.setLayout(new MigLayout("", "[][][41.00][]", "[][][][]"));
		
		chckbxRepeatAllVocables = new JRadioButton("Repeat all vocables");
		chckbxRepeatAllVocables.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeTrainingSettingsSwitch(true);
			}
		});
		panel_7.add(chckbxRepeatAllVocables, "cell 0 0");
		
		chckbxRefresh = new JRadioButton("refresh");
		chckbxRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeTrainingSettingsSwitch(false);
			}
		});
		panel_7.add(chckbxRefresh, "cell 0 1");
		
		JLabel lblRepeatVocablesNot = new JLabel("repeat vocables not trained since");
		panel_7.add(lblRepeatVocablesNot, "cell 1 1");
		
		spinnerMaxDays = new JSpinner();
		spinnerMaxDays.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel_7.add(spinnerMaxDays, "cell 2 1,growx");
		
		JLabel lblDays_1 = new JLabel("days");
		panel_7.add(lblDays_1, "cell 3 1");
		
		JLabel lblShowEveryVocable = new JLabel("Show every vocable at least");
		panel_7.add(lblShowEveryVocable, "cell 0 2");
		
		spinnerShowXTimes = new JSpinner();
		spinnerShowXTimes.setModel(new SpinnerNumberModel(new Integer(4), new Integer(1), null, new Integer(1)));
		panel_7.add(spinnerShowXTimes, "flowx,cell 1 2");
		
		comboTrainerMode = new JComboBox<TestMode>();
		comboTrainerMode.setModel(new DefaultComboBoxModel<TestMode>(new TestMode[] {TestMode.A_B,TestMode.B_A,TestMode.AB}));
		panel_7.add(new JLabel("Test mode "), "cell 0 3");
		panel_7.add(comboTrainerMode, "cell 1 3");
		
		JLabel lblTimes = new JLabel("times");
		panel_7.add(lblTimes, "cell 1 2");
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTrainerSettings(new TrainerSettings(chckbxRepeatAllVocables.isSelected(),(int) spinnerMaxDays.getValue(),(int) spinnerShowXTimes.getValue(), (TestMode) comboTrainerMode.getSelectedItem()));
				changeWindow(WINDOW_STATE.TRAINER);
			}
		});
		this.add(btnStart, "flowx,cell 0 3");
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		this.add(horizontalStrut, "cell 0 3");
		
		JButton btnCancelTrainingSettings = new JButton("Cancel");
		btnCancelTrainingSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO: use ?
				//LISTPICKERDATA.cleanTableModel = false;
				showLastWindow();// WINDOW_STATE.LIST_CHOOSE
			}
		});
		this.add(btnCancelTrainingSettings, "cell 0 3");
		
	}

	@Override
	public void resetView() {
		chckbxRepeatAllVocables.setSelected(true);
		changeTrainingSettingsSwitch(true);
	}
	
	/**
	 * Selection switcher for chckbxRepeatAllVocables & chckbxRefresh
	 * 
	 */
	private void changeTrainingSettingsSwitch(boolean repeatChanged){
		boolean	repeat = repeatChanged && chckbxRepeatAllVocables.isSelected();
		chckbxRefresh.setSelected(!repeat);
		chckbxRepeatAllVocables.setSelected(repeat);
		spinnerMaxDays.setEnabled(!repeat);
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exitRequest() {
		return false;
	}

}
