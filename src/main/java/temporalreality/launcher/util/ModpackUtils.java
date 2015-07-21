package temporalreality.launcher.util;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import net.shadowfacts.shadowlib.log.LogLevel;
import net.shadowfacts.shadowlib.log.Logger;
import net.shadowfacts.shadowlib.util.FileUtils;
import net.shadowfacts.shadowlib.util.InternetUtils;
import net.shadowfacts.shadowlib.util.StreamRedirect;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.login.LoginDialogController;
import temporalreality.launcher.view.overview.ModpackOverviewController;
import temporalreality.launcher.view.passworddialog.PasswordDialogController;
import uk.co.rx14.jmclaunchlib.LaunchSpec;
import uk.co.rx14.jmclaunchlib.MCInstance;
import uk.co.rx14.jmclaunchlib.auth.PasswordSupplier;
import uk.co.rx14.jmclaunchlib.tasks.LaunchTask;
import uk.co.rx14.jmclaunchlib.tasks.LoginTask;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author shadowfacts
 */
public class ModpackUtils {

	public static void loadModpacks(List<Modpack> modpacks) throws Exception {
		for (String s : ConfigManager.getInstanceConfig().packIndexes) {
			TRLauncher.log.info("Loading modpacks from index at " + s);
			String data = getModpackData(s);
			String[] packs = data.split("\n");
			for (String pack : packs) {
				modpacks.add(Modpack.get(new URL(pack)));
			}
		}
	}

