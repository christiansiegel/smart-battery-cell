package gateway.comm.cells;

import gateway.config.Config;
import gateway.config.ObjectManager;

/**
 * Handles communication between gateway and individual cells. 
 */
public interface CellsClientInterface extends Runnable {
	public void initialize(ObjectManager objectManager, Config config);

	/**
	 * Request a balancing mode for a single cell.
	 * 
	 * @param cellId
	 *            Cell ID.
	 * @param balancingMode
	 *            Requested balancing mode.
	 * @param balancing
	 *            Requested balancing state if needed for balancing mode
	 *            (on/off)
	 * @see gateway.domain.BalancingMode
	 */
	public void requestBalancing(String cellId, int balancingMode, boolean balancing);
}
