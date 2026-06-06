package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class MakeTransferMoney extends BasePage<MakeTransferMoney> {

    private SelenideElement recipientNameInput = $(Selectors.byAttribute(
            "placeholder", "Enter recipient name"));
    private SelenideElement selectedAccountReceiver = $(Selectors.byAttribute(
            "placeholder", "Enter recipient account number"));
    private SelenideElement buttonSendTransfer = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private SelenideElement checkBoxConfirmCheck = $(Selectors.byId("confirmCheck"));

    @Override
    public String url() {
        return "/transfer";
    }

    public MakeTransferMoney sendTransferMoney(String accountNumberSender,
                                               String recipientName,
                                               String accountNumberReceiver,
                                               String depositMoney) {
        selectedAccountSender.shouldBe(Condition.visible, Condition.enabled)
                .selectOptionContainingText(accountNumberSender);
        recipientNameInput.shouldBe(Condition.visible, Condition.enabled)
                .sendKeys(recipientName);
        selectedAccountReceiver.shouldBe(Condition.visible, Condition.enabled)
                .sendKeys(accountNumberReceiver);
        amountMoneyInput.shouldBe(Condition.visible, Condition.enabled)
                .sendKeys(depositMoney);
        checkBoxConfirmCheck.shouldBe(Condition.visible, Condition.enabled)
                .click();
        buttonSendTransfer.shouldBe(Condition.visible, Condition.enabled)
                .click();
        return this;
    }
}