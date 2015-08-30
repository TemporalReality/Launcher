package temporalreality.launcher.util;

import temporalreality.launcher.config.ConfigManager;
import de.npe.gameanalytics.Analytics;
import de.npe.gameanalytics.events.GAErrorEvent.Severity;

/**
 * @author CoolSquid
 */
public class TRAnalytics {

	private final Analytics analytics;

	public TRAnalytics(Analytics analytics) {
		this.analytics = analytics;
	}

	public void sendError(Severity severity, String message) {
		if (isEnabled() && (ConfigManager.getInstanceConfig() == null || ConfigManager.getInstanceConfig().enableErrorReporting))
			analytics.eventErrorNow(severity, message);
	}

	public void sendEvent(String eventID) {
		if (isEnabled() && (ConfigManager.getInstanceConfig() == null || ConfigManager.getInstanceConfig().enableAnalytics))
			analytics.eventDesign(eventID);
	}

	public boolean isEnabled() {
		return analytics != null && analytics.isActive();
	}
}