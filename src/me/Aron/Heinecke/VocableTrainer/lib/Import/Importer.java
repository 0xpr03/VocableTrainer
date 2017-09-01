package me.Aron.Heinecke.VocableTrainer.lib.Import;

import me.Aron.Heinecke.VocableTrainer.Database;
import me.Aron.Heinecke.VocableTrainer.lib.DBResult;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Adapted from https://github.com/0xpr03/VocableTrainer-Android
 * 
 * Importer, does actual importing
 */
public class Importer implements ImportHandler {

	private static final int BUFFER_CAPACITY = 100;

	private Logger logger = LogManager.getLogger();

	private PreviewParser previewParser;
	private IMPORT_LIST_MODE mode;
	private TDTableInfoElement overrideTable;
	private TDTableInfoElement currentTable;
	private ArrayList<TDTableElement> insertBuffer = new ArrayList<>(BUFFER_CAPACITY);
	private boolean ignoreEntries;

	public Importer(PreviewParser previewParser, IMPORT_LIST_MODE mode, TDTableInfoElement overrideTable) {
		if (previewParser.isRawData() && overrideTable == null) {
			logger.warn("RawData without passed table!");
			throw new IllegalArgumentException("Missing table!");
		}
		this.previewParser = previewParser;
		this.mode = mode;
		this.overrideTable = overrideTable;
		ignoreEntries = false;
	}

	@Override
	public void start() {
		// raw data or single list with create flag
		if (previewParser.isRawData() || (!previewParser.isMultiList() && mode == IMPORT_LIST_MODE.CREATE)) {
			currentTable = overrideTable;
		}
	}

	@Override
	public void newTable(String name, String columnA, String columnB) {
		flushBuffer();
		ignoreEntries = false;
		TDTableInfoElement tbl;
		if (previewParser.isMultiList() || mode != IMPORT_LIST_MODE.CREATE) {
			DBResult<String> res = Database.getTableName(name, columnA, columnB);
			if (res.hasValue()) {
				logger.debug("table found");
				tbl = new TDTableInfoElement(res.value, name, 0, null, columnA, columnB);
				if (mode == IMPORT_LIST_MODE.IGNORE) {
					ignoreEntries = true;
				}
			} else {
				logger.debug("creating new table");
				tbl = new TDTableInfoElement(name, columnA, columnB);
			}

			currentTable = tbl;
		}
	}

	@Override
	public void newEntry(String A, String B, String Tipp) {
		if (!ignoreEntries) {
			insertBuffer.add(new TDTableElement(A, B, Tipp, null, 0, currentTable.getName()));
		}
	}

	/**
	 * Flushes the buffer and inserts everything
	 */
	private void flushBuffer() {
		if(insertBuffer.isEmpty()) {
			return;
		}
		Database.updateVocs(insertBuffer, currentTable);
		insertBuffer.clear();
		insertBuffer.ensureCapacity(BUFFER_CAPACITY);
	}

	@Override
	public void finish() {
		flushBuffer();
	}

	/**
	 * Import list handling mode
	 */
	public enum IMPORT_LIST_MODE {
		/**
		 * Replace existing list's vocables
		 */
		REPLACE,
		/**
		 * Add to existing lists
		 */
		ADD,
		/**
		 * Ignore existing lists
		 */
		IGNORE,
		/**
		 * Create new list
		 */
		CREATE
	}
}
