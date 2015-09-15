package temporalreality.launcher.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		Map<String, String> map = new HashMap<String, String>();
		String key = null;
		for (String string: additionalInfo)
			if (key == null)
				key = string;
			else {
				map.put(key, string);
				key = null;
			}
		create(title, t, map);
	}

	private static void create(String title, Throwable t, Map<String, String> additionalInfo) {
		try {
			if (Boolean.parseBoolean(System.getProperty("temporalreality.launcher.errorreporting", "true"))) {
				title = title != null ? title : calculateHashCode(t) + ": " + t.toString();
				GHRepository repo = github.getUser("TemporalReality").getRepository("Automatic-Issue-Reporting");
				GHIssue issue = getIssue(title, repo);
				StringWriter w = new StringWriter();
				w.write("```");
				w.write(System.lineSeparator());
				if (issue == null) {
					t.printStackTrace(new PrintWriter(w));
					w.write(System.lineSeparator());
					w.write(System.lineSeparator());
					w.write(System.lineSeparator());
				}
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
				if (issue == null)
					issue = repo.createIssue(title).body(w.toString()).create();
				else
					issue.comment(w.toString());
				TRLauncher.log.error("An error occured, and has been reported automatically. If you would like to add any information, please go to "
						+ issue.getHtmlUrl()
						+ '.');
			} 
		} catch (Throwable t2) {
			t2.printStackTrace();
		}
	}

	private static GHIssue getIssue(String title, GHRepository repo) throws IOException {
		for (GHIssue issue: repo.getIssues(GHIssueState.OPEN))
			if (issue.getTitle().equals(title)) {
				return issue;
			}
		return null;
	}

	private static int calculateHashCode(Throwable t) {
		int result = 1;
		while (t != null) {
			result += result * 31 * t.getClass().getName().hashCode();
			for (StackTraceElement e: t.getStackTrace())
				result += result * 31 * (e.getLineNumber() + 1) * e.getClassName().toString().hashCode();
			t = t.getCause();
		}
		return result;
	}

	static {
		try {
			github = GitHub.connectUsingOAuth(new StringBuilder("bbebd3851b50509879db3ff26adc7ca77e76133d").reverse().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}