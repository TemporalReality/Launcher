package temporalreality.launcher.view.about;

import javafx.fxml.FXML;
import net.shadowfacts.shadowlib.util.DesktopUtils;

import java.net.URI;

/**
 * @author shadowfacts
 */
public class AboutDialogController {

	@FXML
	private void temporalRealityPressed() {
		DesktopUtils.openWebpage(URI.create("http://temporal-reality.com"));
	}

	@FXML
	private void jMCLaunchLibPressed() {
		DesktopUtils.openWebpage(URI.create("https://github.com/RX14/jMCLaunchLib"));
	}

	@FXML
	private void gsonPressed() {
		DesktopUtils.openWebpage(URI.create("https://github.com/google/gson"));
	}

	@FXML
	private void groovyPressed() {
		DesktopUtils.openWebpage(URI.create("http://www.groovy-lang.org"));
	}

	@FXML
	private void unirestPressed() {
		DesktopUtils.openWebpage(URI.create("http://unirest.io/java.html"));
	}

	@FXML
	private void shadowlibPressed() {
		DesktopUtils.openWebpage(URI.create("https://github.com/shadowfacts/ShadowLib"));
	}

	@FXML
	private void xzPressed() {
		DesktopUtils.openWebpage(URI.create("http://tukaani.org/xz/java.html"));
	}

	@FXML
	private void commonsIOPressed() {
		DesktopUtils.openWebpage(URI.create("https://github.com/apache/commons-io"));
	}

	@FXML
	private void commonsCodecPressed() {
		DesktopUtils.openWebpage(URI.create("https://github.com/apache/commons-codec"));
	}

}
