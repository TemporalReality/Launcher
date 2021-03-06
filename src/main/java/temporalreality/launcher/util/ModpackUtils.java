package temporalreality.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import net.shadowfacts.shadowlib.log.LogLevel;
import net.shadowfacts.shadowlib.log.Logger;
import net.shadowfacts.shadowlib.util.FileUtils;
import net.shadowfacts.shadowlib.util.StreamRedirect;

import org.apache.commons.io.IOUtils;

import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.config.ConfigManager;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Side;
import temporalreality.launcher.view.downloaddialog.DownloadDialogController;
import temporalreality.launcher.view.overview.ModpackOverviewController;
import uk.co.rx14.jmclaunchlib.LaunchSpec;
import uk.co.rx14.jmclaunchlib.LaunchTask;
import uk.co.rx14.jmclaunchlib.LaunchTaskBuilder;
import uk.co.rx14.jmclaunchlib.auth.PasswordSupplier;

/**
 * @author shadowfacts
 */
public class ModpackUtils {

	public static List<String> getPackIndexes() {
		List<String> packIndexes = new ArrayList<String>();
		for (String s: System.getProperty("temporalreality.launcher.packIndexes", "").split(","))
			if (!s.isEmpty())
				packIndexes.add(s);
		for (String s: ConfigManager.getInstanceConfig().getPackIndexes())
			if (s != null && !s.isEmpty())
				packIndexes.add(s);
		return packIndexes;
	}

