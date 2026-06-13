package ui.iteration2;

import api.generators.RandomData;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import requests.steps.UserSteps;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositMoneyPage;
import ui.pages.UserDashboard;

public class UserMoneyDepositTest extends BaseUiTest {

    @Test
    @UserSession
    public void userAddDepositWithCorrectData() {
        String correctDeposit = RandomData.getRandomPositiveDecimalDeposit().toString();
        String userAccount = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccount, correctDeposit)
                .checkAlertMessageAndAccept(String.format(BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(),
                        correctDeposit, userAccount))
                .goTo(UserDashboard.class)
                .checkWelcomeText(new UserDashboard().getWELCOME_TEXT_DEFAULT_USER_DASHBOARD())
                .goTo(DepositMoneyPage.class)
                .open()
                .checkDepositMoneyAccount(userAccount, correctDeposit);
    }

    @Test
    @UserSession
    public void userAddDepositWithInvalidData() {
        String depositNullData = "0.00";
        String invalidDeposit = RandomData.getRandomNegativeDecimalDeposit().toString();
        String userAccount = new UserSteps().createAccountsAndGetAccountsId(SessionStorage.getUser(1));

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoney(userAccount, invalidDeposit)
                .checkAlertMessageAndAccept(BankAlert.EDIT_PROFILE_INVALID_AMOUNT.getMessage())
                .open()
                .checkDepositMoneyAccount(userAccount, depositNullData);
    }

    @Test
    @UserSession
    public void userAddDepositLessAccountSelection() {
        String correctDeposit = RandomData.getRandomPositiveDecimalDeposit().toString();

        new UserDashboard()
                .open()
                .createDepositMoney()
                .goTo(DepositMoneyPage.class)
                .addDepositMoneyLessAccountSelection(correctDeposit)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SELECT_ACCOUNT.getMessage());
    }
}