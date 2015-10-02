package temporalreality.launcher.view.config;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import temporalreality.launcher.config.Config;
import temporalreality.launcher.config.ConfigManager;

import java.io.File;


/**
 * @author shadowfacts
 */
public class ConfigDialogController {

	@Getter @Setter private Stage dialogStage;

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

		javaPathField.setText(config.getJavaPath());
		for (String s : config.getJvmArgs()) {
			jvmArgumentsField.setText(jvmArgumentsField.getText() + " " + s);
		}
		widthField.setText(Integer.toString(config.getMcWidth()));
		heightField.setText(Integer.toString(config.getMcHeight()));
		launcherDirField.setText(config.getLauncherDir());
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

		config.setJavaPath(javaPathField.getText());
		config.setJvmArgs(jvmArgumentsField.getText().split(" "));
		config.setMcWidth(Integer.parseInt(widthField.getText()));
		config.setMcHeight(Integer.parseInt(heightField.getText()));
		config.setLauncherDir(launcherDirField.getText());

		ConfigManager.getInstance().save();

		dialogStage.close();
	}

	@FXML
	private void cancelPressed() {
		dialogStage.close();
	}

}
