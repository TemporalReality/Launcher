package temporalreality.launcher.view.overview;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.control.VersionMenuItem;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;
import temporalreality.launcher.util.ModpackUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author shadowfacts
 */
public class ModpackOverviewController {

	private Stage primaryStage;

	@FXML
	private TableView<Modpack> modpackTable;

	@FXML
	private TableColumn<Modpack, String> nameColumn;

	@FXML
	private ImageView image;

	@FXML
	private Label name;

	@FXML
	private Label authorsLabel;

	@FXML
	private Label authors;

	@FXML
	private Label mcLabel;

	@FXML
	private Label mcVer;

	@FXML
	private Label forgeLabel;

	@FXML
	private Label forgeVer;

	@FXML
	private Text description;

	@FXML
	private Button download;

	@FXML
	private MenuButton version;

	@FXML
	private Button launch;

	@FXML
	private Button delete;

	@FXML
	private Button mods;


	public ModpackOverviewController() {

	}

	@FXML
	private void initialize() {
		modpackTable.setItems(TRLauncher.getLauncher().getModpacks());

		nameColumn.setCellValueFactory(cellData ->
			cellData.getValue().isBeta() ? cellData.getValue().getNameProperty().concat(" (BETA)") : cellData.getValue().getNameProperty()
		);

		showModpackDetails(null);

		modpackTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
			showModpackDetails(newValue)
		));
	}

	@FXML
	private void downloadPressed() {
		try {
			Modpack active = modpackTable.getSelectionModel().getSelectedItem();
			ModpackUtils.download(active, () -> updateButtons(active), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void launchPressed() {
		ModpackUtils.launch(modpackTable.getSelectionModel().getSelectedItem(), false);
	}

	@FXML
	private void deletePressed() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Delete Modpack");
		alert.setHeaderText("Are you sure you want to delete the modpack " + modpackTable.getSelectionModel().getSelectedItem().getDisplayName() + "?");
		alert.setContentText("Forever is a very long time...");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			Modpack active = modpackTable.getSelectionModel().getSelectedItem();
			ModpackUtils.delete(active, () -> updateButtons(active));
		}
	}

	@FXML
	private void versionSelected() {
//		updateButtons(modpackTable.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void modListPressed() {
		TRLauncher.getLauncher().showModListDialog(modpackTable.getSelectionModel().getSelectedItem().getSelectedVersion().mods);
	}

	private void setSelectedVersion(Version v) {
		version.setText(v.version);
		modpackTable.getSelectionModel().getSelectedItem().setSelectedVersion(v);
	}

	private void showModpackDetails(Modpack modpack) {
		if (modpack != null) {
			name.setText(modpack.getDisplayName());
			authorsLabel.setText("Author(s):");
			authors.setText(modpack.getAuthor());
			mcLabel.setText("Minecraft:");
			mcVer.setText(modpack.getSelectedVersion().mcVersion);
			forgeLabel.setText("Forge:");
			forgeVer.setText(modpack.getSelectedVersion().forgeVersion);
			description.setText(modpack.getDescription());

			Thread logoThread = new Thread(() -> {
				Image image = new Image(modpack.getLogoUrl());
				Platform.runLater(() -> this.image.setImage(image));
			});
			logoThread.start();

			version.getItems().removeAll(version.getItems());
			for (Version v : modpack.getVersions()) {
				VersionMenuItem item = new VersionMenuItem(v);
				item.setOnAction(event -> {
					setSelectedVersion(item.getVersion());
				});
				version.getItems().add(item);
			}

//			TODO: Add separator and view changelog item
		} else {
			name.setText("");
			authorsLabel.setText("");
			authors.setText("");
			mcLabel.setText("");
			mcVer.setText("");
			forgeLabel.setText("");
			forgeVer.setText("");
			description.setText("");
			image.setImage(null);
		}
		updateButtons(modpack);
	}

	private void updateButtons(Modpack modpack) {
		if (modpack != null) {
			if (ModpackUtils.isModpackInstalled(modpack)) {
//				if (ModpackUtils.canUpgrade(modpack, VersionUtils.getVersionFromString(modpack, version.getText()))) {
//					download.setDisable(false);
//				} else {
					download.setDisable(true);
//				}
				launch.setDisable(false);
				delete.setDisable(false);
				version.setDisable(false);
				mods.setDisable(false);

				String versionFile = null;
				try {
					versionFile = Files.readAllLines(Paths.get("modpacks/" + modpack.getName() + "/version.txt")).get(0);
				} catch (IOException ignored) {}

				if (versionFile != null) {
					for (Version v : modpack.getVersions()) {
						if (v.version.equals(versionFile)) {
							modpack.setSelectedVersion(v);
							version.setText(v.version);
						}
					}
				} else {
					version.setText(modpack.getSelectedVersion().version);
				}

			} else {
				download.setDisable(false);
				launch.setDisable(true);
				delete.setDisable(true);
				version.setDisable(false);
				mods.setDisable(false);

				version.setText(modpack.getSelectedVersion().version);
			}
		} else {
			download.setDisable(true);
			launch.setDisable(true);
			delete.setDisable(true);
			version.setDisable(true);
			mods.setDisable(true);
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
