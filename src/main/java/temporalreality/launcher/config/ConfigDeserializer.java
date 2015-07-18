package temporalreality.launcher.config;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author shadowfacts
 */
public class ConfigDeserializer implements JsonDeserializer<Config> {

	@Override
	public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Config config = new Config();

		JsonObject obj = json.getAsJsonObject();

		config.javaPath = obj.get("javaPath").getAsString();

		JsonArray jvmArgArray = obj.get("jvmArgs").getAsJsonArray();
		String[] jvmArgs = new String[jvmArgArray.size()];
		for (int i = 0; i < jvmArgArray.size(); i++) {
			jvmArgs[i] = jvmArgArray.get(i).getAsString();
		}
		config.jvmArgs = jvmArgs;

		config.mcWidth = obj.get("mcWidth").getAsInt();
		config.mcHeight = obj.get("mcHeight").getAsInt();
		config.launcherDir = obj.get("launcherDir").getAsString();

		return config;
	}

}
