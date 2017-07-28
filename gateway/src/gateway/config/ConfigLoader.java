package gateway.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {
	private String path;
	private Yaml yaml = new Yaml();

	public ConfigLoader(String path) {
		this.path = path;
	}

	public Config load() throws IOException {
		InputStream in = Files.newInputStream(Paths.get(path));
		Config config = yaml.loadAs(in, Config.class);
		System.out.println(config.toString());
		return config;
	}
}
