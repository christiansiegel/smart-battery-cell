package gateway.domain;

import java.util.Date;

import gateway.config.ObjectManager;

public class BatteryCell implements BatteryCellInterface, BatteryCellMonitorInterface, BatteryCellUpdateInterface,
		BatteryCellControlInterface {

	private String id;
	private double voltage;
	private boolean balancing;
	private int balancingMode;
	private Date lastUpdate;
	private ObjectManager objectManager;

	public BatteryCell(String id, ObjectManager objectManager) {
		this.id = id;
		this.objectManager = objectManager;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void requestBalancing(int balancingMode, boolean balancing) {
		objectManager.getCellsClient().requestBalancing(id, balancingMode, balancing);
	}

	@Override
	public synchronized void setVoltage(double voltage) {
		this.voltage = voltage;
		this.lastUpdate = new Date();
	}

	@Override
	public synchronized void setBalancing(boolean balancing) {
		this.balancing = balancing;
		this.lastUpdate = new Date();
	}

	@Override
	public synchronized void setBalancingMode(int balancingMode) {
		this.balancingMode = balancingMode;
		this.lastUpdate = new Date();
	}

	@Override
	public synchronized double getVoltage() {
		return voltage;
	}

	@Override
	public synchronized boolean isBalancing() {
		return balancing;
	}

	@Override
	public synchronized int getBlancingMode() {
		return balancingMode;
	}

	@Override
	public synchronized Date getLastUpdate() {
		return lastUpdate;
	}
}
