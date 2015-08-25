package temporalreality.launcher.control;

import javafx.scene.control.MenuItem;
import temporalreality.launcher.model.Version;



/**
 * @author shadowfacts
 */
public class VersionMenuItem extends MenuItem {

	private Version version;

	public VersionMenuItem(Version version) {
		super(version.version);
		this.version = version;
		setOnAction(event -> System.out.println("Version pressed"));
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}
}
