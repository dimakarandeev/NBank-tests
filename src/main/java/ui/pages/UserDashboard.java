package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
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

    public UserDashboard editProfile() {
        editProfile.click();
        return this;
    }
}