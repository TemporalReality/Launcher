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
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

		urlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUrl()));

		authorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(StringUtils.join(cellData.getValue().getAuthors(), ", ")));

		modTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			try {
				if (modTable.getSelectionModel().getSelectedCells().get(0).getColumn() == 2)
					DesktopUtils.openWebpage(newValue.getUrl());
			} catch (URISyntaxException ignored) {
			}
		}));
	}


	public void setMods(ObservableList<Mod> mods) {
		this.mods = mods;
		modTable.setItems(this.mods);
	}
}