package temporalreality.launcher.auth;

/**
 * @author shadowfacts
 */
public class PasswordResult {

	public String password;
	public boolean offline;

	public PasswordResult(String password, boolean offline) {
		this.password = password;
		this.offline = offline;
	}

}
