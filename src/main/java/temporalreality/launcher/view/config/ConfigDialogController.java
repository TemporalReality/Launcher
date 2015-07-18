package temporalreality.launcher.view.config;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import temporalreality.launcher.config.Config;
import temporalreality.launcher.config.ConfigManager;

import java.io.File;


/**
 * @author shadowfacts
 */
public class ConfigDialogController {

	private Stage dialogStage;

	@FXML
	private TextField javaPathField;

	@FXML
	private TextField jvmArgumentsField;

	@FXML
	private TextField widthField;

	@FXML
	private TextField heightField;

	@FXML
	private TextField launcherDirField;

	@FXML
	private void initialize() {
		Config config = ConfigManager.getInstanceConfig();

		javaPathField.setText(config.javaPath);
		for (String s : config.jvmArgs) {
			jvmArgumentsField.setText(jvmArgumentsField.getText() + " " + s);
		}
		widthField.setText(Integer.toString(config.mcWidth));
		heightField.setText(Integer.toString(config.mcHeight));
		launcherDirField.setText(config.launcherDir);
	}

	@FXML
	private void javaPathButtonPressed() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Java Executable");
		File file = fileChooser.showOpenDialog(dialogStage);
		if (file != null) {
			javaPathField.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void trDirPressed() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Launcher Directory");
		File folder = chooser.showDialog(dialogStage);
		if (folder != null) {
			launcherDirField.setText(folder.getAbsolutePath());
		}
	}

	@FXML
	private void okPressed() {
		Config config = ConfigManager.getInstanceConfig();

		config.javaPath = javaPathField.getText();
		config.jvmArgs = jvmArgumentsField.getText().split(" ");
		config.mcWidth = Integer.parseInt(widthField.getText());
		config.mcHeight = Integer.parseInt(heightField.getText());
		config.launcherDir = launcherDirField.getText();

		ConfigManager.getInstance().save();

		dialogStage.close();
	}

	@FXML
	private void cancelPressed() {
		dialogStage.close();
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}
