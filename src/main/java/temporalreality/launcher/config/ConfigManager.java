package temporalreality.launcher.config;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import lombok.Getter;
import net.shadowfacts.shadowlib.util.InternetUtils;
import temporalreality.launcher.util.Issues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author shadowfacts
 */
public class ConfigManager {

	@Getter private static ConfigManager instance = new ConfigManager();

	private File file = new File(System.getProperty("user.home") + "/.temporalreality/launcher-config.json");

	@Getter private Config config;

	private Gson gson;

	private ConfigManager() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public void init() throws IOException {
		String prop = System.getProperty("temporalreality.launcher.config");
		if (prop != null && !prop.isEmpty()) {
			file = new File(prop);
		}

		System.out.println("config file = " + file);


		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		boolean flag = !file.exists();
		if (flag) {
			file.createNewFile();
			InternetUtils.downloadFile("https://gist.githubusercontent.com/shadowfacts/dd388e0dc9afe45e0b1f/raw/9ff472320b433531758a84162331ea933b3d11ac/gistfile1.txt", file);
		}

		config = gson.fromJson(new FileReader(file), Config.class);

		if (flag) {
			config.setJavaPath(System.getProperty("java.home") + "/bin/");
			if (System.getProperty("os.name").startsWith("Win")) {
				config.setJavaPath(config.getJavaPath() + "java.exe");
			} else {
				config.setJavaPath(config.getJavaPath() + "java");
			}
			save();
		}

		if (!config.getPackIndexes().contains("https://raw.githubusercontent.com/TemporalReality/3rd-Party-Modpacks/master/index.txt")) {
			config.getPackIndexes().add("https://raw.githubusercontent.com/TemporalReality/3rd-Party-Modpacks/master/index.txt");
			save();
		}

	}

	public void save() {
		String data = gson.toJson(config);
		try (PrintStream writer = new PrintStream(file)) {
			writer.println(data);
		} catch (FileNotFoundException e) {
			Issues.create(null, e);
		}
	}

	public boolean signedIn() {
		return getConfig().getUsername() != null && !getConfig().getUsername().equals("");
	}

	public static Config getInstanceConfig() {
		return getInstance().getConfig();
	}
}