package temporalreality.launcher.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
@Getter
@Setter
public class Mod {

	private String name;
	private ArrayList<String> authors = new ArrayList<>();
	private String url;
	private String downloadUrl;
	private String fileName;
	private Side side = Side.BOTH;

	@Override
	public String toString() {
		return name;
	}
}