package me.Aron.Heinecke.VocableTrainer.gui;

import static me.Aron.Heinecke.VocableTrainer.lib.CSVHeaders.CSV_METADATA_COMMENT;
import static me.Aron.Heinecke.VocableTrainer.lib.CSVHeaders.CSV_METADATA_START;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.csv.CSVPrinter;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.gui.JListChoosePanel.ChooseListWorker;
import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.CButton;
import me.Aron.Heinecke.VocableTrainer.lib.CLabel;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableChooseModel;
import me.Aron.Heinecke.VocableTrainer.lib.WaitLayerUI;
import net.miginfocom.swing.MigLayout;

/**
 * Export panel
 * 
 * @author Aron Heinecke
 */
public class JExportPanel extends JPanelBase {

	public static final boolean EXPORT_METADATA = true;
	public static final boolean EXPORT_MULTIPLE = true;
	
	private static final long serialVersionUID = -4542395160770274756L;
	private JTextField textFieldFile;
	private JTable tableLists;
	private TableChooseModel listChooseModel = new TableChooseModel();
	private WaitLayerUI layer_chooserTable = new WaitLayerUI();
	private JButton btnExport;
	private JButton btnCancel;
	private File file = null;
	private JButton btnSelectFile;
	private MouseAdapter chooseListMouseAdapter = null;
	private int amountSelectedLists = 0;

	private final boolean IS_DEV = false;

	public JExportPanel(PanelController panelcontroller) {
		super(panelcontroller);
		initView();
	}

	private void initView() {
		this.setLayout(new MigLayout("", "[grow]", "[47.00,top][][grow][grow][]"));

		JLabel lblImport = new CLabel("Import: Please Select the File you want to Import", getMainFont());
		lblImport.setHorizontalAlignment(SwingConstants.CENTER);
		lblImport.setText("Export please select the destination path & lists to export.");
		add(lblImport, "cell 0 0,growx,aligny top");

		JPanel panel = new JPanel();
		add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[][grow]", "[]"));

		JLabel lblFile = new CLabel("File", getMainFont());
		lblFile.setText("Export File");
		panel.add(lblFile, "cell 0 0,alignx trailing");

		textFieldFile = new JTextField();
		textFieldFile.setEditable(false);
		panel.add(textFieldFile, "flowx,cell 1 0,growx");

		btnSelectFile = new CButton("Select File", getMainFont());
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		panel.add(btnSelectFile, "cell 1 0");

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new MigLayout("", "[grow,center]", "[][grow]"));

		CLabel lblPreview = new CLabel("Preview", getMainFont());
		lblPreview.setText("Selected");
		panel_1.add(lblPreview, "cell 0 0");

		JScrollPane scrollPane = new JScrollPane();
		tableLists = new JTable(listChooseModel);
		tableLists.setFillsViewportHeight(true);
		tableLists.setFont(getEditorFont());
		scrollPane.setViewportView(tableLists);

		if (IS_DEV)
			panel_1.add(scrollPane, "cell 0 1,grow");
		else
			panel_1.add(new JLayer<JComponent>(scrollPane, layer_chooserTable), "cell 0 1,grow");

		add(panel_1, "cell 0 2,grow");

		JPanel panel_2 = new JPanel();
		add(panel_2, "cell 0 3,grow");

		btnCancel = new CButton("Cancel", getMainFont());
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeWindow(WINDOW_STATE.START);
			}
		});
		panel_2.add(btnCancel);

		btnExport = new CButton("Import", getMainFont());
		btnExport.setText("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				export();
				changeWindow(WINDOW_STATE.START);
			}
		});
		panel_2.add(btnExport);

		changeListSelect(false);
		updateExportBtn();
	}

	/**
	 * Changes visibility of list selector
	 * 
	 * @param visible
	 */
	private void changeListSelect(final boolean visible) {
	}

	/**
	 * File selector dialog & handler
	 */
	private void selectFile() {
		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select Export File");
		if (file != null)
			fc.setSelectedFile(file);
		else
			fc.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
		// In response to a button click:
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().exists()) {
				int result = JOptionPane.showConfirmDialog(this,
						"Do you want to overwrite the file " + fc.getSelectedFile().getAbsolutePath() + " ?",
						"File overwriting", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == 1) {
					return;
				}
			}
			file = fc.getSelectedFile();
			textFieldFile.setText(file.getName());
			updateExportBtn();
		}
	}
	
	/**
	 * Export task
	 */
	private void export() {
		try (FileWriter fw = new FileWriter(file);
                //TODO: enforce UTF-8
                BufferedWriter writer = new BufferedWriter(fw);
                CSVPrinter printer = new CSVPrinter(writer, JImportPanel.FORMAT)
           ) {
               int i = 0;
               for (TDTableInfoElement tbl : listChooseModel.getRowData()) {
            	   if(!tbl.isPicked())
            		   continue;
            	   
                   logger.debug("exporting tbl " + tbl.toString());
                   if (EXPORT_METADATA) {
                       printer.printRecord(CSV_METADATA_START);
                       printer.printComment(CSV_METADATA_COMMENT);
                       printer.print(tbl.getName());
                       printer.print(tbl.getColumn_a());
                       printer.print(tbl.getColumn_b());
                       printer.println();
                   }
                   List<TDTableElement> vocables = Database.getVocs(tbl).value;

                   for (TDTableElement ent : vocables) {
                       printer.print(ent.getWord_A());
                       printer.print(ent.getWord_B());
                       printer.print(ent.getTip());
                       printer.println();
                   }
                   i++;
               }
               logger.debug("closing all");
               printer.close();
               writer.close();
               fw.close();
           } catch (Exception e) {
        	   logger.error("{}",e);
           }
	}

	/**
	 * Updated import button
	 */
	private void updateExportBtn() {
		btnExport.setEnabled(amountSelectedLists > 0 && file != null);
	}

	@Override
	public void resetView() {
		this.listChooseModel.clearElements();
		amountSelectedLists = 0;

		this.listChooseModel.setMulti_select(true);
		if (chooseListMouseAdapter == null) {
			chooseListMouseAdapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mevent) {
					if (mevent.getButton() == MouseEvent.BUTTON1) {
						if (tableLists.getSelectedRow() != -1) {
							logger.debug("Got selection");
							if (listChooseModel
									.getTDLEAt(tableLists.convertRowIndexToModel(tableLists.getSelectedRow()))
									.changePicked()) {
								amountSelectedLists++;
							} else {
								amountSelectedLists--;
							}
							listChooseModel.fireTableCellUpdated(tableLists.getSelectedRow(), 0);
							updateExportBtn();
						}
					}
				}
			};
			tableLists.addMouseListener(chooseListMouseAdapter);
		}
		new ChooseListWorker(layer_chooserTable, listChooseModel).execute();
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
