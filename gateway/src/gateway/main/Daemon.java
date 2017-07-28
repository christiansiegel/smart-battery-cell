package gateway.main;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;

import gateway.comm.cells.CellsClientInterface;
import gateway.comm.web.WebClientInterface;
import gateway.config.Config;
import gateway.config.ConfigLoader;
import gateway.config.ObjectFactory;
import gateway.config.ObjectManager;

public class Daemon {

	public static void main(String[] args) throws InterruptedException, IOException, MqttException {

		System.out.println("=== Smart Battery Cell Gateway ===");
		System.out.println("Configuration:");
		System.out.println("");
		
		ConfigLoader configLoader = new ConfigLoader("config.yaml");
		Config config = configLoader.load();
		
		ObjectFactory factory = new ObjectFactory(config);
		ObjectManager objectManager = new ObjectManager(factory);

		CellsClientInterface cellsClient = objectManager.getCellsClient();
		WebClientInterface webClient = objectManager.getWebClient();
		
		Thread cellsClientThread = new Thread(cellsClient);
		Thread webClientThread = new Thread(webClient);
		
		cellsClientThread.start();
		webClientThread.start();
		
		System.out.println("");
		System.out.println("Use Ctrl-C to terminate!");
		
		cellsClientThread.join();
		webClientThread.join();
	}
}
