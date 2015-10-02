package temporalreality.launcher.control;

import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.Setter;
import temporalreality.launcher.model.Version;

/**
 * @author shadowfacts
 */
public class VersionMenuItem extends MenuItem {

	@Getter @Setter private Version version;

	public VersionMenuItem(Version version) {
		super(version.getVersion());
		this.version = version;
	}

}
