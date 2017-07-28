package gateway.config;

import static java.lang.String.format;

public final class BatteryPackConfig {
	/**
	 * Cells that weren't updated within this time are removed from memory [ms].
	 */
	private int cellMemory;
	
	/**
	 * Interval the garbage collector removes outdated cells.
	 */
	private int cellGarbageCollectorInterval;

	public int getCellMemory() {
		return cellMemory;
	}

	public void setCellMemory(int cellMemory) {
		this.cellMemory = cellMemory;
	}
	
	public int getCellGarbageCollectorInterval() {
		return cellGarbageCollectorInterval;
	}

	public void setCellGarbageCollectorInterval(int cellGarbageCollectorInterval) {
		this.cellGarbageCollectorInterval = cellGarbageCollectorInterval;
	}
	
	@Override
    public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(format("   cellMemory: %s ms\n", cellMemory));
		s.append(format("   cellGarbageCollectorInterval: %s ms\n", cellGarbageCollectorInterval));
        return s.toString();
    }
}

