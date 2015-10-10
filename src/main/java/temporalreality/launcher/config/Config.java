package temporalreality.launcher.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author shadowfacts
 */
@Getter
@Setter
public class Config {

	private String javaPath;
	private List<String> jvmArgs;
	private String launcherDir;
	private List<String> packIndexes;
	private String username;
	private int mcWidth;
	private int mcHeight;
	private boolean offline = false;
	private boolean enableAnalytics = true;
}