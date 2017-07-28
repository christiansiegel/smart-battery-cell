package gateway.config;

import gateway.comm.cells.CellsClientInterface;
import gateway.comm.web.WebClientInterface;
import gateway.domain.BatteryPack;
import gateway.domain.BatteryPackAggregationInterface;
import gateway.domain.BatteryPackInterface;

public class ObjectManager {
	private BatteryPack batteryPack;
	private CellsClientInterface cellsclient;
	private WebClientInterface webClient;

	public ObjectManager(ObjectFactory factory) {
		batteryPack = factory.createBatteryPack(this);
		cellsclient = factory.createCellsClient(this);
		webClient = factory.createWebClient(this);
	}

	public BatteryPackInterface getBatteryPack() {
		return batteryPack;
	}
	
	public BatteryPackAggregationInterface getBatteryPackAggregated() {
		return batteryPack;
	}

	public CellsClientInterface getCellsClient() {
		return cellsclient;
	}

	public WebClientInterface getWebClient() {
		return webClient;
	}
}
