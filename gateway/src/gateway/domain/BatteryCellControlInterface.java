package gateway.domain;

/**
 * Control interface of a battery cell.
 */
public interface BatteryCellControlInterface {
	/**
	 * Request a balancing mode.
	 * 
	 * @param balancingMode
	 *            Requested balancing mode.
	 * @param balancing
	 *            Requested balancing state if needed for balancing mode
	 *            (on/off)
	 * @see gateway.domain.BalancingMode
	 */
	public void requestBalancing(int balancingMode, boolean balancing);
}