	private static String getModpackData(String urlString) throws Exception{
		URL url = new URL(urlString);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			return buffer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static File makeModpacksDir() {
		File dir = MiscUtils.getFile("modpacks/");
		if (!dir.exists()) {
			TRLauncher.log.info("modpacks/ folder does not exist, creating it");
			dir.mkdirs();
		}
		return dir;
	}

	public static File getPackDir(Modpack modpack) {
		return MiscUtils.getFile("modpacks/" + modpack.getName() + "/");
	}

	public static boolean isModpackInstalled(Modpack modpack) {
		makeModpacksDir();
		return getPackDir(modpack).exists();
	}

	public static void download(Modpack modpack,  Runnable successHandler, ModpackOverviewController controller) throws IOException {
		makeModpacksDir();

		if (!isModpackInstalled(modpack) || canUpgrade(modpack)) {
			Task downloadTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					int taskCount = modpack.getSelectedVersion().mods.size() + 5;

//					Create modpack dir
					if (!getPackDir(modpack).exists() && !isCancelled()) {
						TRLauncher.log.info("Creating dir for " + modpack.getName());
						updateMessage("Creating modpack directory");
						updateProgress(1, taskCount);

						getPackDir(modpack).mkdirs();
					}

//					Download override zip
					if (!isCancelled()) {
						TRLauncher.log.info("Downloading override zip to temp/" + modpack.getName() + ".zip");
						updateMessage("Downloading override zip");
						updateProgress(2, taskCount);

						InternetUtils.downloadFile(modpack.getSelectedVersion().overrideUrl, MiscUtils.getPath("temp/" + modpack.getName() + ".zip"));
					}

//					Extract override zip
					if (!isCancelled()) {
						TRLauncher.log.info("Extracting override zip");
						updateMessage("Extracting override zip");
						updateProgress(3, taskCount);

						FileUtils.unzipFile(MiscUtils.getPath("temp/" + modpack.getName() + ".zip"), MiscUtils.getPath("modpacks/" + modpack.getName() + "/"));
					}

//					Delete modpack zip
					if (!isCancelled()) {
						TRLauncher.log.info("Deleting override zip");
						updateMessage("Deleting override zip");
						updateProgress(4, taskCount);

						MiscUtils.getFile("temp/" + modpack.getName() + ".zip").delete();
					}

//					Download mods
					if (!isCancelled()) {
						for (int i = 0; i < modpack.getSelectedVersion().mods.size(); i++) {
							if (!isCancelled()) {
								Mod mod = modpack.getSelectedVersion().mods.get(i);

								TRLauncher.log.info("Downloading mod " + mod.name);
								updateMessage("Downloading mod " + mod.name);
								updateProgress(i + 5, taskCount);

								InternetUtils.downloadFile(mod.downloadUrl, MiscUtils.getPath("modpacks/" + modpack.getName() + "/mods/" + mod.fileName));
							}
						}
					}

//					Create version file
					if (!isCancelled()) {
						TRLauncher.log.info("Creating version.txt file for " + modpack.getName());
						updateMessage("Creating version.txt");
						updateProgress(taskCount, taskCount);

						File versionFile = MiscUtils.getFile("modpacks/" + modpack.getName() + "/version.txt");
						if (!versionFile.exists()) {
							versionFile.createNewFile();
						}

						PrintWriter writer = new PrintWriter(MiscUtils.getPath("modpacks/" + modpack.getName() + "/version.txt"));
						writer.println(modpack.getSelectedVersion().version);
						writer.close();
					}

//					Cancel and undo all changes
					if (isCancelled()) {
						FileUtils.deleteFolder(MiscUtils.getFile("modpacks/" + modpack.getName() + "/"));
						return null;
					}

					succeeded();
					return null;
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					updateProgress(1, 1);
					updateMessage("Done!");
					successHandler.run();
				}
			};

			DownloadDialogController dialog = TRLauncher.getLauncher().showDownloadDialog(modpack);
			dialog.getProgressBar().progressProperty().bind(downloadTask.progressProperty());
			dialog.getCurrentItemLabel().textProperty().bind(downloadTask.messageProperty());
			dialog.setTask(downloadTask);
			new Thread(downloadTask).start();

		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.initOwner(TRLauncher.getLauncher().getPrimaryStage());
			alert.setTitle("Modpack Already Installed");
			alert.setHeaderText(modpack.getDisplayName() + " is already installed.");
		}
	}

	public static boolean canUpgrade(Modpack modpack) {
		Version saved = null;
		try {
			String versionFile = Files.readAllLines(Paths.get(MiscUtils.getPath("modpacks/" + modpack.getName() + "/version.txt"))).get(0);

			if (versionFile != null) {
				for (Version v : modpack.getVersions()) {
					if (v.version.equals(versionFile)) {
						saved = v;
					}
				}
			}
		} catch (IOException ignored) {}

		return modpack.getSelectedVersion().equals(saved);
	}

	public static void launch(Modpack modpack) {
		if (isModpackInstalled(modpack)) {

//			PasswordSupplier passwordSupplier;
//
//			String selectedUsername = ConfigManager.getInstanceConfig().username;
//			boolean offline = false;
//
//			if (selectedUsername != null && !selectedUsername.equals("")) {
//				passwordSupplier = (String username) -> {
//
//				}
//			}

			PasswordSupplier passwordSupplier = null;

			String selectedUsername = ConfigManager.getInstanceConfig().username;
			final boolean[] offline = new boolean[]{false};

			if (selectedUsername != null && !selectedUsername.equals("")) {

				passwordSupplier = (String username) -> {
					PasswordDialogController controller = TRLauncher.getLauncher().showPasswordDialog(username);
					offline[0] = controller.isOffline();
					return controller.getPassword();
				};
			} else {
				LoginDialogController controller = TRLauncher.getLauncher().showLoginDialog();
				if (controller != null && controller.isLoggedIn()) {
					passwordSupplier = (String username) -> {
						if (username.equals(controller.getUsername())) {
							return controller.getPassword();
						} else {
							return null;
						}
					};
					selectedUsername = controller.getUsername();
					ConfigManager.getInstanceConfig().username = controller.getUsername();
					ConfigManager.getInstance().save();
				} else {
					TRLauncher.log.error("You must login at least once to launch Minecraft");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.initOwner(TRLauncher.getLauncher().getPrimaryStage());
					alert.setTitle("Login");
					alert.setHeaderText("Login Required");
					alert.setContentText("You must login at least once to launch Minecraft.");
					alert.showAndWait();
				}
			}

			MCInstance instance = MCInstance.createForge(
					modpack.getSelectedVersion().mcVersion,
					modpack.getSelectedVersion().forgeVersion,
					MiscUtils.getPath("modpacks/" + modpack.getName() + "/"),
					MiscUtils.getPath("caches/"),
					passwordSupplier
			);

			LaunchTask launchTask;

			if (offline[0]) {
				launchTask = instance.getOfflineLaunchTask("TRGuest" + new Random().nextInt(1000));
			} else {
				launchTask = instance.getLaunchTask(selectedUsername);
			}

//			TODO: Progress dialog

			for (uk.co.rx14.jmclaunchlib.util.Task task : launchTask.getCurrentTasks()) {
				if (task instanceof LoginTask)
			}

			launchTask.start();

			LaunchSpec spec = launchTask.getSpec();

			if (spec.getJvmArgs() == null) spec.setJvmArgs(new ArrayList<String>());
			if (spec.getLaunchArgs() == null) spec.setLaunchArgs(new ArrayList<String>());

			for (String s : ConfigManager.getInstanceConfig().jvmArgs) {
				if (s != null && !s.equals("")) spec.getJvmArgs().add(s);
			}
			spec.getLaunchArgs().add("--width=" + ConfigManager.getInstanceConfig().mcWidth);
			spec.getLaunchArgs().add("--height=" + ConfigManager.getInstanceConfig().mcHeight);

			Process process = spec.run(Paths.get(ConfigManager.getInstanceConfig().javaPath));
			StreamRedirect output = new StreamRedirect(process.getInputStream(), new Logger("MC", true), LogLevel.INFO);
			StreamRedirect error = new StreamRedirect(process.getErrorStream(), new Logger("MC", true), LogLevel.ERROR);
			output.start();
			error.start();



//			LoginDialogController controller = TRLauncher.getLauncher().showLoginDialog();
//
//			if (controller.isLoggedIn()) {
//
//				MCInstance instance = MCInstance.createForge(
//						modpack.getSelectedVersion().mcVersion,
//						modpack.getSelectedVersion().forgeVersion,
//						MiscUtils.getPath("modpacks/" + modpack.getName() + "/"),
//						MiscUtils.getPath("caches/"),
//						(String username) -> {
//							return "";
//						}
//				);
//
//				try {
////					LaunchSpec spec;
//					LaunchTask task;
//
//					if (controller.get() == null) {
////						spec = instance.getOfflineLaunchSpec("TRGuest" + new Random().nextInt(25));
//						task = instance.getLaunchTask();
//					} else {
////						spec = instance.getLaunchSpec();
//					}
//
////					because RX14 is using a String[] instead of an ArrayList<String>
//					ArrayList<String> temp = new ArrayList<>(Arrays.asList(spec.getJvmArgs()));
//					for (String s : ConfigManager.getInstanceConfig().jvmArgs) {
//						if (s != null && !s.equals("")) temp.add(s);
//					}
//					spec.setJvmArgs(temp.toArray(new String[0]));
//
//					ArrayList<String> temp2 = new ArrayList<>(Arrays.asList(spec.getLaunchArgs()));
//					temp2.add("--width=" + ConfigManager.getInstanceConfig().mcWidth);
//					temp2.add("--height=" + ConfigManager.getInstanceConfig().mcHeight);
//					spec.setLaunchArgs(temp2.toArray(new String[0]));
//
//					Process process = spec.run(Paths.get(ConfigManager.getInstanceConfig().javaPath));
//					StreamRedirect output = new StreamRedirect(process.getInputStream(), new Logger("MC", true), LogLevel.INFO);
//					StreamRedirect error = new StreamRedirect(process.getErrorStream(), new Logger("MC", true), LogLevel.ERROR);
//					output.start();
//					error.start();
//
//				} catch (ForbiddenOperationException e) {
//
//					TRLauncher.log.info("Invalid credentials!");
//					Alert alert = new Alert(Alert.AlertType.ERROR);
//					alert.initOwner(TRLauncher.getLauncher().getPrimaryStage());
//					alert.setTitle("Invalid Credentials");
//					alert.setHeaderText("Invalid login credentials");
//					alert.setContentText("Please launch Minecraft again and enter correct credentials.");
//
//					alert.showAndWait();
//
//				}
//			} else {
//				TRLauncher.log.info("User did not login or enter offline mode, cancelling launch.");
//			}
		}
	}


	public static void delete(Modpack modpack, Runnable successHandler) {
		if (isModpackInstalled(modpack)) {
			Task deleteTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					FileUtils.deleteFolder(MiscUtils.getFile("modpacks/" + modpack.getName() + "/"));

					return null;
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					successHandler.run();
				}
			};

			new Thread(deleteTask).start();
		}
	}

}
