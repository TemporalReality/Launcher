package temporalreality.launcher.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.kohsuke.github.GitHub;

/**
 * @author CoolSquid
 */
public class Issues {

	private static final GitHub github;

	public static void create(String title, Throwable t, Map<String, String> additionalInfo) {
		if (Boolean.parseBoolean(System.getProperty("temporalreality.launcher.errorreporting", "true"))) {
			StringWriter w = new StringWriter();
			w.write("```");
			w.write(System.lineSeparator());
			t.printStackTrace(new PrintWriter(w));
			w.write(System.lineSeparator());
			w.write(System.lineSeparator());
			w.write(System.lineSeparator());
			w.write("Java version: ");
			w.write(System.getProperty("java.version"));
			w.write(System.lineSeparator());
			w.write("Java vendor: ");
			w.write(System.getProperty("java.vendor"));
			w.write(System.lineSeparator());
			w.write("OS: ");
			w.write(System.getProperty("os.name"));
			w.write(System.lineSeparator());
			w.write("Arch: ");
			w.write(System.getProperty("os.arch"));
			w.write(System.lineSeparator());
			if (additionalInfo != null)
				for (Entry<String, String> e: additionalInfo.entrySet()) {
					w.write(e.getKey());
					w.write(": ");
					w.write(e.getValue());
					w.write(System.lineSeparator());
				}
			w.write("```");
			try {
				github.getUser("TemporalReality").getRepository("Automatic-Issue-Reporting").createIssue(title != null ? title : t.toString()).body(w.toString()).create();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	static {
		try {
			github = GitHub.connectUsingOAuth(new StringBuilder("bbebd3851b50509879db3ff26adc7ca77e76133d").reverse().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}