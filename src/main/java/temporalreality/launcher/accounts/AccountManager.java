package temporalreality.launcher.accounts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
public class AccountManager {

	private static AccountManager instance = new AccountManager();

	@Getter private ArrayList<Account> accounts = new ArrayList<>();
	@Getter @Setter private Account selectedAccount = null;

	@Getter @Setter private boolean offline;

	public void addAccount(Account account) {
		getAccounts().add(account);
	}

	public static AccountManager getInstance() {
		return instance;
	}
}
