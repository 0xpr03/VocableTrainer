/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.store;

import java.util.ArrayList;

import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.BaseWindow.WINDOW_STATE;

/**
 * Data object holding data for the list chooser
 * @author Aron Heinecke
 */
public class ListPickerData {
	/**
	 * Tab to show afterwards
	 */
	WINDOW_STATE next_tab;

	ArrayList<TDTableInfoElement> picked;
	boolean multi_select;
	public int amount_chosen = 0;
	public int max_days = -1;
	public boolean cleanTableModel = true;
	
	/**
	 * Creates a new instance
	 * @param multi_select wether to allow multiple tables to be selected
	 * @param next_tab tab to show on okay
	 */
	public ListPickerData(boolean multi_select, WINDOW_STATE next_tab){
		this.multi_select = multi_select;
		this.next_tab = next_tab;
		picked = new ArrayList<TDTableInfoElement>(multi_select ? 10 : 1);
	}
	
	/**
	 * Default instance pointing to start screen
	 */
	public ListPickerData(){
		this(false, WINDOW_STATE.START);
	}
	
	/**
	 * @return the next_tab
	 */
	public synchronized WINDOW_STATE getNext_tab() {
		return next_tab;
	}

	/**
	 * @return the picked
	 */
	public synchronized ArrayList<TDTableInfoElement> getPicked() {
		return picked;
	}
	
	/**
	 * @param picked the picked to set
	 */
	public synchronized void setPicked(TDTableInfoElement picked) {
		this.picked.clear();
		this.picked.add(picked);
	}
	
	/**
	 * @param picked the picked to set
	 */
	public synchronized void addToPicked(TDTableInfoElement picked) {
		this.picked.add(picked);
	}

	/**
	 * @return the multi_select
	 */
	public synchronized boolean isMulti_select() {
		return multi_select;
	}
}
