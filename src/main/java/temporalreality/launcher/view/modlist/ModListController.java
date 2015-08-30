package temporalreality.launcher.view.modlist;

import java.net.URISyntaxException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.shadowfacts.shadowlib.util.DesktopUtils;
import net.shadowfacts.shadowlib.util.StringUtils;
import temporalreality.launcher.model.Mod;

/**
 * @author shadowfacts
 */
public class ModListController {

	private ObservableList<Mod> mods;

	@FXML
	private TableView<Mod> modTable;

	@FXML
	private TableColumn<Mod, String> nameColumn;

	@FXML
	private TableColumn<Mod, String> urlColumn;

	@FXML
	private TableColumn<Mod, String> authorsColumn;


	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));

		urlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().url));

		authorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(StringUtils.join(cellData.getValue().authors, ", ")));

		modTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			try {
				DesktopUtils.openWebpage(newValue.url);
			} catch (URISyntaxException ignored) {
			}
		}));
	}


	public void setMods(ObservableList<Mod> mods) {
		this.mods = mods;
		modTable.setItems(this.mods);
	}


}
