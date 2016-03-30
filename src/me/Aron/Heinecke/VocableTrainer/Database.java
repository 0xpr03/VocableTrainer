/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

/**
 * Global database instance
 * @author Aron Heinecke
 */
public class Database {
	
	private static final Logger logger = LogManager.getLogger();
	private static Connection connection = null;
	
	private static int TEMP_ID = 0;
	
	public static final int MAX_POINTS = 4;
	
	private static final String SQL_TBL_VOC = "CREATE [TEMP] TABLE IF NOT EXISTS `%table%` ( "+
	"`word_a` TEXT NOT NULL, "+
	"`word_b` TEXT NOT NULL, "+
	"`tip` TEXT, "+
	"`points` INTEGER NOT NULL, "+
	"`last_used` INTEGER NOT NULL, "+
	"PRIMARY KEY(word_a,word_b) "+
	")";
	private static final String SQL_TBL_NAMES = "CREATE TABLE IF NOT EXISTS `voc_tables` ( "
	+"`name` TEXT, "
	+"`alias` TEXT, "
	+"`last_used` INTEGER, "
	+"`max_points` INTEGER, " // if this should ever change we can grant backwards compatibility
	+"`col_a` TEXT, "
	+"`col_b` TEXT, "
	+"PRIMARY KEY(name) "
	+");";
	private static final String SQL_DELETE_NONEXISTENT = "DELETE FROM `%table%` "
	+"WHERE NOT EXISTS (SELECT 1 "
    +"FROM `%temp_table%` as tbl2 "
    +"WHERE `%table%`.word_a  = tbl2.word_a " // we can't use an alias as of DELETE syntax
    +"AND `%table%`.word_b = tbl2.word_b);";
	private static final String SQL_UPDATE_VOCS = "INSERT OR REPLACE INTO `%table%` (`word_a`, `word_b`,`tip`,`points`,`last_used`) "
	+"SELECT `word_a`, `word_b`,`tip`,`points`,`last_used` FROM `%temp_tbl%` WHERE 1;";
	private static final String SQL_RESET_CHANGED = "UPDATE `%temp_tbl%` SET `points` = 0, `last_used` = 0 "
	+"WHERE NOT EXISTS (SELECT 1 FROM `%table%` as tbl2 "
	+"WHERE `%temp_tbl%`.word_a = tbl2.word_a AND `%temp_tbl%`.word_b = tbl2.word_b );";
	public static final String prefix_TBL_VOC = "voc_";
	private static final String prefix_TBL_TEMP = "temp_";
	//private static final String SQL_LIST_TBL_VOC = "SELECT `tbl_name` FROM sqlite_master WHERE type='table' AND `tbl_name` LIKE \"%prefix%\";";
	private static final String SQL_GET_ROW_COUNT = "SELECT COUNT(*) as rows FROM `%table%`;";
	private static final String SQL_GET_TBL_VOC = "SELECT `word_a`,`word_b`,`tip`,`last_used`,`points` FROM `%table%` WHERE 1";
	private static final String SQL_UPDATE_VOC = "UPDATE `%table%` SET `points` = ?, `last_used` = ? WHERE `word_a` = ? AND `word_b` = ?;";
	private static final String SQL_GET_VOCABLE_RND = "SELECT `word_a`,`word_b`,`tip`,`last_used`,`points` FROM `%table%` WHERE last_used < ? OR points < ? ORDER BY RANDOM() LIMIT 1;";
	private static final String SQL_INSERT_TEMP_VOC = "INSERT INTO `%table%` (`word_a`,`word_b`,`tip`,`points`,`last_used`) VALUES (?,?,?,?,?) ";
	private static final String SQL_UPDATE_TBL_INFO = "UPDATE `voc_tables` SET `alias` = ?,`col_a` = ?,`col_b` = ? WHERE name = ? ;";
	private static final String SQL_GET_TABLE_ALIAS = "SELECT `alias` FROM `voc_tables` WHERE name = ?";
	private static final String SQL_GET_TABLES = "SELECT `name`,`alias`,`last_used`,`col_a`,`col_b` FROM `voc_tables` WHERE 1";
	private static final String SQL_GET_NEW_NAME = "SELECT COUNT(*) FROM voc_tables;";
	private static final String SQL_INSERT_TBL_NAME = "INSERT INTO `voc_tables` (`name`,`alias`,`last_used`,`max_points`,`col_a`,`col_b`) VALUES (?,?,?,"+MAX_POINTS+",?,?);";
	private static final String SQL_DELETE_TBL = "DROP TABLE `%table%`";
	private static final String SQL_UPDATE_TBL_DATE = "UPDATE `voc_tables` SET `last_used` = ? WHERE `name` = ?";
	private static final String SQL_DELETE_TABLE_INFO = "DELETE FROM `voc_tables` WHERE `name` = ?";
	
