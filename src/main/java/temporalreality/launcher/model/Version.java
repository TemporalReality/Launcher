package temporalreality.launcher.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
@Getter
@Setter
public class Version {

	private String version;
	private String changelogUrl;
	private String mcVersion;
	private String forgeVersion;
	private String overrideUrl;
	private ArrayList<Mod> mods = new ArrayList<>();

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