package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import net.miginfocom.swing.MigLayout;

import me.Aron.Heinecke.VocableTrainer.gui.PanelController;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;

/**
 * Start dialog panel
 * @author Aron Heinecke
 */
public class JStartPanel extends JPanelBase {
	
	private static final long serialVersionUID = -4542395160770274756L;
	private JButton btnStartTraining;

	public JStartPanel(PanelController panelcontroller) {
		super(panelcontroller);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow][grow]", "[47.00,grow,center][grow]"));
		
		btnStartTraining = new JButton("Start Training");
		btnStartTraining.setMaximumSize(new Dimension(194, 46));
		btnStartTraining.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setListPickerData(new ListPickerData(true, WINDOW_STATE.TRAINING_SETTINGS));
				changeWindow(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		this.add(btnStartTraining, "cell 0 0,alignx center,aligny center");
		
		JButton btnEditList = new JButton("Edit List");
		btnEditList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setListPickerData(new ListPickerData(false,WINDOW_STATE.LIST_EDIT));
				changeWindow(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		this.add(btnEditList, "cell 1 0,alignx center,aligny center");
		
		JButton btnNewList = new JButton("New List");
		btnNewList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeWindow(WINDOW_STATE.LIST_EDIT);
			}
		});
		this.add(btnNewList, "cell 1 1,alignx center,aligny center");
	}

	@Override
	public void resetView() {
		getFrame().getRootPane().setDefaultButton(btnStartTraining);
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exitRequest() {
		// TODO Auto-generated method stub
		return false;
	}
}