	static void connect() throws SQLException{
		String path = System.getProperty("user.home");
		path += "/vocabletrainer";
		new File(path).mkdirs();
		path += "/db.sqlite";
		connection = DriverManager.getConnection("jdbc:sqlite:"+path);
		runInit();
	}
	
	private static void runInit() {
		try {
			Statement stm = connection.createStatement();
			stm.execute(SQL_TBL_NAMES);
			stm.close();
		} catch (SQLException e) {
			logger.error("{}",e);
		}
	}
	
	static DBResult<List<TDTableElement>> getVocs(TDTableInfoElement table){
		try{
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery(SQL_GET_TBL_VOC.replace("%table%", table.getName()));
			List<TDTableElement> data = new ArrayList<TDTableElement>();
			while(rs.next()){
				logger.debug("next");
				data.add(new TDTableElement(rs.getString(1), rs.getString(2), rs.getString(3), getDate(rs.getInt(4)), rs.getInt(5)));
			}
			rs.close();
			stm.close();
			return new DBResult<List<TDTableElement>>(data);
		}catch(Exception e){
			logger.error("{}",e);
			return new DBResult<List<TDTableElement>>(e);
		}
	}
	
	/**
	 * Get table alias, returns null if no entry exists
	 * @param name
	 * @return
	 */
	private static DBResult<String> getTableAlias(String name){
		try{
			PreparedStatement stm = connection.prepareStatement(SQL_GET_TABLE_ALIAS);
			stm.setString(1, name);
			ResultSet rs = stm.executeQuery();
			DBResult<String> dbe;
			if(rs.next()){
				dbe = new DBResult<>(rs.getString(1));
			}else{
				dbe = new DBResult<>();
			}
			rs.close();
			stm.close();
			return dbe;
		} catch (Exception e){
			logger.error("{}",e);
			return new DBResult<String>(e);
		}
	}
	
	private static String getSQL_TBL_VOC(boolean temp,String table){
		if(temp)
			return SQL_TBL_VOC.replace("[TEMP]", "TEMP").replace("%table%", table);
		else
			return SQL_TBL_VOC.replace(" [TEMP] ", " ").replace("%table%", table);
	}
	
	private static Date getDate(long time){
		return new Date(time * 1000);
	}
	
	private static long getDateSec(Date date){
		return date.getTime() / 1000;
	}
	
	/**
	 * Update last_used dates of tables
	 * @param tables
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static DBResult updateTableDates(List<TDTableInfoElement> tables){
		logger.entry();
		try{
			PreparedStatement stm = connection.prepareStatement(SQL_UPDATE_TBL_DATE);
			for(TDTableInfoElement elem : tables){
				stm.setLong(1, getDateSec(elem.getLast_used()));
				stm.setString(2, elem.getName());
				stm.executeUpdate();
			}
			return new DBResult<>();
		} catch(Exception e){
			logger.error("{}",e);
			return new DBResult<>(e);
		}
	}
	
	/**
	 * Updates TDTableElements
	 * @param elem
	 * @return error
	 */
	@SuppressWarnings("rawtypes")
	public static DBResult updateVocable(TDTableElement elem){
		try{
			PreparedStatement stm = connection.prepareStatement(SQL_UPDATE_VOC.replace("%table%", elem.getTable()));
			logger.debug("Points: {}",elem.getPoints());
			stm.setInt(1, elem.getPoints());
			stm.setLong(2, getDateSec(elem.getDate()));
			stm.setString(3, elem.getWord_A());
			stm.setString(4, elem.getWord_B());
			DBResult dbe;
			if(stm.executeUpdate() == 1){
				dbe = new DBResult<>();
			}else{
				dbe = new DBResult<>(new Exception("Updated failed, mismatched row count"));
			}
			stm.close();
			return dbe;
		}catch(Exception e){
			logger.error("{}",e);
			return new DBResult<>(e);
		}
	}
	
