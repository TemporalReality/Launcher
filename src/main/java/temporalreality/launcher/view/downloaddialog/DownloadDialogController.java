package temporalreality.launcher.view.downloaddialog;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import temporalreality.launcher.model.Modpack;

/**
 * @author shadowfacts
 */
public class DownloadDialogController {

	private Modpack modpack;
	private Stage dialogStage;
	private Task<?> task;

	@FXML
	private Label downloadingLabel;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label currentItemLabel;

	public DownloadDialogController() {

	}

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

	public Modpack getModpack() {
		return modpack;
	}

	public void setModpack(Modpack modpack) {
		this.modpack = modpack;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public Task<?> getTask() {
		return task;
	}

	public void setTask(Task<?> task) {
		this.task = task;
		task.setOnSucceeded(event -> {
			close();
		});
	}

	public String getDownloadingLabelText() {
		return downloadingLabel.getText();
	}

	public void setDownloadingLabelText(String text) {
		downloadingLabel.setText(text);
	}

	public Label getDownloadingLabel() {
		return downloadingLabel;
	}

	public double getProgress() {
		return progressBar.getProgress();
	}

	public void setProgress(double progress) {
		progressBar.setProgress(progress);
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public String getLabelText() {
		return currentItemLabel.getText();
	}

	public void setLabelText(String text) {
		currentItemLabel.setText(text);
	}

	public Label getCurrentItemLabel() {
		return currentItemLabel;
	}
}
