/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.store;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

/**
 * Data storage to stripe out variables and allow for default initialization
 * @author Aron Heinecke
 */
public class ListData {
	public TDTableInfoElement table;
	public boolean isNew;
	public TDTableElement current_element = null;
	public int current_row;
	
	public ListData(){
		this.table = new TDTableInfoElement((String) Database.getNewVocTableName().value, "Untitled List", -1);
		this.isNew = true;
		this.current_row = -1;
	}
}
