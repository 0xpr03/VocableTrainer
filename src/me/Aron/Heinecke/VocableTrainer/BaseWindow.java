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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.Trainer.TestMode;
import me.Aron.Heinecke.VocableTrainer.lib.ColumnNameDialog;
import me.Aron.Heinecke.VocableTrainer.lib.ForcedListSelectionModel;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableChooseModel;
import me.Aron.Heinecke.VocableTrainer.lib.TableListModel;
import me.Aron.Heinecke.VocableTrainer.lib.WaitLayerUI;
import me.Aron.Heinecke.VocableTrainer.store.ListData;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import net.miginfocom.swing.MigLayout;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.JRadioButton;

//TODO: replace table with layered pane
/**
 * Main application window
 * @author Aron Heinecke
 */
public abstract class BaseWindow {
	
	private final Logger logger = LogManager.getLogger();
	private final boolean IS_DEV = false; // disabling window builder crashing code on true
	
	public enum WINDOW_STATE {
		START,LIST_EDIT,LIST_CHOOSE,TRAINER,TRAINING_SETTINGS
	}

	private JFrame frame;
	private JTable listeditTable;
	private JTextArea vocable_showed;
	private JTable chooseList;
	private JTabbedPane tabbedPane;
	private JMenuBar menuBar;
	private JTextField vocInput;
	private JButton btnResolve;
	private JButton btnShowTip;
	private JButton btnVerify;
	private final String version;
	private WINDOW_STATE TAB;
	private ListPickerData LISTPICKERDATA;
	private MouseAdapter chooseListMouseAdapter = null;
	private Trainer TRAINER;
	private TableChooseModel listChooseModel = new TableChooseModel();
	private TableListModel listEditModel = new TableListModel();
	
	private ListData LISTEDITDATA;
	private JLabel lblListEditor;
	private JButton btnRename;
	private JTextField textEditVocable;
	private JTextField texteditAnswer;
	private JTextField texteditTip;
	private JCheckBox chckbxUseRegex;
	private JButton btnDeleteRow;
	private boolean EDIT_ROW_CHANGE = false;
	private JButton btnRenameTable_CL;
	private JButton btnOkChooseList;
	private JPanel panel_DaySpinner;
	private JButton btnStartTraining;
	private JComboBox<TestMode> comboTrainerMode;
	private JLabel lblCol_a;
	private JLabel lblCol_b;
	private JLabel lblTestMode;
	private WaitLayerUI layer_chooserTable = new WaitLayerUI();
	private WaitLayerUI layer_editTable = new WaitLayerUI();
	private JTable table_1;
	private JSpinner spinnerMaxDays;
	private JRadioButton chckbxRepeatAllVocables;
	private JRadioButton chckbxRefresh;
	private JSpinner spinnerShowXTimes;

