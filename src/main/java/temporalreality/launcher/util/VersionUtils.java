package temporalreality.launcher.util;

import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;

import java.util.Optional;

/**
 * @author shadowfacts
 */
public class VersionUtils {

	public static Version getVersionFromString(Modpack modpack, String version) {
		Optional<Version> theVersion = modpack.getVersions().stream().filter(v -> v.getVersion().equals(version)).findFirst();
		if (theVersion.isPresent()) return theVersion.get();
		else return null;
	}

}
