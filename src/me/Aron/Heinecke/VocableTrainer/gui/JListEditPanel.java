package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.ColumnNameDialog;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.ForcedListSelectionModel;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableListModel;
import me.Aron.Heinecke.VocableTrainer.lib.WaitLayerUI;
import me.Aron.Heinecke.VocableTrainer.store.ListData;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import net.miginfocom.swing.MigLayout;

/**
 * List editor dialog panel<br>
 * Uses controller's ListPickerData
 * @author Aron Heinecke
 *
 */
@SuppressWarnings("serial")
public class JListEditPanel extends JPanelBase {

	private final Logger logger = LogManager.getLogger();
	
	private JLabel lblListEditor;
	private JButton btnRename;
	private TableListModel listEditModel = new TableListModel();

	private ListData LISTEDITDATA = new ListData();;
	
	private final boolean IS_DEV = false; // disabling window builder crashing code on true

	private JTable listeditTable;

	private boolean EDIT_ROW_CHANGE;

	private JTextField texteditAnswer;

	private JTextField textEditVocable;

	private JLabel lblCol_a;

	private JCheckBox chckbxUseRegex;

	private JButton btnDeleteRow;

	private JLabel lblCol_b;
	
	private WaitLayerUI layer_editTable = new WaitLayerUI();

	private JTextField texteditTip;
	
	/**
	 * Create the panel.
	 */
	public JListEditPanel(PanelController pc) {
		super(pc);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow]", "[8.00][74.00,grow][86.00,bottom][bottom]"));
		
		JPanel panel = new JPanel();
		this.add(panel, "cell 0 0,grow");
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton btnLoadList = new JButton("Load List"); // #TEMP
		btnLoadList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setListPickerData(new ListPickerData(false,WINDOW_STATE.LIST_EDIT));
				changeWindow(WINDOW_STATE.LIST_CHOOSE);
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
				String out = (String) JOptionPane.showInputDialog(getFrame(), null, "Please choose a name for your list.", JOptionPane.PLAIN_MESSAGE, null, null, LISTEDITDATA.table.getAlias());
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
				new ColumnNameDialog(null,LISTEDITDATA.table);
				actualizeColName();
			}
		});
		panel.add(btnSetColumns);
		panel.add(btnRename);
		
		JScrollPane scrollPane = new JScrollPane();
		if(IS_DEV)
			this.add(scrollPane, "cell 0 1,grow");
		else
			this.add(new JLayer<JComponent>(scrollPane,layer_editTable), "cell 0 1,grow");
		
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
		this.add(panel_2, "cell 0 2,grow");
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
		this.add(panel_3, "cell 0 3,grow");
		panel_3.setLayout(new MigLayout("", "[][][grow,center][]", "[]"));
		
		JButton btnSave = new JButton("Save & Exit");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EditListSaveWorker(listEditModel, listeditTable, LISTEDITDATA.isNew, LISTEDITDATA.table, layer_editTable).execute();
			}
		});
		panel_3.add(btnSave, "cell 0 0");
		
		JButton btnDiscardChanges = new JButton("Discard Changes");
		btnDiscardChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int chosen = JOptionPane.showConfirmDialog(getPanel(), "Do you really want to discard all changes ?", "Exit to start", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if(chosen == 0){
					changeWindow(WINDOW_STATE.START);
				}
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
		
		
		
	}

	@Override
	public void resetView() {
		this.listEditModel.clearElements();
		ListPickerData listpickerdata = getListPickerData();
		if(listpickerdata.getPicked().size() > 0){
			logger.debug("Loading data..");
			LISTEDITDATA.table = listpickerdata.getPicked().get(0);
			LISTEDITDATA.isNew = false;
			new EditListLoadWorker(layer_editTable, listEditModel, LISTEDITDATA.table).execute();
			setListPickerData(new ListPickerData());
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

	/*
	 * Actualize column name in the list editor
	 */
	private void actualizeColName(){
		listEditModel.setColumnNames(LISTEDITDATA.table.getColumn_a(), LISTEDITDATA.table.getColumn_b());
		lblCol_a.setText(LISTEDITDATA.table.getColumn_a());
		lblCol_b.setText(LISTEDITDATA.table.getColumn_b());
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean exitRequest() {
		int chosen = JOptionPane.showConfirmDialog(getFrame(), "Do you really want to exit ?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		return (chosen != 0);
	}
	
	/**
	 * Worker performing the save job needed by the list editor
	 * @author Aron Heinecke
	 */
	class EditListSaveWorker extends SwingWorker<Integer, Object> {
		private WaitLayerUI layer;
		private TableListModel model;
		private TDTableInfoElement db_table;
		private boolean isNewList;
		private JTable jTable;
		public EditListSaveWorker(TableListModel listModel, JTable table, boolean isNewList, TDTableInfoElement db_table, WaitLayerUI layer) {
			this.layer = layer;
			this.model = listModel;
			this.db_table = db_table;
			this.isNewList = isNewList;
			this.jTable = table;
			layer.start();
		}

		@Override
		public Integer doInBackground() {
			for(int i = 0; i < model.getSize(); i++){
				TDTableElement elem = model.getTDLEAt(i);
				if((elem.getWord_A().equals("") && !elem.getWord_B().equals("")) || (!elem.getWord_A().equals("") && elem.getWord_B().equals(""))){
					return i;
				}
			}
			Database.updateVocs(model.getRowData(), db_table);
			if(!isNewList){
				Database.updateTableInfo(db_table);
			}
			return -1;
		}

		@Override
		protected void done() {
			try {
				layer.stop();
				if(get() != -1){
					JOptionPane.showMessageDialog(jTable.getParent(), "You have invalid data in the table!\nVocable & Answer have to be set.", "Unable to save", JOptionPane.WARNING_MESSAGE, null);
					jTable.changeSelection(get(), 0, false, false);
				}else{
					changeWindow(WINDOW_STATE.START);
				}
			} catch (Exception ignore) {
			}
		}
	}
	
	/**
	 * Worker performing the load job needed by the list editor
	 * @author Aron Heinecke
	 *
	 */
	class EditListLoadWorker extends SwingWorker<DBResult<List<TDTableElement>>, Object> {
		private WaitLayerUI layer;
		private TableListModel model;
		private TDTableInfoElement db_table;
		public EditListLoadWorker(WaitLayerUI layer, TableListModel tableModel, TDTableInfoElement table) {
			this.layer = layer;
			this.model = tableModel;
			this.db_table = table;
			layer.start();
		}
		
		@Override
		public DBResult<List<TDTableElement>> doInBackground() {
			return Database.getVocs(db_table);
		}

		@Override
		protected void done() {
			try {
				layer.stop();
				if (!get().isError) {
					model.add(get().value);
				} else {
					Database.showErrorDialog("Error trying to load the table ", get(), "DB Error");
				}
			} catch (Exception ignore) {
			}
		}
	}
}
