package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    DEPOSIT_MONEY_SUCCESSFULLY("✅ Successfully deposited $%s to account %s!"),
    DEPOSIT_MONEY_INVALID_DATA("❌ Please enter a valid amount."),
    DEPOSIT_MONEY_SELECT_ACCOUNT("❌ Please select an account."),
    EDIT_PROFILE_SUCCESSFULLY("✅ Name updated successfully!"),
    EDIT_PROFILE_INVALID_DATA("❌ Please enter a valid name.");


    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}