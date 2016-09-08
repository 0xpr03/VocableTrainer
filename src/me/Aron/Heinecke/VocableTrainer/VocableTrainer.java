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

/**
 * Main Class
 * @author Aron Heinecke
 */
public class VocableTrainer {
	private final static Logger logger = LogManager.getLogger();
	private final static String version = "0.1.4";
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
			logger.error("Error on db {} init:\n{}",Database.getDBVersion(),e1);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainWindow(version);
				} catch (Exception e) {
					logger.fatal("Error in the Homescreen \n{}", e);
				}
			}
		});
		
		while (true) {
			try {
				// Make sure that the Java VM doesn't quit this program.
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
