package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {

    private SelenideElement welcomeTextUpdateProfile = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement buttonSaveChanges = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

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
}