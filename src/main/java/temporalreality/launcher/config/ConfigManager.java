package temporalreality.launcher.config;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import net.shadowfacts.shadowlib.util.InternetUtils;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.util.Issues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author shadowfacts
 */
public class ConfigManager {

	private static ConfigManager instance = new ConfigManager();

	private File file = new File(System.getProperty("user.home") + "/.temporalreality/launcher-config.json");

	private Config config;

	private Gson gson;

	private ConfigManager() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public void init() throws IOException {
		String prop = System.getProperty("temporalreality.launcher.config");
		if (prop != null && !prop.equals("")) {
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
			config.javaPath = System.getProperty("java.home") + "/bin/";
			if (System.getProperty("os.name").startsWith("Win")) {
				config.javaPath += "java.exe";
			} else {
				config.javaPath += "java";
			}
			save();
		}

	}

	public void save() {
		String data = gson.toJson(config);

		try (PrintStream writer = new PrintStream(file)) {
			writer.println(data);
		} catch (FileNotFoundException e) {
			TRLauncher.log.catching(e);
			Issues.create(null, e, null);
		}
	}

	public boolean signedIn() {
		return getConfig().username != null && !getConfig().username.equals("");
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
