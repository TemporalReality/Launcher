package temporalreality.launcher.view.passwordidalog;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author shadowfacts
 */
public class PasswordDialogController {

	private Stage dialogStage;

	private String username;
	private String password;

	private boolean loggedIn;
	private boolean offline;

	@FXML
	private Label label;

	@FXML
	private TextField textField;

	@FXML
	private void initialize() {
		label.setText("Please enter the password for " + username);
	}

	@FXML
	private void okPressed() {
		password = textField.getText();

		loggedIn = true;

		dialogStage.close();
	}

	@FXML
	private void cancelPressed() {
		loggedIn = false;

		dialogStage.close();
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isOffline() {
		return offline;
	}
}
