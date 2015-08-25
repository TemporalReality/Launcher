package temporalreality.launcher.config;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
		result.add("launcherDir", new JsonPrimitive(src.launcherDir));

		JsonArray packIndexes = new JsonArray();
		for (String s : src.packIndexes) {
			packIndexes.add(new JsonPrimitive(s));
		}
		result.add("packIndexes", packIndexes);

		result.add("mcWidth", new JsonPrimitive(src.mcWidth));
		result.add("mcHeight", new JsonPrimitive(src.mcHeight));
		result.add("username", new JsonPrimitive(src.username));
		result.add("offline", new JsonPrimitive(src.offline));
		/*
		Account Manager
		 */
		//		JsonArray accounts = new JsonArray();
		//		for (Account account : AccountManager.getInstance().getAccounts()) {
		//			JsonObject accountObj = new JsonObject();
		//			accountObj.add("username", new JsonPrimitive(account.getUsername()));
		//			accountObj.add("active", new JsonPrimitive(account.equals(AccountManager.getInstance().getSelectedAccount())));
		//			accounts.add(accountObj);
		//		}
		//		result.add("accounts", accounts);

		return result;
	}

}
