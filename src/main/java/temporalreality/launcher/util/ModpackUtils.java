package temporalreality.launcher.util;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import net.shadowfacts.shadowlib.log.LogLevel;
import net.shadowfacts.shadowlib.log.Logger;
import net.shadowfacts.shadowlib.util.FileUtils;
import net.shadowfacts.shadowlib.util.StreamRedirect;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.overview.ModpackOverviewController;
import uk.co.rx14.jmclaunchlib.MCInstance;
import uk.co.rx14.jmclaunchlib.util.NullSupplier;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author shadowfacts
 */
public class ModpackUtils {

	public static void loadModpacks(String url, List<Modpack> modpacks) throws Exception {
//		System.out.println("Loading modpacks from " + url);
//		String data = getModpackData(url);
//		String[] packs = data.split("\n");
//		for (String s : packs) {
//			modpacks.add(Modpack.get(new URL(s)));
//		}
		System.out.println("Loading modpacks from local file");
		modpacks.add(Modpack.get(new File("basically-basic.json")));
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
		File dir = new File("modpacks/");
		if (!dir.exists()) {
			System.out.println("modpacks/ folder does not exist, creating it");
			dir.mkdirs();
		}
		return dir;
	}

	public static File getPackDir(Modpack modpack) {
		return new File("modpacks/" + modpack.getName() + "/");
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
						System.out.println("Creating dir for " + modpack.getName());
						updateMessage("Creating modpack directory");
						updateProgress(1, taskCount);

						getPackDir(modpack).mkdirs();
					}

//					Download override zip
					if (!isCancelled()) {
						System.out.println("Downloading override zip to temp/" + modpack.getName() + ".zip");
						updateMessage("Downloading override zip");
						updateProgress(2, taskCount);

						FileUtils.downloadFile(modpack.getSelectedVersion().overrideUrl, "temp/" + modpack.getName() + ".zip");
					}

//					Extract override zip
					if (!isCancelled()) {
						System.out.println("Extracting override zip");
						updateMessage("Extracting override zip");
						updateProgress(3, taskCount);

						FileUtils.unzipFile("temp/" + modpack.getName() + ".zip", "modpacks/" + modpack.getName() + "/");
					}

//					Delete modpack zip
					if (!isCancelled()) {
						System.out.println("Deleting override zip");
						updateMessage("Deleting override zip");
						updateProgress(4, taskCount);

						new File("temp/" + modpack.getName() + ".zip").delete();
					}

//					Download mods
					if (!isCancelled()) {
						for (int i = 0; i < modpack.getSelectedVersion().mods.size(); i++) {
							if (!isCancelled()) {
								Mod mod = modpack.getSelectedVersion().mods.get(i);

								System.out.println("Downloading mod " + mod.name);
								updateMessage("Downloading mod " + mod.name);
								updateProgress(i + 5, taskCount);

								FileUtils.downloadFile(mod.downloadUrl, "modpacks/" + modpack.getName() + "/mods/" + mod.fileName);
							}
						}
					}

//					Create version file
					if (!isCancelled()) {
						System.out.println("Creating version.txt file for " + modpack.getName());
						updateMessage("Creating version.txt");
						updateProgress(taskCount, taskCount);

						File versionFile = new File("modpacks/" + modpack.getName() + "/version.txt");
						if (!versionFile.exists()) {
							versionFile.createNewFile();
						}

						PrintWriter writer = new PrintWriter("modpacks/" + modpack.getName() + "/version.txt");
						writer.println(modpack.getSelectedVersion().version);
						writer.close();
					}

//					Cancel and undo all changes
					if (isCancelled()) {
						FileUtils.deleteFolder(new File("modpacks/" + modpack.getName() + "/"));
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
			String versionFile = Files.readAllLines(Paths.get("modpacks/" + modpack.getName() + "/version.txt")).get(0);

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
			MCInstance instance = MCInstance.createForge(
					modpack.getSelectedVersion().mcVersion,
					modpack.getSelectedVersion().forgeVersion,
					"modpacks/" + modpack.getName() + "/",
					"caches/",
					NullSupplier.INSTANCE
			);

			MCInstance.LaunchSpec spec = instance.getOfflineLaunchSpec("ShadowfactsDev");

			Process process = spec.run(Paths.get("/usr/bin/java"));
			StreamRedirect output = new StreamRedirect(process.getInputStream(), new Logger("MC", true), LogLevel.INFO);
			StreamRedirect error = new StreamRedirect(process.getErrorStream(), new Logger("MC", true), LogLevel.ERROR);
			output.start();
			error.start();
		}
	}


	public static void delete(Modpack modpack, Runnable successHandler) {
		if (isModpackInstalled(modpack)) {
			Task deleteTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					FileUtils.deleteFolder(new File("modpacks/" + modpack.getName() + "/"));

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
