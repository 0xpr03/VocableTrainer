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
 * Table element holding all vocable row relevant data
 * @author Aron Heinecke
 */
public class TDTableElement {
	public TDTableElement(String word_a, String word_b, String tip, Date date, int point, String table) {
		this.word_a = word_a;
		this.word_b = word_b;
		this.Tip = tip;
		this.points = point;
		this.date = date;
		this.table = table;
	}
	public TDTableElement(String word_a, String word_b, String tip, Date date, int points) {
		this(word_a,word_b,tip,date,points,null);
	}
	
	/**
	 * @return the date
	 */
	public synchronized Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public synchronized void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the points
	 */
	public synchronized int getPoints() {
		return points;
	}
	/**
	 * @param points the points to set
	 */
	public synchronized void setPoints(int points) {
		this.points = points;
	}
	/**
	 * @return the table
	 */
	public synchronized String getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public synchronized void setTable(String table) {
		this.table = table;
	}
	private String word_a;
	private String word_b;
	private String Tip;
	private Date date;
	private int points;
	private String table;
	public boolean showedHints;
	/**
	 * @return word_a
	 */
	public synchronized String getWord_A() {
		return word_a;
	}
	/**
	 * @param word_a the word_a to set
	 */
	public synchronized void setWord_A(String word_a) {
		this.word_a = word_a;
	}
	/**
	 * @return the word_b
	 */
	public synchronized String getWord_B() {
		return word_b;
	}
	/**
	 * @param word_b the word_b to set
	 */
	public synchronized void setWord_B(String word_b) {
		this.word_b = word_b;
	}
	/**
	 * @return the tip
	 */
	public synchronized String getTip() {
		return Tip;
	}
	/**
	 * @param tip the tip to set
	 */
	public synchronized void setTip(String tip) {
		Tip = tip;
	}
}
