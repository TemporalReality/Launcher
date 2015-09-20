package temporalreality.launcher.model;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
public class Mod {

	public String name;
	public ArrayList<String> authors = new ArrayList<>();
	public String url;
	public String downloadUrl;
	public String fileName;
	public Side side = Side.BOTH;

	@Override
	public String toString() {
		return name;
	}
}