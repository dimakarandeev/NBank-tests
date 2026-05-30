package ui.iteration2;

import api.models.CreateAccountResponse;
import com.codeborne.selenide.Condition;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.DepositMoneyPage;
import ui.pages.UserDashboard;

import java.util.List;

public class UserMoneyDepositTest extends BaseUiTest {

    private final String maxCorrectDepositMoney = "5000.0";
    private final String depositInvalidData = "-100.0";

    @Test
    @UserSession
    public void userAddDepositWithCorrectData() {
        new UserDashboard().open().createNewAccount();

        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();
        String userAccount = createdAccounts.get(0).getAccountNumber();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccount, maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(String.format(
                        BankAlert.DEPOSIT_MONEY_SUCCESSFULLY.getMessage(), maxCorrectDepositMoney, userAccount));

        new UserDashboard().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }

    @Test
    @UserSession
    public void userAddDepositWithInvalidData() {
        new UserDashboard().open().createNewAccount();

        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();
        String userAccount = createdAccounts.get(0).getAccountNumber();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoney(userAccount, depositInvalidData)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_INVALID_DATA.getMessage());
    }

    @Test
    @UserSession
    public void userAddDepositLessAccountSelection() {
        new UserDashboard().open().createNewAccount();

        new UserDashboard().createDepositMoney();
        new DepositMoneyPage()
                .addDepositMoneyLessAccountSelection(maxCorrectDepositMoney)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_MONEY_SELECT_ACCOUNT.getMessage());
    }
}