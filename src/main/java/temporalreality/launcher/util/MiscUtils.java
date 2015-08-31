package temporalreality.launcher.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import temporalreality.launcher.config.ConfigManager;

/**
 * @author shadowfacts
 */
public class MiscUtils {

	private static String launcherDir = null;

	public static String getPath(String s) {
		if (launcherDir == null) {
			String prop = System.getProperty("temporalreality.launcher.dir");
			if (prop != null && !prop.equals("")) {
				launcherDir = prop;
			} else {
				launcherDir = ConfigManager.getInstanceConfig().launcherDir;
			}
		}

		if (!launcherDir.equals("")) {
			return launcherDir + "/" + s;
		} else {
			return s;
		}
	}

	public static File getFile(String s) {
		return new File(getPath(s));
	}

	public static void addIcons(Stage stage) {
		stage.getIcons().addAll(
				new Image("http://temporal-reality.com/logo/1024.png"),
				new Image("http://temporal-reality.com/logo/512.png"),
				new Image("http://temporal-reality.com/logo/256.png"),
				new Image("http://temporal-reality.com/logo/128.png"),
				new Image("http://temporal-reality.com/logo/64.png")
				);
	}

	public static String toString(Throwable t) {
		StringWriter w = new StringWriter();
		t.printStackTrace(new PrintWriter(w));
		return w.toString();
	}
}