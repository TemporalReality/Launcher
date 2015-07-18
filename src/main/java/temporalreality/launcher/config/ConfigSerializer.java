package temporalreality.launcher.config;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author shadowfacts
 */
public class ConfigSerializer implements JsonSerializer<Config> {

	@Override
	public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.add("javaPath", new JsonPrimitive(src.javaPath));

		JsonArray jvmArgs = new JsonArray();
		for (String s : src.jvmArgs) {
			jvmArgs.add(new JsonPrimitive(s));
		}
		result.add("jvmArgs", jvmArgs);

		result.add("mcWidth", new JsonPrimitive(src.mcWidth));
		result.add("mcHeight", new JsonPrimitive(src.mcHeight));
		result.add("launcherDir", new JsonPrimitive(src.launcherDir));

		return result;
	}

}