	public static void loadModpacks(Map<String, Modpack> modpacks) throws Exception {
		for (String s : getPackIndexes()) {
			TRLauncher.log.info("Loading modpacks from index at " + s);
			String data = getModpackData(s);
			String[] packs = data.split("\n");
			for (String pack : packs) {
				try {
					Modpack modpack = Modpack.get(new URL(pack));
					if (modpacks.containsKey(modpack.getName())) {
						TRLauncher.log.warn("Duplicate pack %s!", modpack.getName());
						continue;
					}
					modpacks.put(modpack.getName(), modpack);
				} catch (Throwable t) {
					Issues.create("Issue while adding pack " + pack, t);
				}
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

		boolean installed = isModpackInstalled(modpack);
		if (!installed || canUpgrade(modpack)) {
			File packDir = getPackDir(modpack);
			Task<Void> downloadTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					int taskCount = modpack.getSelectedVersion().getMods().size() + 5;

					//					Create modpack dir
					if (!packDir.exists() && !isCancelled()) {
						TRLauncher.log.info("Creating dir for " + modpack.getName());
						updateMessage("Creating modpack directory");
						updateProgress(1, taskCount);

						packDir.mkdirs();
					}
					else if (!isCancelled()) {
						File versionsTxt = MiscUtils.getFile("modpacks/" + modpack.getName() + "/versions.txt");
						String oldVersion = versionsTxt.exists() ? org.apache.commons.io.FileUtils.readLines(versionsTxt).get(0) : "";
						TRLauncher.log.info("Making backup of " + modpack.getName());
						updateMessage("Making backup of old modpack files");
						updateProgress(1, taskCount);
						ZipUtils.zipFolder(MiscUtils.getFile("modpacks/" + modpack.getName()), MiscUtils.getFile("backups/" + modpack.getName() + '-' + System.currentTimeMillis() + ".zip"));
						for (File file: packDir.listFiles()) {
							if (file.isDirectory() && file.getParent() != null && file.getParentFile().getName().equals("saves"))
								file.renameTo(new File(file.getAbsolutePath() + " - " + oldVersion));
							else if (!file.getName().matches("(saves|options.txt|servers.dat)"))
								if (file.isDirectory())
									FileUtils.deleteFolder(file);
								else
									file.delete();
						}
					}

					//					Download override zip
					if (modpack.getSelectedVersion().getOverrideUrl() != null && !modpack.getSelectedVersion().getOverrideUrl().equals("")) {
						if (!isCancelled()) {
							TRLauncher.log.info("Downloading override zip to temp/" + modpack.getName() + ".zip");
							updateMessage("Downloading override zip");
							updateProgress(2, taskCount);

							HttpURLConnection connection = (HttpURLConnection) new URL(modpack.getSelectedVersion().getOverrideUrl()).openConnection();
							connection.setRequestMethod("GET");
							connection.setAllowUserInteraction(false);
							connection.setDoInput(true);
							connection.setDoOutput(true);
							connection.connect();
							File f = MiscUtils.getFile("temp/" + modpack.getName() + ".zip");
							if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
							if (!f.exists()) f.createNewFile();
							try (InputStream in = connection.getInputStream(); OutputStream out = new FileOutputStream(f)) {
								IOUtils.copy(in, out);
							}
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
					}

					//					Download mods
					if (!isCancelled()) {
						for (int i = 0; i < modpack.getSelectedVersion().getMods().size(); i++) {
							if (!isCancelled()) {
								Mod mod = modpack.getSelectedVersion().getMods().get(i);

								if (mod.getDownloadUrl() != null && !mod.getDownloadUrl().equals("") && mod.getSide() != Side.SERVER) {
									if (mod.getFileName() == null || mod.getFileName().isEmpty())
										//mod.fileName = mod.name + mod.downloadUrl.hashCode() + ".jar";
										continue;
									TRLauncher.log.info("Downloading mod " + mod.getName());
									updateMessage("Downloading mod " + mod.getName());
									updateProgress(i + 5, taskCount);
									HttpURLConnection connection = (HttpURLConnection) new URL(mod.getDownloadUrl()).openConnection();
									connection.setRequestMethod("GET");
									connection.setAllowUserInteraction(false);
									connection.setDoInput(true);
									connection.setDoOutput(true);
									connection.connect();
									File f = MiscUtils.getFile("modpacks/" + modpack.getName() + "/mods/" + mod.getFileName());
									if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
									if (!f.exists()) f.createNewFile();
									try (InputStream in = connection.getInputStream(); OutputStream out = new FileOutputStream(f)) {
										IOUtils.copy(in, out);
									}
								}
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
						writer.println(modpack.getSelectedVersion().getVersion());
						writer.close();
					}

					//					Cancel and undo all changes
					if (isCancelled())
						FileUtils.deleteFolder(MiscUtils.getFile("modpacks/" + modpack.getName() + "/"));
					else
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
		String saved = null;
		try {
			saved = Files.readAllLines(Paths.get(MiscUtils.getPath("modpacks/" + modpack.getName() + "/version.txt"))).get(0);
		} catch (IOException e) {
			TRLauncher.log.error("There was a problem reading version file for modpack: " + modpack);
			e.printStackTrace();
			Issues.create("Couldn't read version file for " + modpack, e);
		}
		return !modpack.getSelectedVersion().getVersion().equals(saved);
	}

	public static void launch(Modpack modpack, boolean offline) {
		if (isModpackInstalled(modpack)) {

			if (ConfigManager.getInstanceConfig().getUsername() == null || ConfigManager.getInstanceConfig().getUsername().equals("")) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Login!");
				alert.setHeaderText("You must have a username selected");
				alert.setContentText("You must select a username in the Accounts menu to launch Minecraft.");
				alert.initModality(Modality.WINDOW_MODAL);
				alert.initOwner(TRLauncher.getLauncher().getPrimaryStage());
				alert.showAndWait();
			}

			PasswordSupplier passwordSupplier = null;

			final String selectedUsername = ConfigManager.getInstanceConfig().getUsername();
			String theUsername;

			if (selectedUsername != null && !selectedUsername.equals("")) {

				passwordSupplier = (username, retry, failureMessage) -> {
					FutureTask<String> passwordTask;

					if (!retry) {
						passwordTask = new FutureTask<>(() -> TRLauncher.getLauncher().showPasswordDialog(selectedUsername).getPassword());
					} else {
						passwordTask = new FutureTask<>(() -> TRLauncher.getLauncher().showPasswordDialog(selectedUsername, failureMessage).getPassword());
					}



					try {
						Platform.runLater(passwordTask);
						return passwordTask.get();
					} catch (InterruptedException e) {
						TRLauncher.log.error("The password retrieval was interrupted");
						TRLauncher.log.catching(e);
						Issues.create(null, e);
					} catch (ExecutionException e) {
						TRLauncher.log.error("There was a problem retrieving the password");
						TRLauncher.log.catching(e);
						Issues.create(null, e);
					}
					return null;
				};
			}

			LaunchTaskBuilder builder = new LaunchTaskBuilder()
					.setCachesDir(MiscUtils.getPath("caches/"))
					.setInstanceDir(MiscUtils.getPath("modpacks/" + modpack.getName() + "/"))
					.setPasswordSupplier(passwordSupplier);

			if (modpack.getSelectedVersion().getForgeVersion() != null && !modpack.getSelectedVersion().getForgeVersion().equals("")) {
				builder = builder.setForgeVersion(modpack.getSelectedVersion().getMcVersion(), modpack.getSelectedVersion().getForgeVersion());
			} else {
				builder = builder.setMinecraftVersion(modpack.getSelectedVersion().getMcVersion());
			}

			final LaunchTaskBuilder theBuilder;

			if (ConfigManager.getInstanceConfig().getUsername() != null && !ConfigManager.getInstanceConfig().getUsername().equals("")) {
				theUsername = ConfigManager.getInstanceConfig().getUsername();
			} else {
				theUsername = "TRGuest" + new Random().nextInt(1000);
			}

			theBuilder = builder.setUsername(theUsername).setOffline(offline);

			Runnable task = () -> {
				LaunchTask launchTask = theBuilder.build();

				//			TODO: Progress dialog

				launchTask.start();

				LaunchSpec spec = launchTask.getSpec();

				if (spec.getJvmArgs() == null)
					spec.setJvmArgs(new ArrayList<String>());
				if (spec.getLaunchArgs() == null)
					spec.setLaunchArgs(new ArrayList<String>());

				for (String s : ConfigManager.getInstanceConfig().getJvmArgs()) {
					if (s != null && !s.equals("")) spec.getJvmArgs().add(s);
				}
				spec.getJvmArgs().add("-Dtemporalreality.launcher.modpack=" + modpack.getName());
				spec.getLaunchArgs().add("--width=" + ConfigManager.getInstanceConfig().getMcWidth());
				spec.getLaunchArgs().add("--height=" + ConfigManager.getInstanceConfig().getMcHeight());

				Process process = spec.run(Paths.get(ConfigManager.getInstanceConfig().getJavaPath()));
				StreamRedirect output = new StreamRedirect(process.getInputStream(), new Logger("MC", true), LogLevel.INFO);
				StreamRedirect error = new StreamRedirect(process.getErrorStream(), new Logger("MC", true), LogLevel.ERROR);
				output.start();
				error.start();

				TRLauncher.getLauncher().setMinecraft(process);
			};

			TRLauncher.getAnalytics().sendEvent("LaunchModpack:" + modpack.getName() + ':' + modpack.getSelectedVersion().toString());

			new Thread(task).start();

		}
	}


	public static void delete(Modpack modpack, Runnable successHandler) {
		if (isModpackInstalled(modpack)) {
			Task<Void> deleteTask = new Task<Void>() {
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
