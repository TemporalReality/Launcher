package temporalreality.launcher.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import temporalreality.launcher.TRLauncher;
import temporalreality.launcher.accounts.Account;
import temporalreality.launcher.accounts.AccountManager;
import temporalreality.launcher.accounts.AccountMenuItem;
import temporalreality.launcher.config.ConfigManager;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.stream.Collectors;

/**
 * @author shadowfacts
 */
public class RootController {

	@FXML
	private MenuBar menuBar;

	@FXML
	private void initialize() {
		if (System.getProperty("os.name").startsWith("Mac")) {
			menuBar.setUseSystemMenuBar(true);
		}


		for (Menu m : menuBar.getMenus()) {
			if (m.getText().equalsIgnoreCase("help")) {

				m.getItems().get(0).setOnAction(event -> {
					TRLauncher.log.info("Showing about dialog");

					TRLauncher.getLauncher().showAboutDialog();
				});

			} else if (m.getText().equalsIgnoreCase("file")) {
				MenuItem configureItem = new MenuItem("Configure...");
				configureItem.setOnAction(event -> {
					TRLauncher.log.info("Showing config dialog");

					TRLauncher.getLauncher().showConfigDialog();
				});

//				MenuItem signOutItem = new MenuItem("Sign Out");
//				signOutItem.setOnAction(event1 -> {
//					TRLauncher.log.info("Signing out");
//
//					ConfigManager.getInstanceConfig().username = "";
//					ConfigManager.getInstance().save();
//				});

				m.getItems().add(0, configureItem);
//				m.getItems().add(1, signOutItem);
				m.getItems().add(1, new SeparatorMenuItem());

				m.getItems().get(2).setOnAction(event -> {
					TRLauncher.log.info("Goodbye");
					System.exit(0);
				});
			}
		}


		Menu accountsMenu = new Menu("Accounts");
		menuBar.getMenus().add(2, accountsMenu);
//		initAccountsMenu(accountsMenu);
		MenuItem account = new MenuItem(ConfigManager.getInstanceConfig().username);

		CheckMenuItem offline = new CheckMenuItem("Offline");
		offline.setSelected(ConfigManager.getInstanceConfig().offline);
		offline.setOnAction(event -> {
			ConfigManager.getInstanceConfig().offline = offline.isSelected();
			ConfigManager.getInstance().save();
		});

		MenuItem set = new MenuItem("Set Account...");
		set.setOnAction(event -> {
			String username = TRLauncher.getLauncher().showAddAccountDialog();
			if (username != null && !username.equals("")) {
				ConfigManager.getInstanceConfig().username = username;
				ConfigManager.getInstance().save();
				account.setText(username);
			}
		});

		accountsMenu.getItems().addAll(account, new SeparatorMenuItem(), offline, set);

//		accountsMenu.getItems()

//		for (Account a : AccountManager.getInstance().getAccounts()) {
//			CheckMenuItem accountItem = new CheckMenuItem(a.getUsername());
//
//			accountItem.setOnAction(event -> {
//				AccountManager.getInstance().setSelectedAccount(a);
//			});
//
//
//			accountsMenu.getItems().add(accountItem);
//		}
//
//		accountsMenu.getItems().add(new SeparatorMenuItem());
//
//		CheckMenuItem offline = new CheckMenuItem("Offline");
//		offline.setOnAction(event -> {
//			AccountManager.getInstance().setOffline(offline.isSelected());
//		});
//
//		accountsMenu.getItems().add(offline);
//		accountsMenu.getItems().add(new SeparatorMenuItem());
//
//		MenuItem manage = new MenuItem("Manage...");
//		manage.setOnAction(event -> {
//			TRLauncher.getLauncher().showAccountsDialog();
//		});
//		accountsMenu.getItems().add(manage);
//
//		MenuItem add = new MenuItem("Add Account...");
//		add.setOnAction(event -> {
//			TRLauncher.log.info("Showing account add dialog");
//			String username = TRLauncher.getLauncher().showAddAccountDialog();
//			if (username != null && !username.equals("")) {
//				AccountManager.getInstance().addAccount(new Account(username));
//			}
//		});
//		accountsMenu.getItems().add(add);

//		menuBar.getMenus().add(2, accountsMenu);
	}


//	private ArrayList<AccountMenuItem> accounts = null;
//	private ArrayList<AccountMenuItem> getAccounts() {
//		if (accounts == null) {
//			accounts = new ArrayList<>();
//			AccountManager.getInstance().getAccounts().stream().forEach(account -> {
//				AccountMenuItem item = new AccountMenuItem(account);
//				item.setSelected(AccountManager.getInstance().getSelectedAccount().equals(account));
//				item.setOnAction(event -> {
//					getAccounts().stream().filter(item2 -> !item.equals(item2)).forEach(item2 -> item2.setSelected(false));
//
//					ConfigManager.getInstance().save();
//				});
//				accounts.add(item);
//			});
//		}
//		return accounts;
//	}
//
//	private CheckMenuItem offline = null;
//	private CheckMenuItem getOffline() {
//		if (offline == null) {
//			offline = new CheckMenuItem("Offline");
//		}
//		return offline;
//	}

	private void initAccountsMenu(Menu menu) {
//		TRLauncher.log.info("Initializing Accounts menu");
//
//		getAccounts().stream().forEach(item -> menu.getItems().add(item));
//		menu.getItems().add(new SeparatorMenuItem());
//		menu.getItems().add(getOffline());
//
//		MenuItem add = new MenuItem("Add Account");
//		add.setOnAction(event -> {
//			TRLauncher.log.info("Showing add account dialog");
//			String username = TRLauncher.getLauncher().showAddAccountDialog();
//			if (username != null && !username.equals("")) {
//				AccountManager.getInstance().addAccount(new Account(username));
//			}
//			getAccounts().add(0, new AccountMenuItem(new Account(username)));
//
//			ConfigManager.getInstance().save();
//
//			updateAccountsMenu(menu);
//		});
//		menu.getItems().add(add);
//
//		MenuItem remove = new MenuItem("Remove current");
//		remove.setOnAction(event -> {
//
//			ArrayList<MenuItem> toRemove = new ArrayList<>();
//			menu.getItems().stream().filter(item -> item instanceof AccountMenuItem).forEach(item -> {
//				AccountMenuItem accountMenuItem = (AccountMenuItem) item;
//				if (accountMenuItem.getAccount().equals(AccountManager.getInstance().getSelectedAccount())) {
////					menu.getItems().remove(item);
//					toRemove.add(item);
//				}
//			});
//			toRemove.stream().forEach(item -> menu.getItems().remove(item));
//
//			AccountManager.getInstance().getAccounts().remove(AccountManager.getInstance().getSelectedAccount());
//			AccountManager.getInstance().setSelectedAccount(null);
//
//			ConfigManager.getInstance().save();
//
//			updateAccountsMenu(menu);
//		});
//		menu.getItems().add(remove);
	}

	private void updateAccountsMenu(Menu menu) {
//		getAccounts().stream().forEach(item -> {
//			item.setSelected(item.getAccount().equals(AccountManager.getInstance().getSelectedAccount()));
//		});
//		getAccounts().stream().filter(item -> !menu.getItems().contains(item)).forEach(item -> menu.getItems().add(0, item));
	}

}
