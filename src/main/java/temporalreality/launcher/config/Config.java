package temporalreality.launcher.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shadowfacts
 */
@Getter
@Setter
public class Config {

	private String javaPath;
	private String[] jvmArgs;
	private String launcherDir;
	private String[] packIndexes;
	private String username;
	private int mcWidth;
	private int mcHeight;
	private boolean offline = false;
	private boolean enableAnalytics = true;
}