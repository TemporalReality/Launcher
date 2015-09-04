package temporalreality.launcher.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import org.apache.commons.io.FileUtils;

import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.util.Issues;
import temporalreality.launcher.util.MiscUtils;
import temporalreality.launcher.util.ModpackDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author shadowfacts
 */
public class Modpack {

	private String name;

	private final StringProperty displayName;
	private final StringProperty author;
	private final StringProperty description;

	private String logoUrl;

	private final ArrayList<Version> versions;

	private Version selectedVersion;

	private boolean beta;

	private transient Image logo;

	public Modpack() {
		this.name = "";

		this.displayName = new SimpleStringProperty();
		this.author = new SimpleStringProperty();
		this.description = new SimpleStringProperty();

		this.versions = new ArrayList<>();

		this.beta = false;
	}

	public static Modpack get(File f) throws FileNotFoundException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Modpack.class, new ModpackDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(new FileReader(f), Modpack.class);
	}

	public static Modpack get(String json) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Modpack.class, new ModpackDeserializer());
		Gson gson = builder.create();
		Modpack pack = gson.fromJson(json, Modpack.class);
		pack.getLogo();
		return pack;
	}

	public static Modpack get(URL url) throws IOException {
		TRLauncher.log.info("Loading modpack from " + url);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			return get(buffer.toString());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName.get();
	}

	public void setDisplayName(String displayName) {
		this.displayName.set(displayName);
	}

	public StringProperty getNameProperty() {
		return displayName;
	}

	public String getAuthor() {
		return author.get();
	}

	public void setAuthor(String author) {
		this.author.set(author);
	}

	public StringProperty getAuthorProperty() {
		return author;
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public ArrayList<Version> getVersions() {
		return versions;
	}

	public void addVersion(Version version) {
		versions.add(version);
	}

	public Version getSelectedVersion() {
		if (selectedVersion != null) {
			return selectedVersion;
		}else {
			return versions.get(0);
		}
	}

	public void setSelectedVersion(Version selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	public boolean isBeta() {
		return beta;
	}

	public void setBeta(boolean beta) {
		this.beta = beta;
	}

	public Image getLogo() {
		if (logo == null) {
			Properties properties = MiscUtils.getLogoProperties();
			if (!properties.containsKey(getName())) {
				String file = MiscUtils.getPath("caches/logos/" + getName() + ".logo");
				properties.setProperty(getName(), file);
				try {
					FileUtils.copyURLToFile(new URL(getLogoUrl()), new File(file));
				} catch (IOException e) {
					TRLauncher.log.catching(e);
					Issues.create(null, e, null);
				}
				try (OutputStream out = FileUtils.openOutputStream(MiscUtils.getFile("caches/logos.properties"))) {
					properties.store(out, null);
				} catch (IOException e) {
					TRLauncher.log.catching(e);
					Issues.create(null, e, null);
				}
			}
			try (InputStream in = FileUtils.openInputStream(new File(properties.getProperty(getName())))) {
				logo = new Image(in);
			} catch (IOException e) {
				TRLauncher.log.catching(e);
				Issues.create(null, e, null);
			}
		}
		return logo;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}