package temporalreality.launcher.view.overview;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.control.VersionMenuItem;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;
import temporalreality.launcher.util.Issues;
import temporalreality.launcher.util.ModpackUtils;

/**
 * @author shadowfacts
 */
public class ModpackOverviewController {

	private Stage primaryStage;

	private ObservableList<Modpack> modpackList = FXCollections.observableArrayList();

	@FXML
	private TextField searchField;

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

	@FXML
	private void initialize() {
		TRLauncher.getLauncher().getModpacks().forEach((key, value) -> {
			if (value.isListed())
				modpackList.add(value);
		});

		modpackTable.setItems(modpackList);

		nameColumn.setCellValueFactory(cellData ->
		cellData.getValue().isBeta() ? cellData.getValue().getNameProperty().concat(" (BETA)") : cellData.getValue().getNameProperty()
				);

		showModpackDetails(null);

		modpackTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
		showModpackDetails(newValue)
				));

		version.setOnAction(event -> updateButtons());
	}

	@FXML
	private void onSearch() {
		modpackList.clear();

		String search = searchField.getText().toLowerCase();
		if (!search.isEmpty()) {
			if (TRLauncher.getLauncher().getModpacks().containsKey(searchField.getText()))
				modpackList.add(TRLauncher.getLauncher().getModpacks().get(searchField.getText()));
			TRLauncher.getLauncher().getModpacks().values().stream().filter(modpack -> {
				if (!searchField.getText().equals(modpack.getName()))
					return false;
				boolean ret = false;
				String[] bits = modpack.getDisplayName().split(" ");
				String[] otherBits = search.split(" ");
				for (String s : bits) {
					for (String s2 : otherBits) {
						ret = ret || s.toLowerCase().startsWith(s2);
					}
				}
				return ret && !searchField.getText().equals(modpack.getName());
			}).forEach(modpackList::add);
		} else {
			TRLauncher.getLauncher().getModpacks().forEach((key, value) -> {
				if (value.isListed())
					modpackList.add(value);
			});
		}
	}

	@FXML
	private void downloadPressed() {
		try {
			Modpack active = modpackTable.getSelectionModel().getSelectedItem();
			ModpackUtils.download(active, () -> {
				updateButtons(active);
				TRLauncher.getAnalytics().sendEvent("DownloadModpack:" + active.getName() + ':' + active.getSelectedVersion().toString());
			}, this);
		} catch (IOException e) {
			TRLauncher.log.catching(e);
			Modpack active = modpackTable.getSelectionModel().getSelectedItem();
			Issues.create("Error when clicking download for pack " + active != null ? active.getName() : null, e);
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
			ModpackUtils.delete(active, () -> {
				updateButtons(active);
				TRLauncher.getAnalytics().sendEvent("DeleteModpack:" + active.getName());
			});
		}
	}

	@FXML
	private void versionSelected() {
		updateButtons(modpackTable.getSelectionModel().getSelectedItem());
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
				Platform.runLater(() -> this.image.setImage(modpack.getLogo()));
			});
			logoThread.start();

			version.getItems().removeAll(version.getItems());
			for (Version v : modpack.getVersions()) {
				VersionMenuItem item = new VersionMenuItem(v);
				item.setOnAction(event -> {
					setSelectedVersion(item.getVersion());
					updateButtons();
				});
				version.getItems().add(item);
			}
			String versionFile = null;
			try {
				versionFile = Files.readAllLines(Paths.get("modpacks/" + modpack.getName() + "/version.txt")).get(0);
			} catch (IOException ignored) {}

			if (versionFile != null) {
				for (Version v : modpack.getVersions()) {
					if (v.version.equals(versionFile)) {
						setSelectedVersion(v);
					}
				}
			} else {
				setSelectedVersion(modpack.getVersions().get(modpack.getVersions().size() - 1));
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

	public void updateButtons() {
		updateButtons(modpackTable.getSelectionModel().getSelectedItem());
	}

	private void updateButtons(Modpack modpack) {
		if (modpack != null) {
			if (ModpackUtils.isModpackInstalled(modpack)) {
				if (ModpackUtils.canUpgrade(modpack)) {
					download.setDisable(false);
				} else {
					download.setDisable(true);
				}
				launch.setDisable(false);
				delete.setDisable(false);
				version.setDisable(false);
				mods.setDisable(false);
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
