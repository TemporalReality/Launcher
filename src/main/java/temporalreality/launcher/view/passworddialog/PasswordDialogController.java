package temporalreality.launcher.view.passworddialog;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shadowfacts
 */
public class PasswordDialogController {

	@Getter @Setter private Stage dialogStage;

	@Getter  private String username;
	@Getter private String password;

	@Getter private boolean loggedIn;
	@Getter private boolean offline;

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

	public void setUsername(String username) {
		this.username = username;
		initialize();
	}

}
