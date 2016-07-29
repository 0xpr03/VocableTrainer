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

import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;
import me.Aron.Heinecke.VocableTrainer.store.TrainerSettings;

/**
 * Trainer managing points, updates etc
 * @author Aron Heinecke
 */
public class Trainer {
	private List<TDTableInfoElement> allTables;
	private List<TDTableInfoElement> usableTables;
	private Logger logger = LogManager.getLogger();
	private TDTableElement currentElement;
	private int max_date;
	private static final int DEDUCATION_WRONG_ON_MAX = 2;
	private static final int MIN_POINTS = 0;
	private TestMode testmode;
	private TestMode mode_element;
	private boolean finished = false;
	private int amountFinished = 0;
	private int errorousSolved = 0;
	private int hintsGiven = 0;
	private int questioned = 0;
	private final TrainerSettings settings;
	private int TOTAL_VOCABLES; // total amount of vocables to be solved
	private int showed_finished = 0; // amount of finished vocables showed since last unfinished
	private int amount_show_finished; // amount of finished vocables to be showed
	private TDTableElement lastVocable;
	
	public enum TestMode{
		A_B,B_A,AB
	};
	
	/**
	 * Create a new Trainer object
	 * @param tables list of tables which should be used
	 * @param settings Trainer settings
	 * @param testmode testmode to use
	 */
	public Trainer(List<TDTableInfoElement> tables, TrainerSettings settings, TestMode testmode){
		currentElement = null;
		this.allTables = tables;
		this.usableTables = new ArrayList<>(tables);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -settings.getRefreshOlderThan());
		this.testmode = testmode;
		this.max_date = (int) (cal.getTimeInMillis() / 1000);
		this.settings = settings;
	}
	
	/**
	 * Initialize possible time consuming tasks
	 */
	public void initTimeConsuming(){
		logger.entry();
		{
			DBResult<?> result = Database.resetVocablePoints(allTables);
			if (result.isError){
				Database.showErrorDialog("Error on DB voc amoutn retrieval ", result, "DB Error");
			}
		}
		{
			DBResult<Integer> result = Database.getTrainingVocableAmount(allTables, max_date, settings.getShowAmountTimes());
			if (result.isError){
				Database.showErrorDialog("Error on DB voc amoutn retrieval ", result, "DB Error");
			}else{
				this.TOTAL_VOCABLES = result.value;
			}
		}
		logger.debug("Amount Vocs: {}",TOTAL_VOCABLES);
		logger.exit();
	}
	
	/**
	 * Check wether the current vocable has a tip
	 * @return true if a tip is available
	 */
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
	
	/**
	 * Decrease points once, if maximum reached
	 */
	private void decreasePoints(){
		if(!currentElement.showedHints){
			currentElement.setPoints(currentElement.getPoints() <= MIN_POINTS ? currentElement.getPoints() - DEDUCATION_WRONG_ON_MAX : currentElement.getPoints());
			currentElement.showedHints = true;
		}
	}
	
	/**
	 * Get the solution, points counted
	 * @return
	 */
	public String getSolution(){
		decreasePoints();
		return mode_element == TestMode.A_B ? currentElement.getWord_B() : currentElement.getWord_A();
	}
	
	/**
	 * Get tip, is counted in the points
	 * @return
	 */
	public String getTip(){
		decreasePoints();
		hintsGiven++;
		return currentElement.getTip();
	}
	
	/**
	 * Verify the input and rate outcome
	 * @param input input to verify against the solution
	 * @return true if input matches solution
	 */
	public boolean verifySolution(String input){
		boolean solved = input.equals(mode_element == TestMode.A_B ? currentElement.getWord_B() : currentElement.getWord_A());
		if(!solved){
			errorousSolved++;
			decreasePoints();
		}else{
			if(!currentElement.showedHints && currentElement.getPoints() < settings.getShowAmountTimes()){
				currentElement.setPoints(currentElement.getPoints()+1);
				if(currentElement.getPoints() >= settings.getShowAmountTimes())
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
				+"\nVocables: "+TOTAL_VOCABLES
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
		logger.entry();
		updateElem();
		lastVocable = currentElement;
		currentElement = null;
		
		boolean usedDropinVariable = false;
		while(currentElement == null && !usableTables.isEmpty()){
			logger.debug("Amount remaining: {}",TOTAL_VOCABLES - amountFinished);
			// finished vocable droping, avoiding doubles
			if ((TOTAL_VOCABLES - amountFinished) < 4 && ( amount_show_finished  == -1 || amount_show_finished > showed_finished)) {
				logger.debug("Using finished variable.");
				if (amount_show_finished == -1){
					showed_finished = 0;
					amount_show_finished = getRandom(4);
				}
				TDTableInfoElement selectedDB = allTables.get(getRandom(allTables.size()));
				DBResult<TDTableElement> result = Database.getRandomVocable(selectedDB, max_date, settings.getShowAmountTimes(), true);
				if(result.isError ){
					Database.showErrorDialog("Error on DB retrieval ", result, "DB Error");
					break;
				}else if(result.value == null){ // db is finished
					logger.error("No vocable for reverse search! Table {}",selectedDB.getName());
				}else{
					usedDropinVariable = true;
					currentElement = result.value;
				}
			}else{
				TDTableInfoElement selectedDB = usableTables.get(getRandom(usableTables.size()));
				DBResult<TDTableElement> result = Database.getRandomVocable(selectedDB, max_date, settings.getShowAmountTimes(), false);
				if(result.isError){
					Database.showErrorDialog("Error on DB retrieval ", result, "DB Error");
					break;
				}else if(result.value == null){ // db is finished
					usableTables.remove(selectedDB);
				}else{
					currentElement = result.value;
					amount_show_finished = -1;
				}
			}
			if (lastVocable != null && currentElement != null) {
				if (lastVocable.equals(currentElement)){ // check for repeating voc
					currentElement = null;
					logger.debug("Detected repeating voc!");
					if(amount_show_finished == -1 && TOTAL_VOCABLES - amountFinished == 1){ // prevent double prevention loop
						amount_show_finished = -1;
					}
				}
			}
		}
		
		if (usedDropinVariable) // increment after while, avoiding multiple increments
			showed_finished++;
		
		if(currentElement == null && !usableTables.isEmpty()){
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
	
	/**
	 * Update current element in the db, set date to current one
	 */
	private void updateElem(){
		if(currentElement != null){
			currentElement.setDate(new Date());
			@SuppressWarnings("rawtypes")
			DBResult dbe = Database.updateVocable(currentElement);
			if(dbe.isError){
				Database.showErrorDialog("Error on DB update ", dbe, "DB Error");
			}
		}
	}
	
	/**
	 * Exit function, update last_used dates and current element
	 * Has to be called to avoid progress loss
	 */
	public void exit(){
		updateElem();
		Database.updateTableDates(allTables);
	}
	
	private int getRandom(int max){
		Random rnd = new Random();
		return rnd.nextInt(max);
	}
}
