package gateway.config;

import static java.lang.String.format;

public final class Config {
	private CellsClientConfig cellsClient;
	private WebClientConfig webClient;
	private BatteryPackConfig batteryPack;

	public CellsClientConfig getCellsClient() {
		return cellsClient;
	}

	public void setCellsClient(CellsClientConfig cellsClient) {
		this.cellsClient = cellsClient;
	}

	public WebClientConfig getWebClient() {
		return webClient;
	}

	public void setWebClient(WebClientConfig webClient) {
		this.webClient = webClient;
	}

	public BatteryPackConfig getBatteryPack() {
		return batteryPack;
	}

	public void setBatteryPack(BatteryPackConfig batteryPackConfig) {
		this.batteryPack = batteryPackConfig;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(format("  cellsClient:\n%s\n", cellsClient));
		s.append(format("  webClient:\n%s\n", webClient));
		s.append(format("  batteryPack:\n%s", batteryPack));
		return s.toString();
	}
}