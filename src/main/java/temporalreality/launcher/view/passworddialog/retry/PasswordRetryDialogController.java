package temporalreality.launcher.view.passworddialog.retry;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;
import temporalreality.launcher.view.passworddialog.PasswordDialogController;

/**
 * @author shadowfacts
 */
public class PasswordRetryDialogController extends PasswordDialogController {

	@Getter private String failureMessage;

	@FXML
	private Label failMsgLabel;

	@Override
	@FXML
	protected void initialize() {
		super.initialize();
		failMsgLabel.setText(failureMessage);
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
		initialize();
	}

}
