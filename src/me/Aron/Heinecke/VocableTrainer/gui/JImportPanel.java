package me.Aron.Heinecke.VocableTrainer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableRowSorter;

import org.apache.commons.csv.CSVFormat;

import me.Aron.Heinecke.VocableTrainer.gui.PanelController.WINDOW_STATE;
import me.Aron.Heinecke.VocableTrainer.lib.CButton;
import me.Aron.Heinecke.VocableTrainer.lib.CLabel;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.lib.TableListModel;
import me.Aron.Heinecke.VocableTrainer.lib.Import.Fetcher;
import me.Aron.Heinecke.VocableTrainer.lib.Import.Importer;
import me.Aron.Heinecke.VocableTrainer.lib.Import.Importer.IMPORT_LIST_MODE;
import me.Aron.Heinecke.VocableTrainer.lib.Import.PreviewParser;
import net.miginfocom.swing.MigLayout;

/**
 * Import panel
 * @author Aron Heinecke
 */
public class JImportPanel extends JPanelBase {
	
	public static final CSVFormat FORMAT = CSVFormat.DEFAULT;
	
	private static final long serialVersionUID = -4542395160770274756L;
	private JButton btnStartTraining;
	private JTextField textFieldFile;
	private JTable tablePreview;
	private TableListModel listPreviewModel = new TableListModel();
	private JLabel lblInfo;
	private JButton btnImport;
	private JButton btnCancel;
	private JLabel lblList;
	private JTextField textFieldList;
	private File file = null;
	private JButton btnSelectFile;
	private ArrayList<TDTableElement> previewElements;
	private PreviewParser parser;
	private JLabel lblColumnA;
	private JLabel lblColumnB;
	private JTextField textFieldColumnA;
	private JTextField textFieldColumnB;

	public JImportPanel(PanelController panelcontroller) {
		super(panelcontroller);
		initView();
	}
	
	private void initView(){
		this.setLayout(new MigLayout("", "[grow]", "[47.00,top][][grow][grow][]"));
		
		JLabel lblImport = new CLabel("Import: Please Select the File you want to Import",getMainFont());
		lblImport.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblImport, "cell 0 0,growx,aligny top");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
		
		JLabel lblFile = new CLabel("File",getMainFont());
		panel.add(lblFile, "cell 0 0,alignx trailing");
		
		textFieldFile = new JTextField();
		textFieldFile.setEditable(false);
		panel.add(textFieldFile, "flowx,cell 1 0,growx");
		
		btnSelectFile = new CButton("Select File",getMainFont());
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		panel.add(btnSelectFile, "cell 1 0");
		
		lblList = new CLabel("List",getMainFont());
		panel.add(lblList, "cell 0 1,alignx trailing");
		
		textFieldList = new JTextField();
		textFieldList.setText("My New List");
		panel.add(textFieldList, "flowx,cell 1 1,growx");
		textFieldList.setColumns(10);
		
		lblColumnA = new CLabel("Column A",getMainFont());
		panel.add(lblColumnA, "cell 0 2,alignx trailing");
		
		textFieldColumnA = new JTextField();
		textFieldColumnA.setText("A");
		panel.add(textFieldColumnA, "cell 1 2,growx");
		textFieldColumnA.setColumns(10);
		
		lblColumnB = new CLabel("Column B",getMainFont());
		panel.add(lblColumnB, "cell 0 3,alignx trailing");
		
		textFieldColumnB = new JTextField();
		textFieldColumnB.setText("B");
		panel.add(textFieldColumnB, "cell 1 3,growx");
		textFieldColumnB.setColumns(10);
		
		lblInfo = new CLabel("",getMainFont());
		panel.add(lblInfo, "cell 1 4");
		
		JPanel panel_1 = new JPanel();
		add(panel_1, "cell 0 2,grow");
		panel_1.setLayout(new MigLayout("", "[grow,center]", "[][grow]"));
		
		CLabel lblPreview = new CLabel("Preview",getMainFont());
		panel_1.add(lblPreview, "cell 0 0");
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "cell 0 1,grow");
		
		tablePreview = new JTable();
		tablePreview = new JTable(listPreviewModel);
		tablePreview.setFillsViewportHeight(true);
		TableRowSorter<TableListModel> sorter = new TableRowSorter<TableListModel>(
				listPreviewModel);
		tablePreview.setRowSorter(sorter);
//		tablePreview.setSelectionModel(null);
		tablePreview.setEnabled(false);
		tablePreview.setFont(getEditorFont());
		scrollPane.setViewportView(tablePreview);
		
		JPanel panel_2 = new JPanel();
		add(panel_2, "cell 0 3,grow");
		
		btnCancel = new CButton("Cancel",getMainFont());
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeWindow(WINDOW_STATE.START);
			}
		});
		panel_2.add(btnCancel);
		
		btnImport = new CButton("Import",getMainFont());
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runImport();
			}
		});
		panel_2.add(btnImport);
		
		changeListSelect(false);
		updateImportBtn();
	}
	
	/**
	 * Changes visibility of list selector
	 * @param visible
	 */
	private void changeListSelect(final boolean visible) {
		lblList.setVisible(visible);
		lblColumnA.setVisible(visible);
		lblColumnB.setVisible(visible);
		textFieldList.setVisible(visible);
		textFieldColumnA.setVisible(visible);
		textFieldColumnB.setVisible(visible);
	}
	
	/**
	 * File selector dialog & handler
	 */
	private void selectFile() {
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select Import File");
		if(file != null)
			fc.setSelectedFile(file);
		else
			fc.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			textFieldFile.setText(file.getName());
			previewParse();
		}
	}
	
	/**
	 * Runs import
	 */
	private void runImport() {
		IMPORT_LIST_MODE mode = IMPORT_LIST_MODE.REPLACE;
		if(parser.isRawData()) {
			mode = IMPORT_LIST_MODE.CREATE;
		}
		Importer handler = new Importer(parser, mode,
				new TDTableInfoElement(textFieldList.getText(), textFieldColumnA.getText(), textFieldColumnB.getText()));
		Fetcher fetcher = new Fetcher(file, FORMAT, handler);
		fetcher.run();
		
		changeWindow(WINDOW_STATE.START);
	}
	
	/**
	 * Updated import button
	 */
	private void updateImportBtn() {
		boolean enabled = true;
		if(file == null) {
			enabled = false;
		}else if(parser == null) {
			enabled = false;
		}
		
		btnImport.setEnabled(enabled);
	}
	
	/**
	 * Preview parses the selected file
	 */
	private void previewParse() {
		previewElements = new ArrayList<>(10);
		parser = new PreviewParser(previewElements);
		Fetcher fetcher = new Fetcher(file, FORMAT, parser);
		fetcher.run();
		listPreviewModel.clearElements();
		listPreviewModel.add(previewElements);
		String text;
		if(parser.isMultiList()) {
			text = "Detected Multi-List format, replace mode";
		}else if(parser.isRawData()){
			text = "Detected Raw Data";
		}else {
			text = "Detected single list, replace mode";
		}
		changeListSelect(parser.isRawData());
		lblInfo.setText(text);
		
		updateImportBtn();
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
