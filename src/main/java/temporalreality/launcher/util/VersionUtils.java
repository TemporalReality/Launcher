package temporalreality.launcher.util;

import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;

/**
 * @author shadowfacts
 */
public class VersionUtils {

	public static Version getVersionFromString(Modpack modpack, String version) {
		for (Version v : modpack.getVersions()) {
			if (v.version.equals(version)) {
				return v;
			}
		}
		return null;
	}

}
