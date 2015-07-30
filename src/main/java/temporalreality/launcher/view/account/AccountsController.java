package temporalreality.launcher.view.account;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import temporalreality.launcher.accounts.Account;

/**
 * @author shadowfacts
 */
public class AccountsController {

	@FXML
	private TableView<Account> accountsTable;

	@FXML
	private TableColumn<Account, String> usernameColumn;

	@FXML
	private TextField username;

	@FXML
	private Button add;

	@FXML
	private Button save;

	@FXML
	private Button activate;

	@FXML
	private Button delete;

	@FXML
	private void initialize() {

	}

	@FXML
	private void addPressed() {

	}

	@FXML
	private void savePressed() {

	}

	@FXML
	private void activatePressed() {

	}

	@FXML
	private void deletePressed() {

	}

	private void update(Account account) {

	}


//	@FXML
//	private TableView<Account> accountTable;
//
//	@FXML
//	private TableColumn<Account, String> usernameColumn;
//
//	@FXML
//	private TextField username;
//
//	@FXML
//	private Button save;
//
//	@FXML
//	private Button activate;
//
//	@FXML
//	private Button delete;
//
//	@FXML
//	private void initialize() {
//		accountTable.setItems(FXCollections.observableArrayList(AccountManager.getInstance().getAccounts()));
//
//		usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
//
//		accountTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
//			update(newValue);
//		}));
//
//		update(null);
//	}
//
//	@FXML
//	private void savePressed() {
//		Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
//		if (selectedAccount != null) {
//			selectedAccount.setUsername(username.getText());
//		} else {
//			Account account = new Account(username.getText());
//			AccountManager.getInstance().addAccount(account);
//			accountTable.getItems().add(account);
//			accountTable.getSelectionModel().select(account);
//		}
//
//		update(accountTable.getSelectionModel().getSelectedItem());
////		accountTable.setItems(FXCollections.observableArrayList(AccountManager.getInstance().getAccounts()));
//	}
//
//	@FXML
//	private void activatePressed() {
//		AccountManager.getInstance().setSelectedAccount(accountTable.getSelectionModel().getSelectedItem());
//	}
//
//	@FXML
//	private void deletePressed() {
//		AccountManager.getInstance().getAccounts().remove(accountTable.getSelectionModel().getSelectedItem());
//
//		accountTable.setItems(FXCollections.observableArrayList(AccountManager.getInstance().getAccounts()));
//	}
//
//	private void update(Account account) {
//		if (account != null) {
//			username.setText(account.getUsername());
//		} else {
//			username.setText("");
//		}
//
//		updateButtons(account);
//	}
//
//	private void updateButtons(Account account) {
//		if (account != null) {
//			save.setDisable(false);
////			activate.setDisable(!AccountManager.getInstance().getSelectedAccount().equals(account));
//
//			Account selectedAccount = AccountManager.getInstance().getSelectedAccount();
//			if (selectedAccount != null) {
//				activate.setDisable(!selectedAccount.equals(account));
//			} else {
//				activate.setDisable(false);
//			}
//
//
//			delete.setDisable(false);
//		} else {
//			activate.setDisable(true);
//			delete.setDisable(true);
//		}
//	}

}
