package gateway.comm.cells;

public class CellMessageTypes {
	/**
	 * State update from cell.
	 * @see gateway.comm.cells.StateUpdateMessage
	 */
	public final static byte STATE_UPDATE = 0;
	
	/**
	 * Balancing mode change request to cell.
	 * @see gateway.comm.cells.BalancingControlMessage
	 */
	public final static byte BALANCING_CONTROL = 1;
}
