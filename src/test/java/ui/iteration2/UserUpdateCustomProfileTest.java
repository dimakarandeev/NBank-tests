package ui.iteration2;

import com.codeborne.selenide.Condition;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

public class UserUpdateCustomProfileTest extends BaseUiTest {

    String newCorrectNameUser = "John Smith";

    @Test
    @UserSession
    public void successChangeUserName() {
        new UserDashboard().open().editProfile();

        new EditProfilePage().getWelcomeTextUpdateProfile()
                .shouldBe(Condition.visible).shouldHave(Condition.text("✏\uFE0F Edit Profile"));

        new EditProfilePage()
                .updatedNameUser(newCorrectNameUser)
                .checkAlertMessageAndAccept(BankAlert.EDIT_PROFILE_SUCCESSFULLY.getMessage());

        new UserDashboard().open();
        new UserDashboard().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text(String.format("Welcome, %s!", newCorrectNameUser)));
    }

    @Test
    @UserSession
    public void changeUserNameWithInvalidData() {
        new UserDashboard().open().editProfile();

        new EditProfilePage().getWelcomeTextUpdateProfile()
                .shouldBe(Condition.visible).shouldHave(Condition.text("✏\uFE0F Edit Profile"));

        new EditProfilePage()
                .updateNameUserLessName()
                .checkAlertMessageAndAccept(BankAlert.EDIT_PROFILE_INVALID_DATA.getMessage());

        new UserDashboard().open();
        new UserDashboard().getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}