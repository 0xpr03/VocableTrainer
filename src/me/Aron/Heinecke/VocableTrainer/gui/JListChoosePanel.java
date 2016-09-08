package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.CButton;
import me.Aron.Heinecke.VocableTrainer.lib.CLabel;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.ForcedListSelectionModel;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableChooseModel;
import me.Aron.Heinecke.VocableTrainer.lib.WaitLayerUI;
import me.Aron.Heinecke.VocableTrainer.store.ListPickerData;
import net.miginfocom.swing.MigLayout;

/**
 * List choose dialog panel<br>
 * Uses controller's ListPickerData
 * @author Aron Heinecke
 */
public class JListChoosePanel extends JPanelBase {
	
	private static final long serialVersionUID = 3452428071933099759L;
	private JTable chooseList;
	private CButton btnOkChooseList;
	private CButton btnRenameTable_CL;
	private JLabel lblTestMode;
	private JPanel panel_DaySpinner;
	private WaitLayerUI layer_chooserTable = new WaitLayerUI();
	private TableChooseModel listChooseModel = new TableChooseModel();
	private ListPickerData listpickerdata;
	private MouseAdapter chooseListMouseAdapter = null;
	
	private final boolean IS_DEV = false; // disabling window builder crashing code on true

	public JListChoosePanel(PanelController pc) {
		super(pc);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow]", "[168.00,grow][40px,baseline]"));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		if(IS_DEV)
			this.add(scrollPane_1, "cell 0 0,grow");
		else
			this.add(new JLayer<JComponent>(scrollPane_1,layer_chooserTable), "cell 0 0,grow");
		
		
		chooseList = new JTable(listChooseModel);
		chooseList.setSelectionModel(new ForcedListSelectionModel());
		scrollPane_1.setViewportView(chooseList);
		chooseList.setFont(getEditorFont());
		
		JPanel panel_6 = new JPanel();
		panel_6.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_6.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(panel_6, "cell 0 1,grow");
		panel_6.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 1));
		
		JPanel panel_4 = new JPanel();
		panel_4.setAlignmentY(0.0f);
		panel_4.setAlignmentX(0.0f);
		panel_6.add(panel_4);
		panel_4.setLayout(new MigLayout("", "[45px][65px][71px][83.00px][]", "[23px]"));
		
		btnOkChooseList = new CButton("Ok",getMainFont());
		panel_4.add(btnOkChooseList, "cell 0 0,alignx leading,aligny top");
		
		CButton btnChooseCancel = new CButton("Cancel",getMainFont());
		panel_4.add(btnChooseCancel, "cell 1 0,alignx left,aligny top");
		
		btnRenameTable_CL = new CButton("Rename",getMainFont());
		panel_4.add(btnRenameTable_CL, "cell 2 0,alignx left,aligny top");
		
		CButton btnDeleteList = new CButton("Delete List",getMainFont());
		btnDeleteList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chooseList.getSelectedRow() > -1){
					int row = chooseList.convertRowIndexToModel(chooseList.getSelectedRow());
					TDTableInfoElement tbl = listChooseModel.getTDLEAt(row);
					int out = JOptionPane.showConfirmDialog(getFrame(), "Do you really want to delete the list \""+tbl.getAlias()+"\" ?", "Delete List", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
		
		lblTestMode = new CLabel("Test mode:",getMainFont());
		panel_5.add(lblTestMode);
		
		panel_DaySpinner = new JPanel();
		panel_5.add(panel_DaySpinner);
		FlowLayout fl_panel_DaySpinner = (FlowLayout) panel_DaySpinner.getLayout();
		fl_panel_DaySpinner.setVgap(1);
		fl_panel_DaySpinner.setHgap(0);
		
		JLabel lblRedoWordsOldet = new CLabel("Repeat words older than",getMainFont());
		panel_DaySpinner.add(lblRedoWordsOldet);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(4);
		panel_DaySpinner.add(horizontalStrut_1);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		panel_DaySpinner.add(horizontalStrut_2);
		
		JLabel lblDays = new CLabel("days",getMainFont());
		panel_DaySpinner.add(lblDays);
		btnChooseCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setListPickerData(new ListPickerData());
				changeWindow(WINDOW_STATE.START);
			}
		});
		btnOkChooseList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finishChooseList();
			}
		});
	}

	@Override
	public void resetView() {
		this.listpickerdata = getListPickerData();
		if (listpickerdata.cleanTableModel)
			this.listChooseModel.clearElements();
			
			this.listChooseModel.setMulti_select(listpickerdata.isMulti_select());
			if(chooseListMouseAdapter != null)
				chooseList.removeMouseListener(chooseListMouseAdapter);
			if(listpickerdata.isMulti_select()){
				chooseListMouseAdapter = new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent mevent) {
						if (mevent.getButton() == MouseEvent.BUTTON1) {
							if(chooseList.getSelectedRow() != -1){
								logger.debug("Got selection");
								if(listChooseModel.getTDLEAt(chooseList.convertRowIndexToModel(chooseList.getSelectedRow())).changePicked()){
									listpickerdata.amount_chosen++;
								}else{
									listpickerdata.amount_chosen--;
								}
								listChooseModel.fireTableCellUpdated(chooseList.getSelectedRow(), 0);
								btnOkChooseList.setEnabled(listpickerdata.amount_chosen > 0);
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
								listpickerdata.setPicked(listChooseModel.getTDLEAt(chooseList.convertRowIndexToModel(chooseList.getSelectedRow())));
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
			panel_DaySpinner.setVisible(listpickerdata.isMulti_select());
			lblTestMode.setVisible(listpickerdata.isMulti_select());
			getFrame().getRootPane().setDefaultButton(btnOkChooseList);
			this.btnRenameTable_CL.setEnabled(listpickerdata.amount_chosen > 0);
			this.btnOkChooseList.setEnabled(listpickerdata.amount_chosen > 0);
			
			if (listpickerdata.cleanTableModel)
				new ChooseListWorker(layer_chooserTable, listChooseModel).execute();
	}
	
	/**
	 * End list selection, check for sel. > 0
	 */
	private void finishChooseList(){
		if(listpickerdata.isMulti_select()){
			for(TDTableInfoElement elem : listChooseModel.getRowData()){
				if(elem.isPicked()){
					listpickerdata.addToPicked(elem);
				}
			}
		}
		setListPickerData(listpickerdata);
		changeWindow(listpickerdata.getNext_tab());
	}

	@Override
	public void exit() {
	}

	@Override
	public boolean exitRequest() {
		return false;
	}
	
	/**
	 * Worker performing the list indexing needed by the list picker
	 * @author Aron Heinecke
	 */
	class ChooseListWorker extends SwingWorker<DBResult<List<TDTableInfoElement>>, Object> {
		private WaitLayerUI layer;
		private TableChooseModel model;
		public ChooseListWorker(WaitLayerUI layer, TableChooseModel tableModel) {
			this.layer = layer;
			this.model = tableModel;
			layer.start();
		}

		@Override
		public DBResult<List<TDTableInfoElement>> doInBackground() {
			return Database.getTables();
		}

		@Override
		protected void done() {
			try {
				layer.stop();
				if (!get().isError) {
					model.add(get().value);
				} else {
					Database.showErrorDialog("Error trying to access the tables", get(), "DB Error");
				}
			} catch (Exception ignore) {
			}
		}
	}
}
