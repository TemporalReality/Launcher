package temporalreality.launcher.control;

import javafx.scene.control.MenuItem;
import temporalreality.launcher.model.Version;



/**
 * @author shadowfacts
 */
public class VersionMenuItem extends MenuItem {

	private Version version;

	public VersionMenuItem(Version version) {
		super(version.getVersion());
		this.version = version;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}
}
