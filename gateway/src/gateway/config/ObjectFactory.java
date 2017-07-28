package gateway.config;

import gateway.comm.cells.CellsClientInterface;
import gateway.comm.cells.UdpCellsClient;
import gateway.comm.web.WebClientInterface;
import gateway.domain.BatteryPack;

public class ObjectFactory {
	private Config config;

	public ObjectFactory(Config config) {
		this.config = config;
	}

	public WebClientInterface createWebClient(ObjectManager objectManager) {
		WebClientInterface webClient = null;
		webClient = loadAndInstantiate(config.getWebClient().getClassPath(), webClient);
		webClient.initialize(objectManager, config);
		return webClient;
	}

	public BatteryPack createBatteryPack(ObjectManager objectManager) {
		BatteryPack pack = new BatteryPack();
		pack.initialize(objectManager, config);
		return pack;
	}

	public CellsClientInterface createCellsClient(ObjectManager objectManager) {
		CellsClientInterface cellsClient = new UdpCellsClient();
		cellsClient.initialize(objectManager, config);
		return cellsClient;
	}

	@SuppressWarnings("unchecked")
	public static <T> T loadAndInstantiate(String classPath, T theObject) {
		Class<?> theClass;
		try {
			theClass = Class.forName(classPath);
			theObject = (T) theClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return theObject;
	}
}
