package ui.iteration2;

import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.UserDashboard;

public class UserMoneyTransferTest extends BaseUiTest {

    private final String maxCorrectDepositMoney = "5000.0";
    private final String depositInvalidData = "-100.0";

    @Test
    @UserSession(value = 2)
    public void userMoneyTransferWithInvalidData() {
        new UserDashboard().open().createNewAccount();

    }

    @Test
    public void userMoneyTransferWithCorrectData() {

    }

    @Test
    public void userTransferMoneyMoreDepositAmount() {

    }

    @Test
    @UserSession
    public void userTransferMoneyYourAccounts() {



    }
}