	/**
	 * Show a simple error dialog
	 * @param message
	 * @param dbe error showed after the message
	 * @param title
	 */
	public static void showErrorDialog(String message, DBResult<?> dbe, String title){
		JOptionPane.showMessageDialog(null, message+dbe.error.toString(), title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Retrive a new table name
	 * Not thread safe
	 * @return
	 */
	public static DBResult<String> getNewVocTableName(){
		try{
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery(SQL_GET_NEW_NAME);
			DBResult<String> dbe;
			if(rs.next()){
				dbe = new DBResult<String>(prefix_TBL_VOC+rs.getInt(1));
			}else{
				dbe = new DBResult<String>(new Exception("NO value!"));
			}
			rs.close();
			stm.close();
			return dbe;
		}catch(Exception e){
			logger.error("{}",e);
			return new DBResult<String>(e);
		}
	}
	
	private static String getSQL_DELETE_NONEXISTENT(String table, String temp_table){
		return SQL_DELETE_NONEXISTENT.replaceAll("%table%", table).replace("%temp_table%", temp_table);
	}
	
	private static String getTempName(){
		return prefix_TBL_TEMP+(++TEMP_ID);
	}
	
	private static String getSQL_UPDATE_VOC(String table, String temp_table){
		return SQL_UPDATE_VOCS.replace("%table%", table).replace("%temp_tbl%", temp_table);
	}
	
	/**
	 * Update/replace/insert word data
	 * Inserts everything into a temp table & deletes from the original what is not existing
	 * afterwards replaces or insert from the temp table in the original
	 * Create the table if not existent
	 * @param data
	 * @param table
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static DBResult updateVocs(List<TDTableElement> data, TDTableInfoElement table) {
		String temp_tbl = getTempName();
		try {
			{ // insert tbl into voc_table if not existent (new tbl)
				Statement stm =  connection.createStatement();
				if(getTableAlias(table.getName()).value == null){
					stm.execute(getSQL_TBL_VOC(false,table.getName()));
					PreparedStatement stm_2 = connection.prepareStatement(SQL_INSERT_TBL_NAME);
					stm_2.setString(1, table.getName());
					stm_2.setString(2, table.getAlias());
					stm_2.setLong(3, getDateSec(new Date()));
					stm_2.setString(4, table.getColumn_a());
					stm_2.setString(5, table.getColumn_b());
					stm_2.executeUpdate();
					stm_2.close();
				}
				stm.execute(getSQL_TBL_VOC(true,temp_tbl));
				stm.close();
			}
			{ // insert everything into temp db
				String sql = SQL_INSERT_TEMP_VOC.replace("%table%", temp_tbl);
				logger.debug(sql);
				PreparedStatement stm = connection.prepareStatement(sql);
				for(TDTableElement elem : data){
					if(elem.getWord_B().equals("") && elem.getWord_A().equals(""))
						continue;
					stm.setString(1, elem.getWord_A());
					stm.setString(2, elem.getWord_B());
					if(elem.getTip() == null) // is set to null by jdbc, when loaded as null and not changed
						stm.setNull(3, Types.VARCHAR);
					else if(elem.getTip().equals(""))
						stm.setNull(3, Types.VARCHAR);
					else
						stm.setString(3, elem.getTip());
					stm.setInt(4, elem.getPoints());
					stm.setLong(5, getDateSec(elem.getDate()));
					stm.executeUpdate();
				}
				stm.close();
			}
			{ // reset points & dates for new (but changed) entries
				String sql = SQL_RESET_CHANGED.replace("%table%", table.getName()).replaceAll("%temp_tbl%", temp_tbl);
				Statement stm = connection.createStatement();
				stm.execute(sql);
				stm.close();
			}
			{
				Statement stm =  connection.createStatement();
				{ // delete removed ones in origin
				String sql = getSQL_DELETE_NONEXISTENT(table.getName(),temp_tbl);
				logger.debug(sql);
				stm.execute(sql);
				}
				{ // update & insert
					String sql = getSQL_UPDATE_VOC(table.getName(),temp_tbl);
					logger.debug(sql);
					stm.execute(sql);
				}
				stm.close();
			}
		} catch (SQLException e) {
			logger.error("Error on updateVoc {}",e);
			return new DBResult(e);
		} finally {
			try {
				Statement stm = connection.createStatement();
				stm.execute(getSQL_DELETE_TBL(temp_tbl));
				stm.close();
			} catch (SQLException e) {
				logger.error("{}",e);
			}
		}
		return new DBResult();
	}
	
	private static String getSQL_DELETE_TBL(String table){
		return SQL_DELETE_TBL.replace("%table%", table);
	}
	
	/**
	 * Delete table and it's information in voc_tables
	 * @param table
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static DBResult deleteTable(TDTableInfoElement table){
		try{
			{
			Statement stm = connection.createStatement();
			stm.execute(getSQL_DELETE_TBL(table.getName()));
			stm.close();
			}
			{
			PreparedStatement stm = connection.prepareStatement(SQL_DELETE_TABLE_INFO);
			stm.setString(1, table.getName());
			stm.executeUpdate();
			stm.close();
			}
			return new DBResult<>();
		} catch (Exception e){
			logger.error("{}",e);
			return new DBResult<>(e);
		}
	}
	
	public static DBResult<TDTableElement> getRandomVocable(TDTableInfoElement table, int max_date_sec, int max_points){
		try{
			PreparedStatement stm = connection.prepareStatement(SQL_GET_VOCABLE_RND.replace("%table%", table.getName()));
			stm.setInt(1, max_date_sec);
			stm.setInt(2, max_points);
			ResultSet rs = stm.executeQuery();
			DBResult<TDTableElement> dbe;
			if(rs.next()){
				dbe = new DBResult<>(new TDTableElement(rs.getString(1), rs.getString(2), rs.getString(3), getDate(rs.getInt(4)), rs.getInt(5), table.getName()));
			}else{
				dbe = new DBResult<>();
			}
			rs.close();
			stm.close();
			return dbe;
		} catch(Exception e){
			logger.error("{}",e);
			return new DBResult<>(e);
		}
	}
	
	public static DBResult<List<TDTableInfoElement>> getTables(){
		try{
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery(SQL_GET_TABLES);
			List<TDTableInfoElement> data = new ArrayList<TDTableInfoElement>();
			while(rs.next()){
				int size = -1;
				Statement stm_2 = connection.createStatement();
				ResultSet rs_2 = stm_2.executeQuery(SQL_GET_ROW_COUNT.replace("%table%", rs.getString(1)));
				if(rs_2.next()){
					size = rs_2.getInt(1);
				}
				rs_2.close();
				stm_2.close();
				logger.debug("rows: {}",size);
				data.add(new TDTableInfoElement(rs.getString(1), rs.getString(2), size, getDate(rs.getInt(3)), rs.getString(4), rs.getString(5)));
			}
			rs.close();
			stm.close();
			return new DBResult<>(data);
		}catch(Exception e){
			logger.error("{}",e);
			return new DBResult<>(e);
		}
	}
	
	/**
	 * Renames a table, does not create it
	 * @param table
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static DBResult updateTableInfo(TDTableInfoElement table){
		try {
			PreparedStatement stm = connection.prepareStatement(SQL_UPDATE_TBL_INFO);
			stm.setString(1, table.getAlias());
			stm.setString(2, table.getColumn_a());
			stm.setString(3, table.getColumn_b());
			stm.setString(4, table.getName());
			stm.executeUpdate();
			stm.close();
			return new DBResult();
		} catch (SQLException e) {
			logger.error("Error on renameTable {}",e);
			return new DBResult(e);
		}
	}
	
	
	
	
	public static void shutdown(){
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
}
