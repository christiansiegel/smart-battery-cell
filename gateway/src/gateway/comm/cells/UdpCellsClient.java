package gateway.comm.cells;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import gateway.config.Config;
import gateway.config.ObjectManager;
import gateway.domain.BatteryCell;
import gateway.domain.BatteryPackInterface;

public class UdpCellsClient implements CellsClientInterface {

	private ObjectManager objectManager;
	private DatagramSocket socket;
	private Map<String, InetAddress> cellAddresses;
	private Config config;

	public UdpCellsClient() {
		cellAddresses = new HashMap<String, InetAddress>();
	}

	public void initialize(ObjectManager objectManager, Config config) {
		this.config = config;
		this.objectManager = objectManager;
		try {
			socket = new DatagramSocket(config.getCellsClient().getPort());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void handleMessage(String cellId, byte[] msgData) {
		UpdateMessageInterface msg = null;
		BatteryPackInterface pack = objectManager.getBatteryPack();

		byte messageType = msgData[0];
		if (messageType == CellMessageTypes.STATE_UPDATE) {
			msg = new StateUpdateMessage();
			if (!msg.fromData(msgData)) {
				return;
			}
			BatteryCell cell = pack.getCell(cellId);
			if (cell == null) {
				cell = new BatteryCell(cellId, objectManager);
			}
			cell.setVoltage(((StateUpdateMessage) msg).getVoltage());
			cell.setBalancing(((StateUpdateMessage) msg).isBalancing());
			cell.setBalancingMode(((StateUpdateMessage) msg).getBalancingMode());
			pack.updateCell(cell);
		}

		if (msg != null) {
			System.out.println(cellId + " << " + msg.toString());
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] buf = new byte[2048];

				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet); // blocking

				if (packet.getData().length < 1) {
					continue;
				}

				String cellId = Integer.toString(packet.getAddress().getAddress()[3] & 0xFF);
				cellAddresses.put(cellId, packet.getAddress());
				byte[] msgData = packet.getData();

				handleMessage(cellId, msgData);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void requestBalancing(String cellId, int balancingMode, boolean balancing) {
		InetAddress address = cellAddresses.get(cellId);
		if (address == null)
			return;
		int port = config.getCellsClient().getPort();

		BalancingControlMessage message = new BalancingControlMessage();
		message.setBalancing(balancing);
		message.setBalancingMode(balancingMode);

		System.out.println(message);

		byte[] sendData = message.getData();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
