package temporalreality.launcher.view.modlist;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.shadowfacts.shadowlib.util.DesktopUtils;
import temporalreality.launcher.model.Mod;

import java.net.URISyntaxException;

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
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().name));

		urlColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().url));

		modTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			try {
				DesktopUtils.openWebpage(newValue.url);
			} catch (URISyntaxException ignored) {}
		}));
	}


	public void setMods(ObservableList<Mod> mods) {
		this.mods = mods;
		modTable.setItems(this.mods);
	}


}
