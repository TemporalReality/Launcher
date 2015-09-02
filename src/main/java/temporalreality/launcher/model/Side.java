package temporalreality.launcher.model;

import com.google.gson.annotations.SerializedName;

public enum Side {

	@SerializedName("client")
	CLIENT,

	@SerializedName("server")
	SERVER,

	@SerializedName("both")
	BOTH;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}