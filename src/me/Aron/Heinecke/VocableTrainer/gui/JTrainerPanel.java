package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import me.Aron.Heinecke.VocableTrainer.Trainer;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.CButton;
import me.Aron.Heinecke.VocableTrainer.lib.CLabel;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import net.miginfocom.swing.MigLayout;

/**
 * Trainer dialog panel<br>
 * Uses controller's TrainerSettings
 * @author Aron Heinecke
 */
@SuppressWarnings("serial")
public class JTrainerPanel extends JPanelBase {
	
	private JTextArea vocable_showed;
	private JTextField vocInput;
	private CButton btnVerify;
	private CButton btnShowTip;
	private CButton btnResolve;
	private Trainer trainer;

	public JTrainerPanel(PanelController pc) {
		super(pc);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow]", "[][grow][grow][]"));
		
		JLabel lblVocable = new CLabel("Trainer",getMainFont());
		this.add(lblVocable, "cell 0 0");
		
		JScrollPane scrollPane_3 = new JScrollPane();
		this.add(scrollPane_3, "cell 0 1,grow");
		
		vocable_showed = new JTextArea();
		vocable_showed.setFont(getTrainerFont());
		scrollPane_3.setViewportView(vocable_showed);
		vocable_showed.setForeground(new Color(0, 0, 0));
		vocable_showed.setBackground(UIManager.getColor("EditorPane.background"));
		vocable_showed.setEditable(false);

		JScrollPane scrollPane_2 = new JScrollPane();
		this.add(scrollPane_2, "cell 0 2,grow");
		
		vocInput = new JTextField();
		vocInput.setFont(getTrainerFont());
		vocInput.setBackground(Color.WHITE);
		vocInput.setMargin(new Insets(0, 2, 0, 0));
		vocInput.setCaretColor(Color.BLACK);
		vocInput.setHorizontalAlignment(SwingConstants.LEFT);
		scrollPane_2.setViewportView(vocInput);
		vocInput.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		this.add(panel_1, "cell 0 3,grow");
		panel_1.setLayout(new MigLayout("", "[415px][415px,grow][]", "[23px]"));
		
		btnVerify = new CButton("Verify",getMainFont());
		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(trainer.verifySolution(vocInput.getText())){
					resetTrainerForm(true);
				}else{
					vocInput.setBackground(new Color(255, 150, 115));
				}
			}
		});
		panel_1.add(btnVerify, "flowx,cell 0 0 2 1,alignx left,aligny center");
		
		btnShowTip = new CButton("Show Tip",getMainFont());
		btnShowTip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnShowTip.setEnabled(false);
				vocable_showed.setText(vocable_showed.getText() + "\n"+trainer.getTip());
				getFrame().getRootPane().setDefaultButton(btnVerify);
				vocInput.requestFocus();
			}
		});
		btnShowTip.setToolTipText("Show tip");
		panel_1.add(btnShowTip, "cell 0 0 2 1,alignx left,aligny center");
		
		btnResolve = new CButton("Resolve",getMainFont());
		btnResolve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vocInput.setText(trainer.getSolution());
				vocInput.requestFocus();
			}
		});
		btnResolve.setToolTipText("Show solution");
		panel_1.add(btnResolve, "cell 0 0");
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		panel_1.add(horizontalGlue_2, "cell 0 0 2 1,alignx left,aligny top");
		
		CButton btnExitTrainer = new CButton("Exit Training",getMainFont());
		btnExitTrainer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!trainer.isFinished()){
					int chosen = JOptionPane.showConfirmDialog(getFrame(), "Do you really want to exit ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
					if(chosen != 0)
						return;
				}
				changeWindow(WINDOW_STATE.START);
			}
		});
		btnExitTrainer.setToolTipText("exit current session");
		panel_1.add(btnExitTrainer, "cell 2 0");
	}
	
	@Override
	public void resetView() {
		trainer = new Trainer(getListPickerData().getPicked(), getTrainerSettings());
		setListPickerData(new ListPickerData());
		resetTrainerForm(false);
		setTrainerEnables(false);
		this.vocable_showed.setText("Initializing, please wait..");
		new TrainerInitWorker(trainer).execute();
	}
	
	protected void setTrainerEnables(boolean enable){
		this.vocInput.setEnabled(enable);
		this.vocable_showed.setEnabled(enable);
		this.btnVerify.setEnabled(enable);
		this.btnShowTip.setEnabled(enable);
		this.btnResolve.setEnabled(enable);
	}
	
	public void trainerInputRequestInput(){
		logger.entry();
		this.vocInput.setEnabled(true);
		this.vocInput.requestFocus();
	}
	
	/**
	 * Reset trainer form AND show new vocable
	 */
	public void resetTrainerForm(boolean requestVocable){
		getFrame().getRootPane().setDefaultButton(this.btnVerify);
		this.vocInput.setText("");
		String vocable;
		if (requestVocable)
			vocable = trainer.getNewVocable();
		else
			vocable = null;
		vocInput.setBackground(Color.WHITE);
		this.btnShowTip.setEnabled(trainer.hasTip());
		this.vocInput.setEditable(vocable != null);
		this.btnVerify.setEnabled(vocable != null);
		this.btnResolve.setEnabled(vocable != null);
		if (requestVocable){
			if(vocable == null){
				this.vocable_showed.setText("FINISHED !\nStats:\n"+trainer.getStats());
			}else{
				this.vocable_showed.setText(vocable);
				this.vocInput.requestFocus();
			}
		}
	}

	@Override
	public void exit() {
		trainer.exit();
	}

	@Override
	public boolean exitRequest() {
		int chosen = JOptionPane.showConfirmDialog(getFrame(), "Do you really want to exit ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		return (chosen != 0);
	}
	
	class TrainerInitWorker extends SwingWorker<Object, Object> {
		Trainer trainer;
		public TrainerInitWorker(Trainer trainer){
			this.trainer = trainer;
		}
		@Override
		public Object doInBackground(){
			trainer.initTimeConsuming();
			return null;
		}
		
		@Override
		protected void done(){
			resetTrainerForm(true);
			trainerInputRequestInput();
		}
	}
}
