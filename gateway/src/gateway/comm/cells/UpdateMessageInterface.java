package gateway.comm.cells;

/**
 * State update message from cell.
 */
public interface UpdateMessageInterface {
	public boolean fromData(byte[] data);
}
