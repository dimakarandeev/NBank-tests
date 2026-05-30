package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DepositMoneyPage extends BasePage<UserDashboard> {

    private SelenideElement buttonAddDeposit = $(Selectors.byText("\uD83D\uDCB5 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositMoneyPage addDepositMoney(String accountNumber, String depositMoney) {
        selectedAccountSender.shouldBe(Condition.visible, Condition.enabled)
                        .selectOptionContainingText(accountNumber);
        amountMoneyInput.sendKeys(depositMoney);
        buttonAddDeposit.shouldBe(Condition.visible, Condition.enabled).click();
        return this;
    }

    public DepositMoneyPage addDepositMoneyLessAccountSelection(String depositMoney) {
        amountMoneyInput.sendKeys(depositMoney);
        buttonAddDeposit.shouldBe(Condition.visible, Condition.enabled).click();
        return this;
    }
}
