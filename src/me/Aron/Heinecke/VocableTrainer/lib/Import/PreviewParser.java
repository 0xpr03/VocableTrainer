package me.Aron.Heinecke.VocableTrainer.lib.Import;

import java.util.List;

import me.Aron.Heinecke.VocableTrainer.lib.TDTableElement;
import me.Aron.Heinecke.VocableTrainer.lib.TDTableInfoElement;

/**
 * Adapted from https://github.com/0xpr03/VocableTrainer-Android
 * 
 * Preview parser handler also counting parsing metadata stats<br>
 * Limiting amount of entries parsed per list
 */
public class PreviewParser implements ImportHandler {
    private final static int PARSE_LIMIT = 5;
    private final List<TDTableElement> list;
    private final TDTableInfoElement tbl = null;
    private int parsed_limiter = 0;
    private int tblCount = 0;
    private int rows = 0;

    public PreviewParser(List<TDTableElement> list) {
        this.list = list;
    }

    @Override
    public void newTable(String name, String columnA, String columnB) {
    	list.add(new TDTableElement(columnA, columnB, name, null, 0));
        parsed_limiter = 0;
        tblCount++;
        rows++;
    }

    @Override
    public void newEntry(String A, String B, String Tipp) {
        rows++;
        if (parsed_limiter < PARSE_LIMIT) {
        	list.add(new TDTableElement(A, B, Tipp, null, 0));
            parsed_limiter++;
        }
    }

    /**
     * Returns amount of rows detected
     *
     * @return
     */
    public int getAmountRows() {
        return rows;
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {

    }

    /**
     * Is parsed list raw data without list metadata
     *
     * @return
     */
    public boolean isRawData() {
        return tblCount == 0;
    }

    /**
     * Is parsed list a multilist
     *
     * @return
     */
    public boolean isMultiList() {
        return tblCount > 1;
    }
}
