package temporalreality.launcher.view.downloaddialog;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import temporalreality.launcher.model.Modpack;

/**
 * @author shadowfacts
 */
@NoArgsConstructor
public class DownloadDialogController {

	@Getter @Setter private Modpack modpack;
	@Getter @Setter private Stage dialogStage;
	@Getter private Task<?> task;

	@FXML
	@Getter private Label downloadingLabel;

	@FXML
	@Getter private ProgressBar progressBar;

	@FXML
	@Getter private Label currentItemLabel;

	@FXML
	private void initialize() {

	}

	@FXML
	private void cancelPressed() {
		task.cancel();
		close();
	}

	public void close() {
		getDialogStage().close();
	}

	public void setTask(Task<?> task) {
		this.task = task;
		task.setOnSucceeded(event -> close());
	}

	public String getDownloadingLabelText() {
		return downloadingLabel.getText();
	}

	public void setDownloadingLabelText(String text) {
		downloadingLabel.setText(text);
	}

	public double getProgress() {
		return progressBar.getProgress();
	}

	public void setProgress(double progress) {
		progressBar.setProgress(progress);
	}

	public String getLabelText() {
		return currentItemLabel.getText();
	}

	public void setLabelText(String text) {
		currentItemLabel.setText(text);
	}
}
