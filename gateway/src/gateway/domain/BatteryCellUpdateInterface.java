package gateway.domain;

/**
 * Update interface of a battery cell.
 * 
 * This must only be used for actual cell values received from the physical
 * smart battery cell.
 */
public interface BatteryCellUpdateInterface {
	/**
	 * Update actual cell voltage.
	 * 
	 * @param voltage
	 *            Actual cell voltage.
	 */
	public void setVoltage(double voltage);

	/**
	 * Update actual cell balancing state (on/off).
	 * 
	 * @param voltage
	 *            Actual cell balancing state (on/off).
	 */
	public void setBalancing(boolean balancing);

	/**
	 * Update actual cell balancing mode.
	 * 
	 * @param voltage
	 *            Actual cell balancing mode.
	 * @see gateway.domain.BalancingMode
	 */
	public void setBalancingMode(int balancingMode);
}
