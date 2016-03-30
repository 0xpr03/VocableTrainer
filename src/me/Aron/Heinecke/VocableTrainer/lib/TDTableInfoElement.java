/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.lib;

import java.util.Date;

/**
 * Object storing table related data
 * Including naming conventions, original name and row count, if given
 * @author Aron Heinecke
 */
public class TDTableInfoElement {
	private String name;
	private String alias;
	private long amount_rows;
	private boolean renamed;
	private Date last_used;
	private String column_a;
	private String column_b;
	private boolean picked = false;
	
	/**
	 * New default TDTableInfoElement without a date
	 * @param name
	 * @param alias
	 * @param amount_rows
	 */
	public TDTableInfoElement(String name, String alias, int amount_rows){
		this(name,alias,amount_rows,null,"A","B");
	}
	
	public TDTableInfoElement(String name, String alias, int amount_rows,Date last_used, String column_a, String column_b){
		this.name = name;
		this.alias = alias;
		this.renamed = false;
		this.last_used = last_used;
		this.amount_rows = amount_rows;
		this.column_a = column_a;
		this.column_b = column_b;
	}
	/**
	 * @return the last_used
	 */
	public synchronized Date getLast_used() {
		return last_used;
	}
	/**
	 * @return the picked
	 */
	public synchronized boolean isPicked() {
		return picked;
	}
	/**
	 * @param column_a the column_a to set
	 */
	public synchronized void setColumn_a(String column_a) {
		this.column_a = column_a;
	}

	/**
	 * @param column_b the column_b to set
	 */
	public synchronized void setColumn_b(String column_b) {
		this.column_b = column_b;
	}

	/**
	 * @param picked the picked to set
	 */
	public synchronized void setPicked(boolean picked) {
		this.picked = picked;
	}
	/**
	 * Flip picked boolean
	 * @return new value
	 */
	public synchronized boolean changePicked(){
		this.picked = !this.picked;
		return this.picked;
	}
	/**
	 * @return the column_a
	 */
	public synchronized String getColumn_a() {
		return column_a;
	}
	/**
	 * @return the column_b
	 */
	public synchronized String getColumn_b() {
		return column_b;
	}
	/**
	 * @return the amount_rows
	 */
	public synchronized long getAmount_rows() {
		return amount_rows;
	}
	/**
	 * @param amount_rows the amount_rows to set
	 */
	public synchronized void setAmount_rows(long amount_rows) {
		this.amount_rows = amount_rows;
	}
	/**
	 * @return the name
	 */
	public synchronized String getName() {
		return name;
	}
	/**
	 * @return the alias
	 */
	public synchronized String getAlias() {
		return alias;
	}
	/**
	 * @return the renamed
	 */
	public synchronized boolean isRenamed() {
		return renamed;
	}
	/**
	 * Change name based on the alias
	 * @param name
	 * @param isChange if true the renamed update will be marked as such
	 */
	public void changeName(String alias, boolean isChange){
		this.alias = alias;
		this.renamed = renamed ? true : isChange;
	}
}
