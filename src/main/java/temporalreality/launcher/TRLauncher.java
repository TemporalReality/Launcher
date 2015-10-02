package temporalreality.launcher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.shadowfacts.shadowlib.util.os.OSUtils;
import net.shadowfacts.shadowlib.util.os.OperatingSystem;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.util.Issues;
import temporalreality.launcher.util.MiscUtils;
import temporalreality.launcher.util.ModpackUtils;
import temporalreality.launcher.util.TRAnalytics;
import temporalreality.launcher.view.account.SetUsernameController;
import temporalreality.launcher.view.config.ConfigDialogController;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.modlist.ModListController;
import temporalreality.launcher.view.overview.ModpackOverviewController;
import temporalreality.launcher.view.passworddialog.PasswordDialogController;
import coolsquid.logging.LogManager;
import coolsquid.logging.Logger;
import de.npe.gameanalytics.SimpleAnalytics;

/**
 * @author shadowfacts
 */
public class TRLauncher extends Application {

	public static Logger log;

	@Getter private static TRLauncher launcher;
	@Getter private static TRAnalytics analytics;
	@Getter private static String version;

	@Getter private Stage primaryStage;
	private BorderPane rootLayout;

	@Getter private Map<String, Modpack> modpacks = new HashMap<>();

	@Getter @Setter private Process minecraft;

	public TRLauncher() throws Exception {
		launcher = this;

		ConfigManager.getInstance().init();

		try {
			LogManager.setDefaultContext(LogManager.getContext("Launcher", System.out, new FileOutputStream(MiscUtils.getFile("launcher.log"))));
		} catch (FileNotFoundException e) {
			System.err.println("Could not setup logging library properly.");
			TRLauncher.log.catching(e);
			Issues.create(null, e);
			LogManager.setDefaultContext(LogManager.getContext("Launcher", System.out));
		}
		log = LogManager.getLogger("Launcher");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Issues.create(null, e));
		try {
			Properties properties = new Properties();
			try (InputStream in = TRLauncher.class.getResourceAsStream("MANIFEST.MF")) {
				if (in != null)
					properties.load(in);
				else
					log.warn("Failed to load the jar manifest. Are we running in a development environment?");
			}
			version = properties.getProperty("Implementation-Version", "unknown");

			analytics = new TRAnalytics(new SimpleAnalytics(version, "4b2c71837e92e180fcbc0433b57f3a05", "0e0384bf65d2d6c8887f9c4a9294a1fbed83c363"));
			analytics.sendEvent("OS:" + System.getProperty("os.name"));
			analytics.sendEvent("Arch:" + System.getProperty("os.arch"));
			analytics.sendEvent("JavaVersion:" + System.getProperty("java.version").split("_")[0]);
			analytics.sendEvent("JavaVendor:" + System.getProperty("java.vendor"));
		} catch (Throwable t) {
			log.error("Failed to setup analytics.");
			analytics = new TRAnalytics(null);
		}

		ModpackUtils.loadModpacks(modpacks);

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Temporal Reality");
		MiscUtils.addIcons(this.primaryStage);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (minecraft != null) minecraft.destroyForcibly();
		}));

		if (OperatingSystem.getOS() == OperatingSystem.OSX) { //buuuu //What did you expect? It's OSX
			try {
				OSUtils.setOSXDockIcon(new URL("http://temporal-reality.com/logo/1024.png"));
			} catch (MalformedURLException ignored) {}
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			Issues.create(null, e);
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
				TRLauncher.log.catching(e);
				Issues.create(null, e);
			}

			return controller;
		} catch (IOException e) {
			TRLauncher.log.error("Couldn't find the specified layout");
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
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
			TRLauncher.log.catching(e);
			Issues.create(null, e);
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Throwable t) {
			log.catching(t);
			Issues.create(null, t);
		} finally {
			System.exit(0);
		}
	}
}
