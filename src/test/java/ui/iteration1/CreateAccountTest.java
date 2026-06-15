package ui.iteration1;

import api.models.CreateAccountResponse;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import requests.steps.UserSteps;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {
    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        new UserDashboard().open().createNewAccount();

        List<CreateAccountResponse> createdAccounts = UserSteps.findAllAccountUser();

        assertThat(createdAccounts).hasSize(1);

        new UserDashboard().checkAlertMessageAndAccept
                (BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.get(0).getAccountNumber());

        assertThat(createdAccounts.get(0).getBalance()).isZero();
    }
}