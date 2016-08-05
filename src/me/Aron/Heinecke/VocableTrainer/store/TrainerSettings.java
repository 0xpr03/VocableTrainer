package me.Aron.Heinecke.VocableTrainer.store;

import me.Aron.Heinecke.VocableTrainer.Trainer.TestMode;

/**
 * Storage us
 * @author Aron Heinecke
 *
 */
public class TrainerSettings {
	private boolean repeatAll;
	private int refreshOlderThan;
	private int showAmountTimes;
	private TestMode testMode;
	public TrainerSettings(boolean repeatAll, int refreshOlderThan, int showAmountTimes, TestMode testMode) {
		this.repeatAll = repeatAll;
		this.refreshOlderThan = refreshOlderThan;
		this.showAmountTimes = showAmountTimes;
		this.testMode = testMode;
	}
	/**
	 * @return the repeatAll
	 */
	public boolean isRepeatAll() {
		return repeatAll;
	}
	/**
	 * @return the refreshOlderThan
	 */
	public int getRefreshOlderThan() {
		return refreshOlderThan;
	}
	/**
	 * @return the showAmountTimes
	 */
	public int getShowAmountTimes() {
		return showAmountTimes;
	}
	/**
	 * 
	 * @return the TestMode
	 */
	public TestMode getTestMode() {
		return testMode;
	}
}
