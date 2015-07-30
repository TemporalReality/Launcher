package temporalreality.launcher.config;

import com.google.gson.*;
import temporalreality.launcher.accounts.Account;
import temporalreality.launcher.accounts.AccountManager;

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
		config.launcherDir = obj.get("launcherDir").getAsString();

		JsonArray indexArray = obj.get("packIndexes").getAsJsonArray();
		String[] packIndex = new String[indexArray.size()];
		for (int i = 0; i < indexArray.size(); i++) {
			packIndex[i] = indexArray.get(i).getAsString();
		}
		config.packIndexes = packIndex;

		config.mcWidth = obj.get("mcWidth").getAsInt();
		config.mcHeight = obj.get("mcHeight").getAsInt();
		config.username = obj.get("username").getAsString();
		config.offline = obj.get("offline").getAsBoolean();

		/*
		Account Manager
		 */
//		JsonArray accountArray = obj.get("accounts").getAsJsonArray();
//		for (int i = 0; i < accountArray.size(); i++) {
////			AccountManager.getInstance().addAccount(new Account(accountArray.get(i).getAsString()));
//			JsonObject accountObj = accountArray.get(i).getAsJsonObject();
//
//			boolean active = accountObj.get("active").getAsBoolean();
//
//			Account account = new Account(accountObj.get("username").getAsString());
//
//			AccountManager.getInstance().addAccount(account);
//			if (active) {
//				AccountManager.getInstance().setSelectedAccount(account);
//			}
//		}

		return config;
	}

}
