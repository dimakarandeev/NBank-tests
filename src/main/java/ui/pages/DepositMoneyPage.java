package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositMoneyPage extends BasePage<DepositMoneyPage> {

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

    public DepositMoneyPage checkDepositMoneyAccount(String numberAccount, String sumExpected) {
        List<SelenideElement> matchingOptions = selectedAccountSender
                .shouldBe(Condition.visible, Condition.enabled)
                .getOptions()
                .stream()
                .filter(opt -> opt.getText().startsWith(numberAccount))
                .toList();

        int count = matchingOptions.size();
        assertEquals(1, count, "Ожидается ровно 1 опция, но найдено: " + count);

        String foundText = matchingOptions.get(0).getText();

        String fullExpected = String.format(DepositMoneyBalance.DEPOSIT_MONEY_BALANCE.getMoneyBalance(), numberAccount, sumExpected);
        assertEquals(
                String.format(DepositMoneyBalance.DEPOSIT_MONEY_BALANCE.getMoneyBalance(), numberAccount, fullExpected),
                foundText,
                "Депозит '" + foundText + "' не совпадает с ожидаемым '" + fullExpected + "'");
        return this;
    }
}
