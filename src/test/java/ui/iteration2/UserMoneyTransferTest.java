package ui.iteration2;

import api.models.CreateAccountResponse;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.*;

import java.util.List;

public class UserMoneyTransferTest extends BaseUiTest {

    private final String maxCorrectDepositMoney = "5000.00";
    private final String depositInvalidData = "-100.0";
    private final String doubleBalance = "10000.00";

    @Test
    @UserSession(value = 2)
    public void userMoneyTransferWithInvalidData() {
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsReceiver = SessionStorage.getSteps().getAllAccounts();
        String userAccountReceiver = createdAccountsReceiver.get(0).getAccountNumber();


        BasePage.authAsUser(SessionStorage.getUser(2));
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsSender = SessionStorage.getSteps(2).getAllAccounts();
        String userAccountSender = createdAccountsSender.get(0).getAccountNumber();


        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(
                        BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(), maxCorrectDepositMoney, userAccountSender));


        new UserDashboard().createMakeTransfer();
        new MakeTransferMoney()
                .sendTransferMoney(
                        userAccountSender,
                        "John Cena",
                        userAccountReceiver,
                        depositInvalidData)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_MIN_REQUIRED.getMessage());


        new DepositMoneyPage().open();
        new DepositMoneyPage().checkDepositMoneyAccount(
                userAccountSender,
                String.format("%s (Balance: $%s)", userAccountSender, maxCorrectDepositMoney));
    }

    @Test
    @UserSession(value = 2)
    public void userMoneyTransferWithCorrectData() {
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsReceiver = SessionStorage.getSteps().getAllAccounts();
        String userAccountReceiver = createdAccountsReceiver.get(0).getAccountNumber();

        BasePage.authAsUser(SessionStorage.getUser(2));
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsSender = SessionStorage.getSteps(2).getAllAccounts();
        String userAccountSender = createdAccountsSender.get(0).getAccountNumber();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(
                        BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(), maxCorrectDepositMoney, userAccountSender));

        new UserDashboard().createMakeTransfer();
        new MakeTransferMoney()
                .sendTransferMoney(
                        userAccountSender,
                        "John Cena",
                        userAccountReceiver,
                        maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(
                        String.format(BankAlert.TRANSFER_SUCCESSFUL.getMessage(), maxCorrectDepositMoney, userAccountReceiver));

        new DepositMoneyPage().open();
        new DepositMoneyPage().checkDepositMoneyAccount(
                userAccountSender,
                String.format("%s (Balance: $0.00)", userAccountSender));
    }

    @Test
    @UserSession(value = 2)
    public void userTransferMoneyMoreDepositAmount() {
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsReceiver = SessionStorage.getSteps().getAllAccounts();
        String userAccountReceiver = createdAccountsReceiver.get(0).getAccountNumber();

        BasePage.authAsUser(SessionStorage.getUser(2));
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsSender = SessionStorage.getSteps(2).getAllAccounts();
        String userAccountSender = createdAccountsSender.get(0).getAccountNumber();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(
                        BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(), maxCorrectDepositMoney, userAccountSender));

        new UserDashboard().createMakeTransfer();
        new MakeTransferMoney()
                .sendTransferMoney(
                        userAccountSender,
                        "John Cena",
                        userAccountReceiver,
                        doubleBalance)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_INVALID_INSUFFICIENT_FUNDS.getMessage());

        new DepositMoneyPage().open();
        new DepositMoneyPage().checkDepositMoneyAccount(
                userAccountSender,
                String.format("%s (Balance: $%s)", userAccountSender, maxCorrectDepositMoney));
    }

    @Test
    @UserSession
    public void userTransferMoneyYourAccounts() {
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsSender = SessionStorage.getSteps().getAllAccounts();
        String userAccountSender = createdAccountsSender.get(0).getAccountNumber();

        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponse> createdAccountsReceiver = SessionStorage.getSteps().getAllAccounts();
        String userAccountReceiver = createdAccountsReceiver.get(0).getAccountNumber();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(
                        BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(), maxCorrectDepositMoney, userAccountSender));

        new UserDashboard().createMakeTransfer();
        new MakeTransferMoney()
                .sendTransferMoney(
                        userAccountSender,
                        "John Cena",
                        userAccountReceiver,
                        maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(
                        String.format(BankAlert.TRANSFER_SUCCESSFUL.getMessage(), maxCorrectDepositMoney, userAccountReceiver));

        new DepositMoneyPage().open();
        new DepositMoneyPage().checkDepositMoneyAccount(
                userAccountSender,
                String.format("%s (Balance: $0.00)", userAccountSender));
    }
}