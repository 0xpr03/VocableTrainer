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
 * Custom TableModel for the list choose table
 * @author "Aron Heinecke"
 */
@SuppressWarnings("serial")
public class TableChooseModel extends AbstractTableModel {
	
	private final Logger logger = LogManager.getLogger();
	private boolean multi_select = false;
    /**
     * Update multi select
	 * @param multi_select the multi_select to set
	 */
	public synchronized void setMulti_select(boolean multi_select) {
		this.multi_select = multi_select;
		super.fireTableDataChanged();
	}

	protected static final String[] COLUMN_NAMES = {
    	"Use",
        "Name",
        "Used",
        "Rows",
        "Col A",
        "Col B"
    };
    private List<TDTableInfoElement> rowData;
    
    /**
     * Retrieve a copy of the internal data
	 * @return the rowData
	 */
	public List<TDTableInfoElement> getRowData() {
		return new ArrayList<TDTableInfoElement>(rowData);
	}

	public TableChooseModel() {
    	rowData = new ArrayList<TDTableInfoElement>(25);
    }
    
    public void add(TDTableInfoElement tdl) {
    	add(Arrays.asList(tdl));
    }
    
    public void remove(int row){
    	logger.debug(row);
    	rowData.remove(row);
    	super.fireTableRowsDeleted(row,row);
    }
    
    public void add(List<TDTableInfoElement> tdl){
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
        	super.fireTableRowsDeleted(0, i);
    	}
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
    
    public TDTableInfoElement getTDLEAt(int row){
    	if(row >= rowData.size())
    		return null;
    	else
    		return rowData.get(row);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 && multi_select)
            return Boolean.class;
        return super.getColumnClass(columnIndex);
    }
    
    /**
     * Return the values according to the headers, from the TDTableInfoElement
     */
	@Override
    public Object getValueAt(int rowIndex, int columnIndex){
		TDTableInfoElement tdl = getTDLEAt(rowIndex);
    	Object value = null;
		switch(columnIndex) {
    	case 0:
			if(multi_select)
    			value = tdl.isPicked();
			else
				value = null;
    		break;
    	case 1:
    		value = tdl.getAlias();
    		break;
    	case 2:
    		value = tdl.getLast_used();
    		break;
    	case 3:
    		value = tdl.getAmount_rows();
    		break;
    	case 4:
    		value = tdl.getColumn_a();
    		break;
    	case 5:
    		value = tdl.getColumn_b();
    		break;
    	}
    	return value;
    }
}
