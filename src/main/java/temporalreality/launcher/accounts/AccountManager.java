package temporalreality.launcher.accounts;

import java.util.ArrayList;

/**
 * @author shadowfacts
 */
public class AccountManager {

	private static AccountManager instance = new AccountManager();

	private ArrayList<Account> accounts = new ArrayList<>();
	private Account selectedAccount = null;

	private boolean offline;

	public void addAccount(Account account) {
		getAccounts().add(account);
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public Account getSelectedAccount() {
		return selectedAccount;
	}

	public void setSelectedAccount(Account selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public static AccountManager getInstance() {
		return instance;
	}
}
