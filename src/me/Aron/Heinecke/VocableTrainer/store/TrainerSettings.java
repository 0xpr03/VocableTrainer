package me.Aron.Heinecke.VocableTrainer.store;

/**
 * Storage us
 * @author Aron Heinecke
 *
 */
public class TrainerSettings {
	private boolean repeatAll;
	private int refreshOlderThan;
	private int showAmountTimes;
	public TrainerSettings(boolean repeatAll, int refreshOlderThan, int showAmountTimes) {
		this.repeatAll = repeatAll;
		this.refreshOlderThan = refreshOlderThan;
		this.showAmountTimes = showAmountTimes;
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
	
}
