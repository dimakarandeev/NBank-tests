package ui.iteration2;

import api.generators.RandomData;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

public class UserUpdateCustomProfileTest extends BaseUiTest {

    @Test
    @UserSession
    public void successChangeUserName() {
        String correctNameUser = RandomData.getRandomUserUpdateProfile();

        new UserDashboard()
                .open()
                .editProfile()
                .goTo(EditProfilePage.class)
                .checkWelcomeTextUpProfile()
                .updatedNameUser(correctNameUser)
                .checkAlertMessageAndAccept(BankAlert.EDIT_PROFILE_SUCCESSFULLY.getMessage())
                .goTo(UserDashboard.class)
                .open()
                .checkWelcomeText(
                        String.format(new UserDashboard().getWELCOME_TEXT_CUSTOM_USER_DASHBOARD(), correctNameUser));
    }

    @Test
    @UserSession
    public void changeUserNameWithInvalidData() {
        new UserDashboard()
                .open()
                .editProfile()
                .goTo(EditProfilePage.class)
                .checkWelcomeTextUpProfile()
                .updateNameUserLessName()
                .checkAlertMessageAndAccept(BankAlert.EDIT_PROFILE_INVALID_DATA.getMessage())
                .goTo(UserDashboard.class)
                .open()
                .checkWelcomeText(new UserDashboard().getWELCOME_TEXT_DEFAULT_USER_DASHBOARD());
    }
}