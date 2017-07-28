package gateway.comm.cells;

/**
 * Request balancing mode change to cell.
 */
public class BalancingControlMessage implements ControlMessageInterface {
	private boolean balancing;
	private int balancingMode;

	@Override
	public byte[] getData() {
		byte[] data = new byte[3];
		data[0] = CellMessageTypes.BALANCING_CONTROL;
		data[1] = (byte) balancingMode;
		data[2] = (byte) (balancing ? 1 : 0);
		return data;
	}

	public boolean isBalancing() {
		return balancing;
	}

	public void setBalancing(boolean balancing) {
		this.balancing = balancing;
	}

	public int getBalancingMode() {
		return balancingMode;
	}

	public void setBalancingMode(int balancingMode) {
		this.balancingMode = balancingMode;
	}

	public String toString() {
		return "BalancingControlMessage [balancing=" + balancing + ", balancingMode=" + balancingMode + "]";
	}
}
