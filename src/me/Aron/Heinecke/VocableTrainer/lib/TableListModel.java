/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom TableModel for Patient Treatment JTable
 * @author "Aron Heinecke"
 */
@SuppressWarnings("serial")
public class TableListModel extends AbstractTableModel {
	
	private final Logger logger = LogManager.getLogger();
    protected final static String[] COLUMN_NAMES = {
        "A",
        "B",
        "Tip",
    };
    private List<TDTableElement> rowData;
    
    /**
     * Retrieve a copy of the internal data
	 * @return the rowData
	 */
	public List<TDTableElement> getRowData() {
		return new ArrayList<TDTableElement>(rowData);
	}

	public TableListModel() {
    	rowData = new ArrayList<>(25);
    }
    
    public void add(TDTableElement tdl) {
    	add(Arrays.asList(tdl));
    }
    
    public void remove(int row){
    	logger.debug(row);
    	rowData.remove(row);
    	super.fireTableRowsDeleted(row,row);
    }
    
    public void add(List<TDTableElement> tdl){
    	rowData.addAll(tdl);
    	fireTableDataChanged();
    }
    
    @Override
    public boolean isCellEditable(int row, int col)
    { return false; }
    
    /**
     * Delete all rows
     * @author "Aron Heinecke"
     */
    public void clearElements(){
    	if(rowData.size() != 0){
    		int i = rowData.size()-1;
        	rowData.clear();
        	super.fireTableRowsDeleted(0, i-1);
    	}
    }
    
    public void resetColumnNames(){
    	setColumnNames("A", "B");
    }
    
    /**
     * Set column header names & update table
     * @param col_a
     * @param col_b
     */
    public void setColumnNames(String col_a, String col_b){
    	COLUMN_NAMES[0] = col_a;
    	COLUMN_NAMES[1] = col_b;
    	fireTableStructureChanged();
    }
    
    @Override
    public int getRowCount() {
    	return rowData.size();
    }
    
    @Override
    public int getColumnCount() {
    	return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    public int getSize(){
    	return rowData.size();
    }
    
    public TDTableElement getTDLEAt(int row){
    	if(row >= rowData.size())
    		return null;
    	else
    		return rowData.get(row);
    }
    
    /**
     * Return the values according to the headers, from the TDListElement
     */
	@Override
    public Object getValueAt(int rowIndex, int columnIndex){
    	TDTableElement tdl = getTDLEAt(rowIndex);
    	Object value = null;
    	switch(columnIndex) {
    	case 0:
    		value = tdl.getWord_A();
    		break;
    	case 1:
    		value = tdl.getWord_B();
    		break;
    	case 2:
    		value = tdl.getTip();
    		break;
    	}
    	return value;
    }
    
}
