package temporalreality.launcher.util;

import com.google.gson.*;
import temporalreality.launcher.model.Mod;
import temporalreality.launcher.model.Modpack;
import temporalreality.launcher.model.Version;

import java.lang.reflect.Type;

/**
 * @author shadowfacts
 */
public class ModpackDeserializer implements JsonDeserializer<Modpack> {

	@Override
	public Modpack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Modpack modpack = new Modpack();

		JsonObject obj = json.getAsJsonObject();

		modpack.setName(obj.get("name").getAsString());
		modpack.setDisplayName(obj.get("displayName").getAsString());
		modpack.setAuthor(obj.get("author").getAsString());
		modpack.setDescription(obj.get("description").getAsString());
		modpack.setLogoUrl(obj.get("logoUrl").getAsString());

		if (obj.has("beta")) modpack.setBeta(obj.get("beta").getAsBoolean());

		JsonArray versionArray = obj.get("versions").getAsJsonArray();
		for (int i = 0; i < versionArray.size(); i++) {
			Version v = new Version();
			JsonObject versionObj = versionArray.get(i).getAsJsonObject();

			v.version = versionObj.get("version").getAsString();
			JsonElement changelogUrl = versionObj.get("changelogUrl");
			v.changelogUrl = changelogUrl == null ? "" : changelogUrl.getAsString();
			v.mcVersion = versionObj.get("mcVersion").getAsString();
			JsonElement forgeVersion = versionObj.get("forgeVersion");
			v.forgeVersion = forgeVersion == null ? "" : forgeVersion.getAsString();
			JsonElement overrideUrl = versionObj.get("overrideUrl");
			v.overrideUrl = overrideUrl == null ? "" : overrideUrl.getAsString();

			JsonElement mods = versionObj.get("mods");
			if (mods != null) {
				for (int j = 0; j < mods.getAsJsonArray().size(); j++) {
					Mod mod = new Mod();
					JsonObject modObj = mods.getAsJsonArray().get(j).getAsJsonObject();

					mod.name = modObj.get("name").getAsString();
					mod.url = modObj.get("url").getAsString();

					JsonArray authorsArray = modObj.get("authors").getAsJsonArray();
					for (int k = 0; k < authorsArray.size(); k++) {
						mod.authors.add(authorsArray.get(k).getAsString());
					}

					JsonElement downloadUrl = modObj.get("downloadUrl");
					mod.downloadUrl = downloadUrl == null ? "" : downloadUrl.getAsString();

					JsonElement fileName = modObj.get("fileName");
					mod.fileName = fileName == null ? "" : fileName.getAsString();

					v.addMod(mod);
				}
			}

			modpack.addVersion(v);
		}

		modpack.setSelectedVersion(modpack.getVersions().get(0));

		return modpack;
	}
}
