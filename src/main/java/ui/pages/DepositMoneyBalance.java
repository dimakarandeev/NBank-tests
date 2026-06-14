package ui.pages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DepositMoneyBalance {

    DEPOSIT_MONEY_BALANCE("%s (Balance: $%s)");

    private final String moneyBalance;
}