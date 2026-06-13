package ui.iteration2;

import api.generators.RandomData;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import requests.steps.UserSteps;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositMoneyPage;
import ui.pages.MakeTransferMoney;
import ui.pages.UserDashboard;

public class UserMoneyTransferTest extends BaseUiTest {

    private final String maxCorrectDepositMoney = "5000.00";
    private final String doubleBalance = "10000.00";

    @Test
    @UserSession(value = 2, auth = 2)
    public void userMoneyTransferWithInvalidData() {
        String invalidDeposit = RandomData.getRandomNegativeDecimalDeposit().toString();
        String correctNameUser = RandomData.getRandomUserUpdateProfile();

        String userAccountReceiver = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));
        String userAccountSender = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(2));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        maxCorrectDepositMoney, userAccountSender))
                .goTo(UserDashboard.class)
                .createMakeTransfer()
                .goTo(MakeTransferMoney.class)
                .sendTransferMoney(
                        userAccountSender,
                        correctNameUser,
                        userAccountReceiver,
                        invalidDeposit)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_MIN_REQUIRED.getMessage())
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccountSender, maxCorrectDepositMoney);
    }

    @Test
    @UserSession(value = 2, auth = 2)
    public void userMoneyTransferWithCorrectData() {
        String depositNullData = "0.00";
        String correctNameUser = RandomData.getRandomUserUpdateProfile();

        String userAccountReceiver = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));
        String userAccountSender = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(2));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        maxCorrectDepositMoney, userAccountSender))
                .goTo(UserDashboard.class)
                .createMakeTransfer()
                .goTo(MakeTransferMoney.class)
                .sendTransferMoney(
                        userAccountSender,
                        correctNameUser,
                        userAccountReceiver,
                        maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.TRANSFER_SUCCESSFUL.getMessage(),
                        maxCorrectDepositMoney, userAccountReceiver))
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccountSender, depositNullData);
    }

    @Test
    @UserSession(value = 2, auth = 2)
    public void userTransferMoneyMoreDepositAmount() {
        String correctNameUser = RandomData.getRandomUserUpdateProfile();
        String userAccountReceiver = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));
        String userAccountSender = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(2));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        maxCorrectDepositMoney, userAccountSender))
                .goTo(UserDashboard.class)
                .createMakeTransfer()
                .goTo(MakeTransferMoney.class)
                .sendTransferMoney(
                        userAccountSender,
                        correctNameUser,
                        userAccountReceiver,
                        doubleBalance)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_INVALID_INSUFFICIENT_FUNDS.getMessage())
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccountSender, maxCorrectDepositMoney);
    }

    @Test
    @UserSession
    public void userTransferMoneyYourAccounts() {
        String depositNullData = "0.00";
        String correctNameUser = RandomData.getRandomUserUpdateProfile();
        String userAccountSender = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));
        String userAccountReceiver = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccountSender, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        maxCorrectDepositMoney, userAccountSender))
                .goTo(UserDashboard.class)
                .createMakeTransfer()
                .goTo(MakeTransferMoney.class)
                .sendTransferMoney(
                        userAccountSender,
                        correctNameUser,
                        userAccountReceiver,
                        maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.TRANSFER_SUCCESSFUL.getMessage(),
                        maxCorrectDepositMoney, userAccountReceiver))
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccountSender, depositNullData);
    }
}