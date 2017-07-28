package gateway.domain;

import java.util.Date;

/**
 * Monitoring interface of a battery cell.
 */
public interface BatteryCellMonitorInterface {
	/**
	 * Get cell voltage.
	 * 
	 * @return Cell voltage in V.
	 */
	public double getVoltage();

	/**
	 * Get balancing state (on/off).
	 * 
	 * @return Balancing state (on/off).
	 */
	public boolean isBalancing();

	/**
	 * Get balancing mode.
	 * 
	 * @see gateway.domain.BalancingMode
	 * @return Balancing mode.
	 */
	public int getBlancingMode();

	/**
	 * Get time of last property update.
	 * 
	 * @return time of last property update.
	 */
	public Date getLastUpdate();
}
