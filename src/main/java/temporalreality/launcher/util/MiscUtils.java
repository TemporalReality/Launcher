package temporalreality.launcher.util;

import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.view.login.LoginDialogController;

import java.io.File;

/**
 * @author shadowfacts
 */
public class MiscUtils {

	public static String getPath(String s) {
//		return ConfigManager.getInstanceConfig().launcherDir + "/" + s;
		String launcherDir = ConfigManager.getInstanceConfig().launcherDir;
		if (!launcherDir.equals("")) {
			return launcherDir + "/" + s;
		} else {
			return s;
		}
	}

	public static File getFile(String s) {
		return new File(getPath(s));
	}

}
