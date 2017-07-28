package gateway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import gateway.config.Config;
import gateway.config.ObjectManager;

public class BatteryPack implements BatteryPackInterface, BatteryPackAggregationInterface {
	/**
	 * Maps cell IDs to the actual objects.
	 */
	private Map<String, BatteryCell> cells;

	/**
	 * Initialize battery pack.
	 * 
	 * @param objectManager
	 * @param config
	 */
	public synchronized void initialize(ObjectManager objectManager, Config config) {
		cells = new HashMap<String, BatteryCell>();

		java.util.Timer t = new java.util.Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				removeCells(config.getBatteryPack().getCellMemory());
			}
		}, 0, config.getBatteryPack().getCellGarbageCollectorInterval());
	}

	/**
	 * Remove all cells that weren't updated within the timeout.
	 * 
	 * @param timeout
	 *            Timout in milliseconds.
	 */
	private synchronized void removeCells(long timeout) {
		long minTime = new Date().getTime() - timeout;
		for (Iterator<Entry<String, BatteryCell>> it = cells.entrySet().iterator(); it.hasNext();) {
			Entry<String, BatteryCell> entry = it.next();
			if (entry.getValue().getLastUpdate().getTime() < minTime) {
				it.remove();
			}
		}
	}

	@Override
	public double getVoltageMin() {
		double min = Double.POSITIVE_INFINITY;
		for (BatteryCell cell : cells.values()) {
			if (cell.getVoltage() < min) {
				min = cell.getVoltage();
			}
		}
		return min;
	}

	@Override
	public double getVoltageMax() {
		double max = 0;
		for (BatteryCell cell : cells.values()) {
			if (cell.getVoltage() > max) {
				max = cell.getVoltage();
			}
		}
		return max;
	}

	@Override
	public double getVoltageMean() {
		double sum = 0;
		for (BatteryCell cell : cells.values()) {
			sum += cell.getVoltage();
		}
		return sum / cells.size();
	}

	@Override
	public int getNumberOfCells() {
		return cells.size();
	}

	@Override
	public Collection<BatteryCell> getCells() {
		return new ArrayList<BatteryCell>(cells.values());
	}

	@Override
	public BatteryCell getCell(String id) {
		return cells.get(id);
	}

	@Override
	public void updateCell(BatteryCell cell) {
		cells.put(cell.getId(), cell);
	}
}
