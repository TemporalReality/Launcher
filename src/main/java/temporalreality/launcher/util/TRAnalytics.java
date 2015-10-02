package temporalreality.launcher.util;

import lombok.AllArgsConstructor;
import temporalreality.launcher.config.ConfigManager;
import de.npe.gameanalytics.Analytics;

/**
 * @author CoolSquid
 */
@AllArgsConstructor
public class TRAnalytics {

	private final Analytics analytics;

	public void sendEvent(String eventID) {
		if (isEnabled())
			analytics.eventDesign(eventID);
	}

	public boolean isEnabled() {
		return analytics != null && analytics.isActive() && (ConfigManager.getInstanceConfig() == null || ConfigManager.getInstanceConfig().isEnableAnalytics()) && false; //temp disabled, waiting for privacy policy
	}
}