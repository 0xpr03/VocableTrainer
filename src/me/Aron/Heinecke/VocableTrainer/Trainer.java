/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.lib.DBError;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

/**
 * Class storing trainer related data & running DB commands
 * @author Aron Heinecke
 */
public class Trainer {
	private List<TDTableInfoElement> allTables;
	private List<TDTableInfoElement> usableTeables;
	private Logger logger = LogManager.getLogger();
	private TDTableElement currentElement;
	private int max_date;
	private static final int DEDUCATION_WRON_ON_MAX = 2;
	private TestMode testmode;
	private TestMode mode_element;
	private boolean finished = false;
	private int totalVoc;
	private int amountFinished = 0;
	private int errorousSolved = 0;
	private int hintsGiven = 0;
	private int questioned = 0;
	
	public enum TestMode{
		A_B,B_A,AB
	};
	
	public Trainer(List<TDTableInfoElement> tables, int max_days, TestMode testmode){
		currentElement = null;
		this.allTables = tables;
		this.usableTeables = new ArrayList<>(tables);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -max_days);
		this.testmode = testmode;
		this.max_date = (int) (cal.getTimeInMillis() / 1000);
	}
	
	public boolean hasTip(){
		if(currentElement != null)
			return currentElement.getTip() != null;
		else
			return false;
	}
	
	/**
	 * @return the finished
	 */
	public synchronized boolean isFinished() {
		return finished;
	}

	private void decreasePoints(){
		logger.debug("A Points: {}",currentElement.getPoints());
		if(!currentElement.showedHints){
			currentElement.setPoints(currentElement.getPoints() >= Database.MAX_POINTS ? currentElement.getPoints() - DEDUCATION_WRON_ON_MAX : currentElement.getPoints());
			currentElement.showedHints = true;
		}
		logger.debug("B Points: {}",currentElement.getPoints());
	}
	
	public String getSolution(){
		decreasePoints();
		return mode_element == TestMode.A_B ? currentElement.getWord_B() : currentElement.getWord_A();
	}
	
	public String getTip(){
		decreasePoints();
		hintsGiven++;
		return currentElement.getTip();
	}
	
	public boolean verifySolution(String input){
		boolean solved = input.equals(mode_element == TestMode.A_B ? currentElement.getWord_B() : currentElement.getWord_A());
		if(!solved){
			errorousSolved++;
			decreasePoints();
		}else{
			if(!currentElement.showedHints && currentElement.getPoints() < Database.MAX_POINTS){
				currentElement.setPoints(currentElement.getPoints()+1);
				if(currentElement.getPoints() >= Database.MAX_POINTS)
					amountFinished++;
			}
		}
		return solved;
	}
	
	/**
	 * Returns string containing the current statistics of the training session
	 * @return
	 */
	public String getStats(){
		return "Questions total: "+questioned
				+"\nFinished: "+amountFinished
				+"\nGiven hints: "+hintsGiven
				+"\nTimes not solved: "+errorousSolved;
	}
	
	/**
	 * Returns a new TDTableElement vocable
	 * Returns null if there are no more vocables to do
	 * @return
	 */
	public String getNewVocable(){
		updateElem();
		currentElement = null;
		while(currentElement == null && !usableTeables.isEmpty()){
			TDTableInfoElement selectedDB = usableTeables.get(getRandom());
			DBError<TDTableElement> result = Database.getRandomVocable(selectedDB, max_date, Database.MAX_POINTS);
			if(result.isError){
				Database.showErrorDialog("Error on DB retrieval ", result, "DB Error");
				break;
			}else if(result.value == null){ // db is finished
				usableTeables.remove(selectedDB);
			}else{
				currentElement = result.value;
			}
		}
		if(currentElement == null && !usableTeables.isEmpty()){
			return "Error";
		}else{
			questioned++;
			if(currentElement != null){
				if(testmode == TestMode.AB){
					Random rnd = new Random();
					mode_element = rnd.nextDouble() > 0.5 ? TestMode.A_B : TestMode.B_A;
				}else{
					mode_element = testmode;
				}
				switch(mode_element){
				case A_B:
					return currentElement.getWord_A();
				case B_A:
					return currentElement.getWord_B();
				default:
					logger.error("Unknown testing mode!");
					break;
				
				}
				return currentElement.getWord_A();
			}else{
				finished = true;
				return null;
			}
		}
	}
	
	private void updateElem(){
		if(currentElement != null){
			currentElement.setDate(new Date());
			@SuppressWarnings("rawtypes")
			DBError dbe = Database.updateVocable(currentElement);
			if(dbe.isError){
				Database.showErrorDialog("Error on DB update ", dbe, "DB Error");
			}
		}
	}
	
	/**
	 * Exit function, update last_used dates and current element
	 */
	public void exit(){
		updateElem();
		Database.updateTableDates(allTables);
	}
	
	private int getRandom(){
		Random rnd = new Random();
		return rnd.nextInt(usableTeables.size());
	}
}
