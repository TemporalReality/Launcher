package temporalreality.launcher.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.config.ConfigManager;


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
					TRLauncher.log.info("Showing about dialog");

					TRLauncher.getLauncher().showAboutDialog();
				});

			} else if (m.getText().equalsIgnoreCase("file")) {
				MenuItem configureItem = new MenuItem("Configure...");
				configureItem.setOnAction(event -> {
					TRLauncher.log.info("Showing config dialog");

					TRLauncher.getLauncher().showConfigDialog();
				});


				m.getItems().add(0, configureItem);
				m.getItems().add(1, new SeparatorMenuItem());

				m.getItems().get(2).setOnAction(event -> {
					TRLauncher.log.info("Goodbye");
					System.exit(0);
				});
			}
		}


		Menu accountsMenu = new Menu("Accounts");
		menuBar.getMenus().add(2, accountsMenu);
		MenuItem account = new MenuItem(ConfigManager.getInstanceConfig().username);

//		CheckMenuItem offline = new CheckMenuItem("Offline");
//		offline.setSelected(ConfigManager.getInstanceConfig().offline);
//		offline.setOnAction(event -> {
//			ConfigManager.getInstanceConfig().offline = offline.isSelected();
//			ConfigManager.getInstance().save();
//		});

		MenuItem set = new MenuItem("Set Account...");
		set.setOnAction(event -> {
			String username = TRLauncher.getLauncher().showSetUsernameDialog();
			if (username != null && !username.equals("")) {
				ConfigManager.getInstanceConfig().username = username;
				ConfigManager.getInstance().save();
				account.setText(username);
			}
		});

		accountsMenu.getItems().addAll(account, new SeparatorMenuItem(), set);

	}

}
