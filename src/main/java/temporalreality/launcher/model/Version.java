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

}
