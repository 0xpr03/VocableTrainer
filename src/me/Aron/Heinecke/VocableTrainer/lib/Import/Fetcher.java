package me.Aron.Heinecke.VocableTrainer.lib.Import;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static me.Aron.Heinecke.VocableTrainer.lib.CSVHeaders.CSV_METADATA_START;

/**
 * Adapted from Adapted from https://github.com/0xpr03/VocableTrainer-Android
 * 
 * Fetcher for CSV files
 */
public class Fetcher {

	private Logger logger = LogManager.getLogger();
	private final File source;
	private final CSVFormat format;
	private final ImportHandler handler;
	private final static int RECORD_SIZE = 3;
    private final static int MIN_RECORD_SIZE = RECORD_SIZE - 1;
    private final static int REC_V1 = 0;
    private final static int REC_V2 = 1;
    private final static int REC_V3 = 2;

	public Fetcher(File source, CSVFormat format, ImportHandler handler) {
		this.source = source;
		this.format = format;
		this.handler = handler;
	}

	public void run() {
		handler.start();
		logger.entry();
		try (FileReader reader = new FileReader(source);
				BufferedReader bufferedReader = new BufferedReader(reader);
				CSVParser parser = new CSVParser(bufferedReader, format)) {
			boolean tbl_start = false;
            final String empty_v3 = "";
            int i = 1;
			for (CSVRecord record : parser) {
				if (record.size() < MIN_RECORD_SIZE) {
                    logger.info("ignoring entry, missing values: " + record.toString());
                    continue;
                } else if (record.size() > RECORD_SIZE) {
                	logger.info("entry longer then necessary: " + record.toString());
                }
                String v1 = record.get(REC_V1);
                String v2 = record.get(REC_V2);
                String v3 = record.size() < RECORD_SIZE ? empty_v3 : record.get(REC_V3);
                if (tbl_start) {
                    handler.newTable(v1, v2, v3);
                    tbl_start = false;
                } else if (tbl_start = v1.equals(CSV_METADATA_START[0]) && v2.equals(CSV_METADATA_START[1]) && v3.equals(CSV_METADATA_START[2])) {
                    //do nothing
                } else {
                    handler.newEntry(v1, v2, v3);
                }
                i++;

			}
			parser.close();
			bufferedReader.close();
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			handler.finish();
		}
	}
}
