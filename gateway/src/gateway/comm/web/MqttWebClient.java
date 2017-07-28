package gateway.comm.web;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import gateway.config.Config;
import gateway.config.ObjectManager;
import gateway.domain.BalancingMode;
import gateway.domain.BatteryCell;
import gateway.domain.BatteryPackAggregationInterface;
import gateway.domain.BatteryPackInterface;;

public class MqttWebClient implements WebClientInterface, MqttCallback {
	private ObjectManager objectManager;
	private MqttClient mqttClient;
	private String mainTopic = "smartbatterycells/";
	private int qos = 1;
	private Config config;
	private MqttConnectOptions connOpts;

	public void initialize(ObjectManager objectManager, Config config) {
		this.objectManager = objectManager;
		this.config = config;

		String broker = "tcp://" + config.getWebClient().getHost() + ":" + config.getWebClient().getPort();
		String clientId = "BatteryPackMqttPublisher";

		MemoryPersistence persistence = new MemoryPersistence();
		try {
			mqttClient = new MqttClient(broker, clientId, persistence);
			connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			mqttClient.setCallback(this);
			mqttClient.connect(connOpts);
			mqttClient.subscribe(mainTopic + "+/balancingCommand");
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private <T> void publish(String topic, T message) {
		try {
			MqttMessage msg = new MqttMessage(String.valueOf(message).getBytes());
			msg.setQos(qos);
			mqttClient.publish(topic, msg);
		} catch (MqttException me) {
			me.printStackTrace();
		}
	}

	private void publishCellStates(BatteryPackInterface pack) {
		Collection<BatteryCell> cells = pack.getCells();
		for (BatteryCell cell : cells) {
			publish(mainTopic + cell.getId() + "/voltage", cell.getVoltage());
			publish(mainTopic + cell.getId() + "/balancing", cell.isBalancing());
			publish(mainTopic + cell.getId() + "/balancingMode",
					cell.getBlancingMode() == BalancingMode.PASSIVE_AUTO ? "AUTO" : "MANUAL");
		}
	}

	private void publishAggregatedCellStates(BatteryPackAggregationInterface pack) {
		publish(mainTopic + "min/voltage", pack.getVoltageMin());
		publish(mainTopic + "max/voltage", pack.getVoltageMax());
		publish(mainTopic + "mean/voltage", pack.getVoltageMean());
	}

	@Override
	public void run() {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				publishCellStates(objectManager.getBatteryPack());
				publishAggregatedCellStates(objectManager.getBatteryPackAggregated());
			}
		}, 0, config.getWebClient().getPublishInterval());
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			mqttClient.connect(connOpts);
			mqttClient.subscribe(mainTopic + "+/balancingCommand");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String[] topics = topic.split("/");
		String command = topics[topics.length - 1];
		String cellId = topics[topics.length - 2];

		if (command.equals("balancingCommand")) {
			BatteryCell cell = objectManager.getBatteryPack().getCell(cellId);
			if (cell != null) {
				boolean balancing = new String(message.getPayload()).contains("ON");
				int balancingMode = new String(message.getPayload()).contains("AUTO") ? BalancingMode.PASSIVE_AUTO
						: BalancingMode.PASSIVE_MANUAL;

				cell.requestBalancing(balancingMode, balancing);
			}
		}
	}
}
