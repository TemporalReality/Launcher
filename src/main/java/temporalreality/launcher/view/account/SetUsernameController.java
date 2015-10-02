package temporalreality.launcher.view.account;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shadowfacts
 */
public class SetUsernameController {

	@Getter @Setter private Stage dialogStage;

	@Getter private String username = "";

	@FXML
	private TextField usernameField;

	@FXML
	private void okPressed() {
		username = usernameField.getText();
		dialogStage.close();
	}

	@FXML
	private void cancelPressed() {
		dialogStage.close();
	}

}
