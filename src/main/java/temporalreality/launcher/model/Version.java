package temporalreality.launcher.model;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
public class Version {

	public String version;
	public String changelogUrl;
	public String mcVersion;
	public String forgeVersion;
	public String overrideUrl;
	public ArrayList<Mod> mods = new ArrayList<>();

	public void addMod(Mod mod) {
		mods.add(mod);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Version) {
			Version other = (Version)obj;
			return version.equals(other.version) &&
					changelogUrl.equals(other.changelogUrl) &&
					mcVersion.equals(other.mcVersion) &&
					forgeVersion.equals(other.forgeVersion) &&
					overrideUrl.equals(other.overrideUrl) &&
					mods.equals(other.mods);
		}
		return false;
	}

	@Override
	public String toString() {
		return version;
	}
}