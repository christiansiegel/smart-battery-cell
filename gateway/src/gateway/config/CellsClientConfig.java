package gateway.config;

import static java.lang.String.format;

public final class CellsClientConfig {
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(format("   port: %s\n", port));
		return s.toString();
	}
}
