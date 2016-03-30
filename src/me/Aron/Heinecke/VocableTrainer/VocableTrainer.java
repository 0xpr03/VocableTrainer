/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer;

import java.awt.EventQueue;
import java.sql.SQLException;

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VocableTrainer {
	private final static Logger logger = LogManager.getLogger();
	private final static String version = "0.1";
	public static void main(String[] args){
		logger.info("Starting up VocableTrainer version {}",version);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("Error setting look and feel \n{}",e);
		}
		
		try {
			Database.connect();
			registerExitFunction();
		} catch (SQLException e1) {
			logger.error("Error on db init:\n{}",e1);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new mainWindow(version);
				} catch (Exception e) {
					logger.error("Homescreen thread error \n{}", e);
				}
			}
		});
		
		while (true) {
			try {
				// Make sure that the Java VM don't quit this program.
				Thread.sleep(100);
			} catch (Exception e) {/* ignore */
			}
		}
	}
	
	private static void registerExitFunction() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Exiting");
				Database.shutdown();
			}
		});
	}
}
