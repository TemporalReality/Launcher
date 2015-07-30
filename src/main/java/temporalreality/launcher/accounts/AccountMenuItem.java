package temporalreality.launcher.accounts;

import javafx.scene.control.CheckMenuItem;

/**
 * @author shadowfacts
 */
public class AccountMenuItem extends CheckMenuItem {

	private Account account;

	public AccountMenuItem(Account account) {
		this.account = account;

		setText(account.getUsername());
	}

	public Account getAccount() {
		return account;
	}
}
