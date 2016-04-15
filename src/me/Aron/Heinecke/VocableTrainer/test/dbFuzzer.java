/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

/**
 * Fuzzer concurrent inserting a massive amount of data into the db
 * Can be useful in performance tweaks, testing of UI responsiveness etc
 * @author Aron Heinecke
 *
 */
public class dbFuzzer {
	
	private static Logger logger = LogManager.getLogger();
	
	public static void main(String[] args){
		try {
			Database.connect();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processors);
		for (int x = 0; x < 9999999; x++) {
			Runnable worker = new MyRunnable(x);
			executor.execute(worker);
		}
		executor.shutdown();
		logger.info("Fuzzing with {} threads\nBe sure not to run this on productive systems.",processors);
		while (!executor.isTerminated()) {
			 
		}
		
		Database.shutdown();
	}
	
	public static class MyRunnable implements Runnable {
		private final int id;
 
		MyRunnable(int id) {
			this.id = id;
		}
 
		@Override
		public void run() {
			ArrayList<TDTableElement> list = new ArrayList<>();
			for(int i = 0; i < 1; i++){
				list.add(new TDTableElement(i+"a", i+"b", i+"c", new Date(), 0));
			}
			TDTableInfoElement table = new TDTableInfoElement("voc_FUZ_"+id,"FUZZING!``"+id,0,new Date(),"A","B");
			Database.updateVocs(list, table);
		}
	}
}
