package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    
    private final String WELCOME_TEXT_DEFAULT_USER_DASHBOARD = "Welcome, noname!";
    private final String WELCOME_TEXT_CUSTOM_USER_DASHBOARD = "Welcome, %s!";
    
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement createNewDepositMoney = $(Selectors.byText("💰 Deposit Money"));
    private SelenideElement createNewTransferMoney = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement editProfile = $(Selectors.byClassName("user-info"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public UserDashboard createDepositMoney() {
        createNewDepositMoney.shouldBe(Condition.visible, Condition.enabled)
                .click();
        return this;
    }

    public UserDashboard createMakeTransfer() {
        createNewTransferMoney.shouldBe(Condition.visible, Condition.enabled)
                .click();
        return this;
    }

    public UserDashboard editProfile() {
        editProfile.click();
        return this;
    }

    public UserDashboard checkWelcomeText(String expectWelcomeText) {
        welcomeText
                .shouldBe(Condition.visible, Condition.enabled)
                .shouldHave(Condition.text(expectWelcomeText), Duration.ofSeconds(10));
        String actualWelcomeText = welcomeText.getText();
        assertEquals(actualWelcomeText, expectWelcomeText);
        return this;
    }
}