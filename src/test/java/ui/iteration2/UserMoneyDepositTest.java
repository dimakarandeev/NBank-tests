package ui.iteration2;

import api.models.CreateAccountResponse;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import requests.steps.UserSteps;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositMoneyPage;
import ui.pages.UserDashboard;

import java.util.List;

public class UserMoneyDepositTest extends BaseUiTest {

    private final String maxCorrectDepositMoney = "5000.00";
    private final String depositInvalidData = "-100.0";
    private final String depositNullData = "0.00";

    @Test
    @UserSession
    public void userAddDepositWithCorrectData() {

        new UserSteps().createAccounts(SessionStorage.getUser(1));
        String userAccount = SessionStorage.getSteps().getAllAccounts().get(0).getAccountNumber();

        new UserDashboard()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccount, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        maxCorrectDepositMoney, userAccount))
                .goTo(UserDashboard.class)
                .checkWelcomeText("Welcome, noname!")
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccount, maxCorrectDepositMoney);
    }

    @Test
    @UserSession
    public void userAddDepositWithInvalidData() {
        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();
        String userAccount = createdAccounts.get(0).getAccountNumber();

        new UserDashboard()
                .open()
                .createNewAccount()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccount, depositInvalidData)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage())
                .open()
                .checkDepositMoneyAccount(userAccount, depositNullData);


//        new UserDashboard().open().createNewAccount();
//
//        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();
//        String userAccount = createdAccounts.get(0).getAccountNumber();
//
//        new UserDashboard().createDepositMoney();
//        new DepositMoneyPage()
//                .addDepositMoney(userAccount, depositInvalidData)
//                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage());
//
//        new DepositMoneyPage().open();
//        new DepositMoneyPage().checkDepositMoneyAccount(
//                userAccount, depositNullData);
    }

    @Test
    @UserSession
    public void userAddDepositLessAccountSelection() {
        new UserDashboard()
                .open()
                .createNewAccount()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoneyLessAccountSelection(maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SELECT_ACCOUNT.getMessage());

//        new UserDashboard().open().createNewAccount();
//
//        new UserDashboard().createDepositMoney();
//        new DepositMoneyPage()
//                .addDepositMoneyLessAccountSelection(maxCorrectDepositMoney)
//                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SELECT_ACCOUNT.getMessage());
    }
}