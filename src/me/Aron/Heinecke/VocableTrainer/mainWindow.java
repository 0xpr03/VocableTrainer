package me.Aron.Heinecke.VocableTrainer;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import me.Aron.Heinecke.VocableTrainer.BaseWindow.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableChooseModel;
import me.Aron.Heinecke.VocableTrainer.lib.TableListModel;
import me.Aron.Heinecke.VocableTrainer.lib.WaitLayerUI;

/**
 * Main Window, extending BaseWindow to segregate some code chunks
 * @author Aron Heinecke
 *
 */
final class MainWindow extends BaseWindow {

	public MainWindow(String version) {
		super(version);
		
	}
	
	@Override
	protected void listChooseLoader(WaitLayerUI layer, TableChooseModel tableModel){
		new ChooseListWorker(layer, tableModel).execute();
	}
	
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

	@Override
	protected void loadListEditData(WaitLayerUI layer, TableListModel listModel, TDTableInfoElement table) {
		new EditListLoadWorker(layer, listModel, table).execute();
	}

	@Override
	protected void listSaveAction(TableListModel listModel, JTable table, boolean isNewList,TDTableInfoElement db_table, WaitLayerUI layer) {
		new EditListSaveWorker(listModel, table, isNewList, db_table, layer).execute();
	}
	
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
					switchTab(WINDOW_STATE.START);
				}
			} catch (Exception ignore) {
			}
		}
	}
}
