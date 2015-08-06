package temporalreality.launcher;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.shadowfacts.shadowlib.util.OperatingSystem;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.util.MiscUtils;
import temporalreality.launcher.util.ModpackUtils;
import temporalreality.launcher.view.account.SetUsernameController;
import temporalreality.launcher.view.config.ConfigDialogController;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.modlist.ModListController;
import temporalreality.launcher.view.overview.ModpackOverviewController;
import coolsquid.logging.LogManager;
import coolsquid.logging.Logger;
import temporalreality.launcher.view.passworddialog.PasswordDialogController;

/**
 * @author shadowfacts
 */
public class TRLauncher extends Application {

	public static Logger log;

	private static TRLauncher launcher;

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Modpack> modpacks = FXCollections.observableArrayList();

	public TRLauncher() throws Exception {
		launcher = this;

		ConfigManager.getInstance().init();

		try {
			LogManager.setDefaultContext(LogManager.getContext("Launcher", System.out, new FileOutputStream(MiscUtils.getFile("launcher.log"))));
		} catch (FileNotFoundException e) {
			System.err.println("Could not setup logging library properly.");
			e.printStackTrace();
			LogManager.setDefaultContext(LogManager.getContext("Launcher", System.out));
		}
		log = LogManager.getLogger("Launcher");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ModpackUtils.loadModpacks(modpacks);

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Temporal Reality");
		this.primaryStage.getIcons().add(new Image("http://i.imgur.com/L9zXP7u.png"));

		System.out.println("OperatingSystem.getOS() = " + OperatingSystem.getOS());
		System.out.println("System.getProperty(\"os.name\") = " + System.getProperty("os.name"));
		if (OperatingSystem.getOS() == OperatingSystem.OSX) {
			com.apple.eawt.Application osxApplication = com.apple.eawt.Application.getApplication();
			osxApplication.setDockIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://i.imgur.com/L9zXP7u.png")));
		}

		initRootLayout();
		showModpackOverview();
	}

	public void initRootLayout() {
		TRLauncher.log.info("Initializing root layout");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/RootLayout.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public void showModpackOverview() {
		TRLauncher.log.info("Showing modpack overview");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/overview/ModpackOverview.fxml"));

			AnchorPane modpackOverview = loader.load();

			rootLayout.setCenter(modpackOverview);

			ModpackOverviewController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public DownloadDialogController showDownloadDialog(Modpack modpack) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/downloaddialog/DownloadDialog.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Downloading Modpack");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			DownloadDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setModpack(modpack);

			controller.setDownloadingLabelText("Downloading " + modpack.getDisplayName());

			dialogStage.show();

			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return controller;
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
			return null;
		}
	}

	public PasswordDialogController showPasswordDialog(String username) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/passworddialog/PasswordDialog.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Login");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			PasswordDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUsername(username);

			dialogStage.showAndWait();

			return controller;
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
			return null;
		}
	}

	public void showConfigDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/config/ConfigDialog.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Config");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			ConfigDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public void showAboutDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/about/AboutDialog.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("About");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			dialogStage.showAndWait();
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public void showModListDialog(ArrayList<Mod> mods) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/modlist/ModList.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Mod List");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			ModListController controller = loader.getController();
			controller.setMods(FXCollections.observableArrayList(mods));

			dialogStage.showAndWait();
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public String showSetUsernameDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/account/SetUsername.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add Account");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			SetUsernameController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

			return controller.getUsername();

		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			e.printStackTrace();
			return null;
		}
	}

	public static TRLauncher getLauncher() {
		return launcher;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public ObservableList<Modpack> getModpacks() {
		return modpacks;
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}
}