package temporalreality.launcher.view;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import temporalreality.launcher.TRLauncher;

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


		for (Menu m : menuBar.getMenus()) {
			if (m.getText().equalsIgnoreCase("help")) {

				m.getItems().get(0).setOnAction(event -> {
					System.out.println("Showing about dialog");

					TRLauncher.getLauncher().showAboutDialog();
				});

			} else if (m.getText().equalsIgnoreCase("file")) {
				MenuItem configureItem = new MenuItem("Configure");
				configureItem.setOnAction(event -> {
					System.out.println("Showing config dialog");

					TRLauncher.getLauncher().showConfigDialog();
				});

				m.getItems().add(0, configureItem);
				m.getItems().add(1, new SeparatorMenuItem());

				m.getItems().get(2).setOnAction(event -> {
					System.out.println("Goodbye");
					System.exit(0);
				});
			}
		}
	}

}
