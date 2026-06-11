package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {

    private final String WELCOME_TEXT = "✏️ Edit Profile";
    private final String SAVE_CHANGES_TEXT = "💾 Save Changes";

    private SelenideElement welcomeTextUpdateProfile = $(Selectors.byText(WELCOME_TEXT));
    private SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement buttonSaveChanges = $(Selectors.byText(SAVE_CHANGES_TEXT));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage updatedNameUser(String name) {
        newNameInput.shouldBe(Condition.visible, Condition.enabled).sendKeys(name);
        buttonSaveChanges.shouldBe(Condition.visible, Condition.enabled).click();
        return this;
    }

    public EditProfilePage updateNameUserLessName() {
        buttonSaveChanges.shouldBe(Condition.visible, Condition.enabled).click();
        return this;
    }

    public EditProfilePage checkWelcomeTextUpProfile() {
        String welcomeText = welcomeTextUpdateProfile.shouldBe(Condition.visible, Condition.enabled).getText();
        assertEquals(welcomeText, WELCOME_TEXT);
        return this;
    }
}