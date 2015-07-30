package temporalreality.launcher.view.account;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author shadowfacts
 */
public class SetUsernameController {

	private Stage dialogStage;

	private String username = "";

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

	public String getUsername() {
		return username;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}
