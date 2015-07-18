package temporalreality.launcher.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.shadowfacts.shadowlib.util.InternetUtils;

import java.io.*;

/**
 * @author shadowfacts
 */
public class ConfigManager {

	private static ConfigManager instance = new ConfigManager();

	private File file = new File(System.getProperty("user.home") + "/.temporalreality/launcher-config.json");

	private Config config;

	private Gson gson;

	private ConfigManager() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Config.class, new ConfigSerializer());
		builder.registerTypeAdapter(Config.class, new ConfigDeserializer());
		builder.setPrettyPrinting();
		gson = builder.create();
	}

	public void init() throws IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
			InternetUtils.downloadFile("https://gist.githubusercontent.com/shadowfacts/dc87dd58fdcd317ebd1d/raw/da5dccc30341c37ae8cb1ddf284da86df899abc0/0.1.0.json", file);
		}

		config = gson.fromJson(new FileReader(file), Config.class);
	}

	public void save() {
		String data = gson.toJson(config);

		try {
			PrintWriter writer = new PrintWriter(file);
			writer.println(data);
			writer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Config file could not be found");
			e.printStackTrace();
		}
	}

	public Config getConfig() {
		return config;
	}


	public static ConfigManager getInstance() {
		return instance;
	}

	public static Config getInstanceConfig() {
		return getInstance().getConfig();
	}

}
