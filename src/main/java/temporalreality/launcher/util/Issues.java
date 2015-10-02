package temporalreality.launcher.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import temporalreality.launcher.TRLauncher;

/**
 * @author CoolSquid
 */
public class Issues {

	private static final GitHub github;

	public static void create(String title, Throwable t) {
		create(title, t, (Map<String, String>) null);
	}

	public static void create(String title, Throwable t, String... additionalInfo) {
		Map<String, String> map = new HashMap<>();
		String key = null;
		for (String string: additionalInfo) {
			if (key == null) {
				key = string;
			} else {
				map.put(key, string);
				key = null;
			}
		}
		create(title, t, map);
	}

	public static void create(String title, Throwable t, Map<String, String> additionalInfo) {
		new Thread(() -> {
			try {
				if (Boolean.parseBoolean(System.getProperty("temporalreality.launcher.errorreporting", "true"))) {
					String theTitle = title != null ? title : t.toString();
					GHRepository repo = github.getUser("TemporalReality").getRepository("Automatic-Issue-Reporting");
					Optional<GHIssue> optionalIssue = repo.getIssues(GHIssueState.OPEN).stream().filter(issue -> issue.getTitle().equals(theTitle)).findFirst();
					if (optionalIssue.isPresent()) {
						GHIssue issue = optionalIssue.get();
						StringWriter w = new StringWriter();

//				Stacktrace
						if (issue == null) {
							w.write("```");
							t.printStackTrace(new PrintWriter(w));
							w.write("```");
							w.write(System.lineSeparator());
							w.write(System.lineSeparator());
							w.write(System.lineSeparator());
						}

//				System details
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
						if (additionalInfo != null) {
							for (Entry<String, String> e : additionalInfo.entrySet()) {
								w.write(e.getKey());
								w.write(": ");
								w.write(e.getValue());
								w.write(System.lineSeparator());
							}
						}
						if (issue == null) {
							issue = repo.createIssue(theTitle.replace(System.getProperty("user.name"), "user")).body(w.toString().replace(System.getProperty("user.name"), "user")).create();
						} else {
							issue.comment(w.toString().replace(System.getProperty("user.name"), "use"));
						}
						TRLauncher.log.catching(t);
						TRLauncher.log.error("An error occurred, and has been reported automatically. If you would like to add any information, please go to "
								+ issue.getHtmlUrl()
								+ '.');
					}
				}
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
		}).start();
	}

	static {
		try {
			github = GitHub.connectUsingOAuth(new StringBuilder("bbebd3851b50509879db3ff26adc7ca77e76133d").reverse().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}