	/**
	 * Create the application.
	 */
	public BaseWindow(String version) {
		this.version = version;
		initialize();
		TAB = WINDOW_STATE.START;
		switchTab(TAB);
		frame.pack();
		this.frame.setVisible(true);
		LISTPICKERDATA = new ListPickerData();
		this.LISTEDITDATA = new ListData();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("VocableTrainer - "+version);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				logger.entry();
				exit();
			}
		});
		frame.setBounds(100, 100, 625, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
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
				JOptionPane.showMessageDialog(frame, "VocableTrainer version "+version+"\nCopyright Aron Heinecke 2016", "About", JOptionPane.INFORMATION_MESSAGE, null);
			}
		});
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JCustomTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setFocusable(false);
		tabbedPane.setEnabled(false);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panelStart = new JPanel();
		tabbedPane.addTab("", null, panelStart, null);
		panelStart.setLayout(new MigLayout("", "[grow][grow]", "[47.00,grow,center][grow]"));
		
		btnStartTraining = new JButton("Start Training");
		btnStartTraining.setMaximumSize(new Dimension(194, 46));
		btnStartTraining.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LISTPICKERDATA = new ListPickerData(true, WINDOW_STATE.TRAINING_SETTINGS);
				switchTab(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		panelStart.add(btnStartTraining, "cell 0 0,alignx center,aligny center");
		
		JButton btnEditList = new JButton("Edit List");
		btnEditList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LISTPICKERDATA = new ListPickerData(false,WINDOW_STATE.LIST_EDIT);
				switchTab(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		panelStart.add(btnEditList, "cell 1 0,alignx center,aligny center");
		
		JButton btnNewList = new JButton("New List");
		btnNewList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchTab(WINDOW_STATE.LIST_EDIT);
			}
		});
		panelStart.add(btnNewList, "cell 1 1,alignx center,aligny center");
		
		JPanel panelListEdit = new JPanel();
		tabbedPane.addTab("", null, panelListEdit, null);
		panelListEdit.setLayout(new MigLayout("", "[grow]", "[8.00][74.00,grow][86.00,bottom][bottom]"));
		
		JPanel panel = new JPanel();
		panelListEdit.add(panel, "cell 0 0,grow");
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton btnLoadList = new JButton("Load List");
		btnLoadList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LISTPICKERDATA = new ListPickerData(false,WINDOW_STATE.LIST_EDIT);
				switchTab(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		panel.add(btnLoadList);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);
		
		lblListEditor = new JLabel("List Editor");
		panel.add(lblListEditor);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_1);
		
		btnRename = new JButton("Rename List");
		btnRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String out = (String) JOptionPane.showInputDialog(null, null, "Please choose a name for your list.", JOptionPane.PLAIN_MESSAGE, null, null, LISTEDITDATA.table.getAlias());
				if(out == null){
					return;
				}
				if(out.equals("")){
					return;
				}
				LISTEDITDATA.table.changeName(out, !LISTEDITDATA.isNew);
				actualizeListEditTitle();
			}
		});
		
		JButton btnSetColumns = new JButton("Set Columns");
		btnSetColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ColumnNameDialog(frame,LISTEDITDATA.table);
				actualizeColName();
			}
		});
		panel.add(btnSetColumns);
		panel.add(btnRename);
		
		JScrollPane scrollPane = new JScrollPane();
		if(IS_DEV)
			panelListEdit.add(scrollPane, "cell 0 1,grow");
		else
			panelListEdit.add(new JLayer<JComponent>(scrollPane,layer_editTable), "cell 0 1,grow");
		
		listeditTable = new JTable(listEditModel);
		listeditTable.setFillsViewportHeight(true);
		TableRowSorter<TableListModel> sorter = new TableRowSorter<TableListModel>(
				listEditModel);
		listeditTable.setRowSorter(sorter);
		listeditTable.setSelectionModel(new ForcedListSelectionModel());
		listeditTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		        if(e.getValueIsAdjusting()){
		        	return;
		        }else{
		        	int row = ((ListSelectionModel)e.getSource()).getMinSelectionIndex();
		        	if(row > -1 && row < listEditModel.getSize()){
		        		row = listeditTable.convertRowIndexToModel(row);
						if(listEditModel.getTDLEAt(row) != null){
							LISTEDITDATA.current_element = listEditModel.getTDLEAt(row);
							LISTEDITDATA.current_row = row;
							updateListEditFields();
						}else{
							LISTEDITDATA.current_element = null;
						}
		        	}
		        }
		    }});
		listeditTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mevent) {
				// @formatter:off
				if (mevent.getButton() == MouseEvent.BUTTON1) {
					if(listeditTable.getSelectedRow() == -1){
						listEditModel.add(new TDTableElement("", "", "", new Date(0), 0));
					}
				}
				// @formatter:on
			}
		});
		scrollPane.setViewportView(listeditTable);
		
		JPanel panel_2 = new JPanel();
		panelListEdit.add(panel_2, "cell 0 2,grow");
		panel_2.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		
		lblCol_a = new JLabel("Vocable:");
		panel_2.add(lblCol_a, "cell 0 0,alignx trailing");
		
		textEditVocable = new JTextField();
		textEditVocable.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textEditVocable.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    change();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    change();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    change();
			  }

			  public void change() {
				 if(EDIT_ROW_CHANGE)
						 return;
			     LISTEDITDATA.current_element.setWord_A(textEditVocable.getText());
			     listEditModel.fireTableCellUpdated(LISTEDITDATA.current_row, 0);
			  }
			});
		textEditVocable.setAction(new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		        texteditAnswer.requestFocus();
		    }
		});
		panel_2.add(textEditVocable, "cell 1 0,growx");
		textEditVocable.setColumns(10);
		
		lblCol_b = new JLabel("Answer:");
		panel_2.add(lblCol_b, "cell 0 1,alignx trailing");
		
		texteditAnswer = new JTextField();
		texteditAnswer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		texteditAnswer.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    change();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    change();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    change();
			  }

			  public void change() {
				 if(EDIT_ROW_CHANGE)
						 return;
			     LISTEDITDATA.current_element.setWord_B(texteditAnswer.getText());
			     listEditModel.fireTableCellUpdated(LISTEDITDATA.current_row, 1);
			  }
			});
		texteditAnswer.setAction(new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		        texteditTip.requestFocus();
		    }
		});
		panel_2.add(texteditAnswer, "cell 1 1,growx");
		texteditAnswer.setColumns(10);
		
		JLabel lblTip = new JLabel("Tip:");
		panel_2.add(lblTip, "cell 0 2,alignx trailing");
		
		texteditTip = new JTextField();
		texteditTip.setFont(new Font("Tahoma", Font.PLAIN, 12));
		texteditTip.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    change();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    change();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    change();
			  }

			  public void change() {
				 if(EDIT_ROW_CHANGE)
					 return;
			     LISTEDITDATA.current_element.setTip(texteditTip.getText());
			     listEditModel.fireTableCellUpdated(LISTEDITDATA.current_row, 2);
			  }
			});
		texteditTip.setAction(new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		    	if(LISTEDITDATA.current_row + 1 >= listEditModel.getSize()){
		    		addRow();
		    	}else{
		    		listeditTable.getSelectionModel().setSelectionInterval(LISTEDITDATA.current_row+1, LISTEDITDATA.current_row+1);
		    	}
		    	textEditVocable.requestFocus();
		    }
		});
		panel_2.add(texteditTip, "cell 1 2,growx");
		texteditTip.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panelListEdit.add(panel_3, "cell 0 3,grow");
		panel_3.setLayout(new MigLayout("", "[][][grow,center][]", "[]"));
		
		JButton btnSave = new JButton("Save & Exit");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listSaveAction(listEditModel, listeditTable, LISTEDITDATA.isNew, LISTEDITDATA.table, layer_editTable);
			}
		});
		panel_3.add(btnSave, "cell 0 0");
		
		JButton btnDiscardChanges = new JButton("Discard Changes");
		btnDiscardChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int chosen = JOptionPane.showConfirmDialog(frame, "Do you really want to discard all changes ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if(chosen == 0)
					switchTab(WINDOW_STATE.START);
			}
		});
		panel_3.add(btnDiscardChanges, "cell 1 0");
		
		chckbxUseRegex = new JCheckBox("use RegEx");
		chckbxUseRegex.setEnabled(false);
		chckbxUseRegex.setToolTipText("Use RegEx in this list for answer verification");
		panel_3.add(chckbxUseRegex, "cell 2 0");
		
		btnDeleteRow = new JButton("Delete Row");
		btnDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteRow();
			}
		});
		panel_3.add(btnDeleteRow, "flowx,cell 3 0");
		
		JButton btnInsertRow = new JButton("Append Row");
		btnInsertRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRow();
				textEditVocable.requestFocus();
			}
		});
		panel_3.add(btnInsertRow, "cell 3 0");
		
		JPanel panelChooseList = new JPanel();
		tabbedPane.addTab("", null, panelChooseList, null);
		panelChooseList.setLayout(new MigLayout("", "[grow]", "[168.00,grow][40px,baseline]"));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		if(IS_DEV)
			panelChooseList.add(scrollPane_1, "cell 0 0,grow");
		else
			panelChooseList.add(new JLayer<JComponent>(scrollPane_1,layer_chooserTable), "cell 0 0,grow");
		
		
		chooseList = new JTable(listChooseModel);
		chooseList.setSelectionModel(new ForcedListSelectionModel());
		scrollPane_1.setViewportView(chooseList);
		
		JPanel panel_6 = new JPanel();
		panel_6.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_6.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelChooseList.add(panel_6, "cell 0 1,grow");
		panel_6.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 1));
		
		JPanel panel_4 = new JPanel();
		panel_4.setAlignmentY(0.0f);
		panel_4.setAlignmentX(0.0f);
		panel_6.add(panel_4);
		panel_4.setLayout(new MigLayout("", "[45px][65px][71px][83.00px][]", "[23px]"));
		
		btnOkChooseList = new JButton("Ok");
		panel_4.add(btnOkChooseList, "cell 0 0,alignx leading,aligny top");
		
		JButton btnChooseCancel = new JButton("Cancel");
		panel_4.add(btnChooseCancel, "cell 1 0,alignx left,aligny top");
		
		btnRenameTable_CL = new JButton("Rename");
		panel_4.add(btnRenameTable_CL, "cell 2 0,alignx left,aligny top");
		
		JButton btnDeleteList = new JButton("Delete List");
		btnDeleteList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chooseList.getSelectedRow() > -1){
					int row = chooseList.convertRowIndexToModel(chooseList.getSelectedRow());
					TDTableInfoElement tbl = listChooseModel.getTDLEAt(row);
					int out = JOptionPane.showConfirmDialog(frame, "Do you really want to delete the list \""+tbl.getAlias()+"\" ?", "Delete List", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(out == 0){
						Database.deleteTable(tbl);
						listChooseModel.remove(row);
					}
				}
			}
		});
		panel_4.add(btnDeleteList, "cell 3 0");
		
		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5, "cell 4 0");
		panel_5.setMinimumSize(new Dimension(10, 7));
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setVgap(0);
		
		lblTestMode = new JLabel("Test mode:");
		panel_5.add(lblTestMode);
		
		comboTrainerMode = new JComboBox<TestMode>();
		comboTrainerMode.setModel(new DefaultComboBoxModel<TestMode>(new TestMode[] {TestMode.A_B,TestMode.B_A,TestMode.AB}));
		panel_5.add(comboTrainerMode);
		
		panel_DaySpinner = new JPanel();
		panel_5.add(panel_DaySpinner);
		FlowLayout fl_panel_DaySpinner = (FlowLayout) panel_DaySpinner.getLayout();
		fl_panel_DaySpinner.setVgap(1);
		fl_panel_DaySpinner.setHgap(0);
		
		JLabel lblRedoWordsOldet = new JLabel("Repeat words older than");
		panel_DaySpinner.add(lblRedoWordsOldet);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(4);
		panel_DaySpinner.add(horizontalStrut_1);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		panel_DaySpinner.add(horizontalStrut_2);
		
		JLabel lblDays = new JLabel("days");
		panel_DaySpinner.add(lblDays);
		btnChooseCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LISTPICKERDATA = new ListPickerData();
				switchTab(WINDOW_STATE.START);
			}
		});
		btnOkChooseList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finishChooseList();
			}
		});
		
		JPanel panelStartTraining = new JPanel();
		tabbedPane.addTab("New tab", null, panelStartTraining, null);
		panelStartTraining.setLayout(new MigLayout("", "[grow,leading]", "[][160.00,grow][grow][]"));
		
		JLabel lblStartTrainingInfo = new JLabel("Training Settings");
		panelStartTraining.add(lblStartTrainingInfo, "cell 0 0,alignx center");
		
		JScrollPane scrollPane_4 = new JScrollPane();
		panelStartTraining.add(scrollPane_4, "cell 0 1,grow");
		
		table_1 = new JTable();
		scrollPane_4.setViewportView(table_1);
		
		JPanel panel_7 = new JPanel();
		panelStartTraining.add(panel_7, "cell 0 2,grow");
		panel_7.setLayout(new MigLayout("", "[][][41.00][]", "[][][]"));
		
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
		
		JLabel lblTimes = new JLabel("times");
		panel_7.add(lblTimes, "cell 1 2");
		
		JButton btnStart = new JButton("Start");
		panelStartTraining.add(btnStart, "flowx,cell 0 3");
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelStartTraining.add(horizontalStrut, "cell 0 3");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LISTPICKERDATA.cleanTableModel = false;
				switchTab(WINDOW_STATE.LIST_CHOOSE);
			}
		});
		panelStartTraining.add(btnCancel, "cell 0 3");
		
		JPanel panelTrain = new JPanel();
		tabbedPane.addTab("New tab", null, panelTrain, null);
		panelTrain.setLayout(new MigLayout("", "[grow]", "[][grow][grow][]"));
		
		JLabel lblVocable = new JLabel("Trainer");
		panelTrain.add(lblVocable, "cell 0 0");
		
		JScrollPane scrollPane_3 = new JScrollPane();
		panelTrain.add(scrollPane_3, "cell 0 1,grow");
		
		vocable_showed = new JTextArea();
		vocable_showed.setFont(new Font("Tahoma", Font.PLAIN, 13));
		scrollPane_3.setViewportView(vocable_showed);
		vocable_showed.setForeground(new Color(0, 0, 0));
		vocable_showed.setBackground(UIManager.getColor("EditorPane.background"));
		vocable_showed.setEditable(false);

		JScrollPane scrollPane_2 = new JScrollPane();
		panelTrain.add(scrollPane_2, "cell 0 2,grow");
		
		vocInput = new JTextField();
		vocInput.setFont(new Font("Tahoma", Font.PLAIN, 13));
		vocInput.setBackground(Color.WHITE);
		vocInput.setMargin(new Insets(0, 2, 0, 0));
		vocInput.setCaretColor(Color.BLACK);
		vocInput.setHorizontalAlignment(SwingConstants.LEFT);
		scrollPane_2.setViewportView(vocInput);
		vocInput.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panelTrain.add(panel_1, "cell 0 3,grow");
		panel_1.setLayout(new MigLayout("", "[415px][415px,grow][]", "[23px]"));
		
		btnVerify = new JButton("Verify");
		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(TRAINER.verifySolution(vocInput.getText())){
					resetTrainerForm();
				}else{
					vocInput.setBackground(new Color(255, 150, 115));
				}
			}
		});
		panel_1.add(btnVerify, "flowx,cell 0 0 2 1,alignx left,aligny center");
		
		btnShowTip = new JButton("Show Tip");
		btnShowTip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnShowTip.setEnabled(false);
				vocable_showed.setText(vocable_showed.getText() + "\n"+TRAINER.getTip());
				frame.getRootPane().setDefaultButton(btnVerify);
				vocInput.requestFocus();
			}
		});
		btnShowTip.setToolTipText("Show tip");
		panel_1.add(btnShowTip, "cell 0 0 2 1,alignx left,aligny center");
		
		btnResolve = new JButton("Resolve");
		btnResolve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vocInput.setText(TRAINER.getSolution());
				vocInput.requestFocus();
			}
		});
		btnResolve.setToolTipText("Show solution");
		panel_1.add(btnResolve, "cell 0 0");
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		panel_1.add(horizontalGlue_2, "cell 0 0 2 1,alignx left,aligny top");
		
		JButton btnExitTrainer = new JButton("Exit Training");
		btnExitTrainer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!TRAINER.isFinished()){
					int chosen = JOptionPane.showConfirmDialog(frame, "Do you really want to exit ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
					if(chosen != 0)
						return;
				}
				switchTab(WINDOW_STATE.START);
			}
		});
		btnExitTrainer.setToolTipText("exit current session");
		panel_1.add(btnExitTrainer, "cell 2 0");
	}
	
	/**
	 * Function for switching between tabs
	 * @param state
	 */
	protected void switchTab(WINDOW_STATE state){
		switch(TAB){
		case LIST_CHOOSE:
			break;
		case LIST_EDIT:
			break;
		case START:
			break;
		case TRAINER:
			TRAINER.exit();
			break;
		default:
			break;
		}
		
		switch(state){
		case START:
			showSTARTTab();
			break;
		case LIST_EDIT:
			showLIST_EDITTab();
			break;
		case LIST_CHOOSE:
			showLIST_CHOOSETab();
			break;
		case TRAINER:
			showTRAINERTab();
			break;
		case TRAINING_SETTINGS:
			showTRAINING_SETTINGSTab();
			break;
		default:
			logger.error("Unknown tab state {}",state);
			break;
		}
		TAB = state;
	}
	
	private void showSTARTTab(){
		frame.getRootPane().setDefaultButton(btnStartTraining);
		tabbedPane.setSelectedIndex(0);
	}
	
	/**
	 * Called upon having selected all lists to be used for the training
	 */
	private void showTRAINING_SETTINGSTab(){
		tabbedPane.setSelectedIndex(3);
		chckbxRepeatAllVocables.setSelected(true);
		changeTrainingSettingsSwitch(true);
	}
	
	/**
	 * Function called on list chooser display call
	 */
	private void showLIST_CHOOSETab(){
		if (LISTPICKERDATA.cleanTableModel)
		this.listChooseModel.clearElements();
		
		this.listChooseModel.setMulti_select(LISTPICKERDATA.isMulti_select());
		if(chooseListMouseAdapter != null)
			chooseList.removeMouseListener(chooseListMouseAdapter);
		if(LISTPICKERDATA.isMulti_select()){
			chooseListMouseAdapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mevent) {
					if (mevent.getButton() == MouseEvent.BUTTON1) {
						if(chooseList.getSelectedRow() != -1){
							logger.debug("Got selection");
							if(listChooseModel.getTDLEAt(chooseList.convertRowIndexToModel(chooseList.getSelectedRow())).changePicked()){
								LISTPICKERDATA.amount_chosen++;
							}else{
								LISTPICKERDATA.amount_chosen--;
							}
							listChooseModel.fireTableCellUpdated(chooseList.getSelectedRow(), 0);
							btnOkChooseList.setEnabled(LISTPICKERDATA.amount_chosen > 0);
						}
					}
				}
			};
		}else{
			chooseListMouseAdapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mevent) {
					if (mevent.getButton() == MouseEvent.BUTTON1) {
						if(chooseList.getSelectedRow() != -1){
							logger.debug("Got selection");
							LISTPICKERDATA.setPicked(listChooseModel.getTDLEAt(chooseList.convertRowIndexToModel(chooseList.getSelectedRow())));
							btnOkChooseList.setEnabled(true);
							if(mevent.getClickCount() > 1){
								finishChooseList();
							}
						}
					}
				}
			};
		}
		chooseList.addMouseListener(chooseListMouseAdapter);
		panel_DaySpinner.setVisible(LISTPICKERDATA.isMulti_select());
		comboTrainerMode.setVisible(LISTPICKERDATA.isMulti_select());
		lblTestMode.setVisible(LISTPICKERDATA.isMulti_select());
		frame.getRootPane().setDefaultButton(btnOkChooseList);
		tabbedPane.setSelectedIndex(2);
		this.btnRenameTable_CL.setEnabled(LISTPICKERDATA.amount_chosen > 0);
		this.btnOkChooseList.setEnabled(LISTPICKERDATA.amount_chosen > 0);
		
		if (LISTPICKERDATA.cleanTableModel)
			listChooseLoader(layer_chooserTable, listChooseModel);
		
	}
	
	protected abstract void listChooseLoader(WaitLayerUI layer, TableChooseModel tableModel);
	
	/**
	 * Called on list editor data load
	 * @param listModel tale model to be manipulated
	 * @param table selected db table
	 */
	protected abstract void loadListEditData(WaitLayerUI layer, TableListModel listModel, TDTableInfoElement table);
	
	/**
	 * Function called on list edit display call
	 */
	private void showLIST_EDITTab(){
		this.listEditModel.clearElements();
		tabbedPane.setSelectedIndex(1);
		if(LISTPICKERDATA.getPicked().size() > 0){
			logger.debug("Loading data..");
			LISTEDITDATA.table = LISTPICKERDATA.getPicked().get(0);
			LISTEDITDATA.isNew = false;
			loadListEditData(layer_editTable, listEditModel, LISTEDITDATA.table);
			LISTPICKERDATA = new ListPickerData();
		}else{
			this.LISTEDITDATA = new ListData();
			listEditModel.resetColumnNames();
		}
		actualizeColName();
		actualizeListEditTitle();
		EDIT_ROW_CHANGE = true;
		this.textEditVocable.setText("");
		this.texteditAnswer.setText("");
		this.texteditTip.setText("");
		EDIT_ROW_CHANGE = false;
		this.textEditVocable.setEditable(false);
		this.texteditAnswer.setEditable(false);
		this.texteditTip.setEditable(false);
		this.btnDeleteRow.setEnabled(false);
		if(listEditModel.getRowCount() == 0){
			addRow();
			textEditVocable.requestFocus(false);
		}
	}
	
	/*
	 * Actualize column name in the list editor
	 */
	private void actualizeColName(){
		listEditModel.setColumnNames(LISTEDITDATA.table.getColumn_a(), LISTEDITDATA.table.getColumn_b());
		lblCol_a.setText(LISTEDITDATA.table.getColumn_a());
		lblCol_b.setText(LISTEDITDATA.table.getColumn_b());
	}
	
	/**
	 * Show trainer tab
	 */
	private void showTRAINERTab(){
		TRAINER = new Trainer(LISTPICKERDATA.getPicked(), LISTPICKERDATA.max_days, (TestMode) comboTrainerMode.getSelectedItem());
		LISTPICKERDATA = new ListPickerData();
		resetTrainerForm();
		tabbedPane.setSelectedIndex(3);
		this.vocInput.requestFocus();
	}
	
	/**
	 * Reset trainer form AND show new vocable
	 */
	private void resetTrainerForm(){
		frame.getRootPane().setDefaultButton(this.btnVerify);
		this.vocInput.setText("");
		String vocable = TRAINER.getNewVocable();
		vocInput.setBackground(Color.WHITE);
		this.btnShowTip.setEnabled(TRAINER.hasTip());
		this.vocInput.setEditable(vocable != null);
		this.btnVerify.setEnabled(vocable != null);
		this.btnResolve.setEnabled(vocable != null);
		if(vocable == null){
			this.vocable_showed.setText("FINISHED !\nStats:\n"+TRAINER.getStats());
		}else{
			this.vocable_showed.setText(vocable);
			this.vocInput.requestFocus();
		}
	}
	
	/**
	 * Update list editor fields
	 */
	private void updateListEditFields(){
		this.texteditTip.setText(LISTEDITDATA.current_element.getTip());
		this.textEditVocable.setText(LISTEDITDATA.current_element.getWord_A());
		this.texteditAnswer.setText(LISTEDITDATA.current_element.getWord_B());
		this.textEditVocable.setEditable(true);
		this.texteditAnswer.setEditable(true);
		this.texteditTip.setEditable(true);
		this.btnDeleteRow.setEnabled(true);
	}
	
	/**
	 * Update title of current list
	 */
	private void actualizeListEditTitle(){
		this.lblListEditor.setText("Editor - "+LISTEDITDATA.table.getAlias());
	}
	
	/**
	 * End list selection window, check for sel. > 0
	 */
	private void finishChooseList(){
		if(LISTPICKERDATA.isMulti_select()){
			for(TDTableInfoElement elem : listChooseModel.getRowData()){
				if(elem.isPicked()){
					LISTPICKERDATA.addToPicked(elem);
				}
			}
			LISTPICKERDATA.max_days = (int) spinnerMaxDays.getValue();
		}
		switchTab(LISTPICKERDATA.getNext_tab());
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
	
	/**
	 * Delete row in list editor
	 */
	private void deleteRow(){
		if(this.listeditTable.getSelectedRow() > -1){
			EDIT_ROW_CHANGE = true;
			this.listEditModel.remove(this.listeditTable.getSelectedRow());
			EDIT_ROW_CHANGE = false;
		}
	}
	
	/**
	 * Add row in list editor
	 */
	private void addRow(){
		listEditModel.add(new TDTableElement("", "", "", new Date(0), 0));
		listeditTable.getSelectionModel().setSelectionInterval(listEditModel.getRowCount()-1, listEditModel.getRowCount()-1);
	}
	
	/**
	 * Called on save button press
	 * Has to verify the data & save everything in the db
	 * @param db_table, WaitLayerUI layer 
	 * @param isNewList 
	 * @param table 
	 * @param listModel 
	 */
	protected abstract void listSaveAction(TableListModel listModel, JTable table, boolean isNewList, TDTableInfoElement db_table, WaitLayerUI layer);
	
	/**
	 * Exit and show confirm dialog for some specific tabs
	 */
	private void exit(){
		switch(TAB){
		case LIST_EDIT:
		case TRAINER:
			int chosen = JOptionPane.showConfirmDialog(frame, "Do you really want to exit ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if(chosen != 0)
				return;
			break;
		default:
			break;
		}
		frame.setEnabled(false);
		switchTab(WINDOW_STATE.START);
		System.exit(0);
	}
}
