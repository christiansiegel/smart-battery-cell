package gateway.config;

import static java.lang.String.format;

public final class WebClientConfig {
	private String host;
	private int port;
	private String classPath;
	private int publishInterval;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public int getPublishInterval() {
		return publishInterval;
	}

	public void setPublishInterval(int publishInterval) {
		this.publishInterval = publishInterval;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(format("   classPath: %s\n", classPath));
		s.append(format("   host: %s\n", host));
		s.append(format("   port: %s\n", port));
		s.append(format("   publishInterval: %s ms\n", publishInterval));
		return s.toString();
	}
}
