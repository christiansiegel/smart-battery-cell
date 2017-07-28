package gateway.domain;

import java.util.Collection;

/**
 * Basic battery pack.
 * 
 * Outdated battery cells shall be removed internally. 
 */
public interface BatteryPackInterface {
	/**
	 * Get all cells in battery pack.
	 * 
	 * @return All cells in battery pack.
	 */
	public Collection<BatteryCell> getCells();

	/**
	 * Get cell with given ID.
	 * 
	 * @param id
	 *            Cell ID.
	 * @return Cell with given ID or null if no such cell exists.
	 */
	public BatteryCell getCell(String id);

	/**
	 * Add or update cell.
	 * 
	 * @param cell
	 *            Cell object.
	 */
	public void updateCell(BatteryCell cell);
}
