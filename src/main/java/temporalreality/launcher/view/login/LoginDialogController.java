package temporalreality.launcher.view.login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uk.co.rx14.jmclaunchlib.auth.Credentials;

import java.util.function.Supplier;

/**
 * @author shadowfacts
 */
public class LoginDialogController {

	private Stage dialogStage;

	private String username = null;
	private String password = null;

	private boolean loggedIn;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button ok;

	@FXML
	private Button cancel;

	@FXML
	private void initialize() {

	}

	@FXML
	private void okPressed() {
		username = usernameField.getText();
		password = passwordField.getText();

		loggedIn = true;

		dialogStage.close();
	}

	@FXML
	private void cancelPressed() {
		loggedIn = false;

		dialogStage.close();
	}

	public Credentials get() {
		if (username != null && password != null) {
			return new Credentials(username, password);
		} else {
			return null;
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
