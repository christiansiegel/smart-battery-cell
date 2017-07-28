package gateway.domain;

/**
 * Data aggregation features of a battery pack. 
 */
public interface BatteryPackAggregationInterface {
	/**
	 * Get aggregated minimum voltage of all cells in battery pack.
	 * 
	 * @return Minimum cell voltage.
	 */
	public double getVoltageMin();

	/**
	 * Get aggregated maximum voltage of all cells in battery pack.
	 * 
	 * @return Maximum cell voltage.
	 */
	public double getVoltageMax();

	/**
	 * Get aggregated mean voltage of all cells in battery pack.
	 * 
	 * @return Mean cell voltage.
	 */
	public double getVoltageMean();

	/**
	 * Get number of cells in battery pack.
	 * 
	 * @return Number of cells.
	 */
	public int getNumberOfCells();
}
