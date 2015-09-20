package temporalreality.launcher.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.util.Issues;
import temporalreality.launcher.util.MiscUtils;

import com.google.gson.Gson;

/**
 * @author shadowfacts
 */
public class Modpack {

	private String name, displayName, author, description, logoUrl;

	private Set<String> tags = new HashSet<>(5);

	private ArrayList<Version> versions;

	private transient Version selectedVersion;

	private boolean beta;
	private boolean listed = true;
	private transient boolean favorite;

	private transient Image logo;

	public static Modpack get(File f) throws IOException {
		try (FileReader reader = new FileReader(f)) {
			return get(IOUtils.toString(reader));
		}
	}

	public static Modpack get(String json) {
		Modpack pack = new Gson().fromJson(json, Modpack.class);
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
		return displayName + (listed ? "" : " (Unlisted)");
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public StringProperty getNameProperty() {
		return new SimpleStringProperty(getDisplayName());
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public StringProperty getAuthorProperty() {
		return new SimpleStringProperty(author);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StringProperty getDescriptionProperty() {
		return new SimpleStringProperty(description);
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

	public void setListed(boolean listed) {
		this.listed = listed;
	}

	public boolean isListed() {
		return listed;
	}

	public Set<String> getTags() {
		if (tags.size() > 5)
			tags.removeIf((label) -> tags.size() > 5 || label.length() > 15);
		return tags;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public Image getLogo() {
		if (logo == null && getLogoUrl() != null) {
			File file = new File(MiscUtils.getPath("caches/logos/" + getLogoUrl().hashCode() + ".logo"));
			if (!file.exists())
				try {
					FileUtils.copyURLToFile(new URL(getLogoUrl()), file);
				} catch (IOException e) {
					Issues.create("Issue while downloading logo for pack " + name, e);
				}
			try (InputStream in = FileUtils.openInputStream(file)) {
				logo = new Image(in);
			} catch (IOException e) {
				Issues.create("Issue while loading logo for pack " + name, e);
			}
		}
		return logo;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}