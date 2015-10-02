package temporalreality.launcher.accounts;

import javafx.scene.control.CheckMenuItem;
import lombok.Getter;

/**
 * @author shadowfacts
 */
public class AccountMenuItem extends CheckMenuItem {

	@Getter private Account account;

	public AccountMenuItem(Account account) {
		this.account = account;

		setText(account.getUsername());
	}

}
