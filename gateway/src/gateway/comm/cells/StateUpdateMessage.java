package gateway.comm.cells;

/**
 * State update message from cell.
 */
public class StateUpdateMessage implements UpdateMessageInterface {
	private float voltage = 0;
	private boolean balancing = false;
	private int balancingMode = 0;
	
	@Override
	public boolean fromData(byte[] data) {
		if (data[0] != CellMessageTypes.STATE_UPDATE || data.length < 5) {
			return false;
		} else {
			voltage = (float) ((data[1] << 8 | data[2] & 0xff) / 1000.0);
			balancing = data[3] == 1;
			balancingMode = data[4];
			return true;
		}
	}

	public float getVoltage() {
		return voltage;
	}

	public void setVoltage(float voltage) {
		this.voltage = voltage;
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
		return "StateUpdateMessage [voltage=" + voltage + ", balancing=" + balancing + ", balancingMode=" + balancingMode + "]";
	}
}
