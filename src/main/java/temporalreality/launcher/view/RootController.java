package temporalreality.launcher.view;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

/**
 * @author shadowfacts
 */
public class RootController {

	@FXML
	private MenuBar menuBar;

	@FXML
	private void initialize() {
		if (System.getProperty("os.name").startsWith("Mac")) {
			menuBar.setUseSystemMenuBar(true);
		}




//		menuBar.getMenus().add()
	}

}
