package temporalreality.launcher;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.util.ModpackUtils;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.view.config.ConfigDialogController;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.login.LoginDialogController;
import temporalreality.launcher.view.overview.ModpackOverviewController;

import java.io.IOException;

/**
 * @author shadowfacts
 */
public class TRLauncher extends Application {

	private static TRLauncher launcher;

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Modpack> modpacks = FXCollections.observableArrayList();

	public TRLauncher() throws Exception {

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ModpackUtils.loadModpacks("https://gist.githubusercontent.com/shadowfacts/89d9b6a33a815a8344ef/raw/8b2c1ff5fce28ed12f37493345c2d200ca42caed/gistfile1.txt", modpacks);
		launcher = this;

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Temporal Reality");

		initRootLayout();
		showModpackOverview();
	}

	public void initRootLayout() {
		System.out.println("Initializing root layout");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/RootLayout.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			System.err.println("Couldn't find the specified layout");
			e.printStackTrace();
		}
	}

	public void showModpackOverview() {
		System.out.println("Showing modpack overview");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/overview/ModpackOverview.fxml"));

			AnchorPane modpackOverview = loader.load();

			rootLayout.setCenter(modpackOverview);

			ModpackOverviewController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);
		} catch (IOException e) {
			System.err.println("Couldn't find the specified layout");
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
			System.err.println("Couldn't find the specified layout");
			e.printStackTrace();
			return null;
		}
	}

	public LoginDialogController showLoginDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(TRLauncher.class.getResource("view/login/LoginDialog.fxml"));

			AnchorPane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Login");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			LoginDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

			return controller;

		} catch (IOException e) {
			System.err.println("Couldn't find the specified layout");
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
			System.err.println("Couldn't find the specified layout");
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
			System.err.println("Couldn't find the specified layout");
			e.printStackTrace();
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
		ConfigManager.getInstance().init();

		launch(args);
	}
}
