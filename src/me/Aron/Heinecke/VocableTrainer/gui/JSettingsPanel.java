package me.Aron.Heinecke.VocableTrainer.gui;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.Aron.Heinecke.VocableTrainer.lib.CLabel;
import net.miginfocom.swing.MigLayout;

public class JSettingsPanel extends JPanelBase {

	//font trainer,editor,menu,
	private static final long serialVersionUID = -1044241660263207646L;
	private JLabel lblExampleText_General;
	private JLabel lblExampleTest_Editor;
	private JLabel lblExampleTest_Trainer;

	public JSettingsPanel(PanelController pc) {
		super(pc);
		initView();
	}
	
	private void initView(){
		setLayout(new MigLayout("", "[grow]", "[][grow][baseline]"));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[grow,center]", "[]"));
		
		JLabel lblSettings = new CLabel("Settings",getMainFont());
		panel_1.add(lblSettings, "cell 0 0");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[grow,left][grow,center][grow,left]", "[][][]"));
		
		JLabel lblGeneralFont = new CLabel("General Font",getMainFont());
		panel.add(lblGeneralFont, "cell 0 0");
		
		JButton btnEditFont_General = new JButton("Edit Font");
		btnEditFont_General.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblExampleText_General.setFont(showFontDialog(lblExampleText_General.getFont()));
			}
		});
		panel.add(btnEditFont_General, "cell 1 0");
		
		lblExampleText_General = new CLabel("Example Test Text",getMainFont());
		panel.add(lblExampleText_General, "cell 2 0");
		
		JLabel lblEditorFont = new CLabel("Editor Font",getMainFont());
		panel.add(lblEditorFont, "cell 0 1");
		
		JButton btnEditFont_Editor = new JButton("Edit Font");
		btnEditFont_Editor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblExampleTest_Editor.setFont(showFontDialog(lblExampleTest_Editor.getFont()));
			}
		});
		panel.add(btnEditFont_Editor, "cell 1 1");
		
		lblExampleTest_Editor = new CLabel("Example Test Text",getMainFont());
		panel.add(lblExampleTest_Editor, "cell 2 1");
		
		JLabel lblTrainerFont = new CLabel("Trainer Font",getMainFont());
		panel.add(lblTrainerFont, "cell 0 2");
		
		JButton btnEditFont_Trainer = new JButton("Edit Font");
		btnEditFont_Trainer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblExampleTest_Trainer.setFont(showFontDialog(lblExampleTest_Trainer.getFont()));
			}
		});
		panel.add(btnEditFont_Trainer, "cell 1 2");
		
		lblExampleTest_Trainer = new CLabel("Example Test Text",getMainFont());
		panel.add(lblExampleTest_Trainer, "cell 2 2");
		
		JPanel panel_2 = new JPanel();
		add(panel_2, "cell 0 2,grow");
		panel_2.setLayout(new MigLayout("", "[grow,right][right][right]", "[]"));
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMainFont(lblExampleText_General.getFont());
				setEditorFont(lblExampleTest_Editor.getFont());
				setTrainerFont(lblExampleTest_Trainer.getFont());
				saveFonts();
				showLastWindow();
			}
		});
		panel_2.add(btnSave, "cell 0 0");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLastWindow();
			}
		});
		panel_2.add(btnCancel, "cell 1 0");
		
		JButton btnLoadDefaults = new JButton("Load defaults");
		btnLoadDefaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblExampleTest_Editor.setFont(getDefaultFont());
				lblExampleTest_Trainer.setFont(getFallbackFontTrainer());
				lblExampleText_General.setFont(getDefaultFont());
			}
		});
		panel_2.add(btnLoadDefaults, "cell 2 0");
	}
	
	private Font showFontDialog(Font font){
		JFontChooseDialog jfcd = new JFontChooseDialog();
		jfcd.setSelectedFont(font);
		jfcd.showDialog(getPanel());
		return jfcd.getSelectedFont();
	}
	
	@Override
	protected void resetView() {
		lblExampleText_General.setFont(getMainFont());
		lblExampleTest_Trainer.setFont(getTrainerFont());
		lblExampleTest_Editor.setFont(getEditorFont());
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean exitRequest() {
		// TODO Auto-generated method stub
		return false;
	}

}
