package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    DEPOSIT_MONEY_SUCCESSFULLY("✅ Successfully deposited $%s to account %s!"),
    DEPOSIT_MONEY_SELECT_ACCOUNT("❌ Please select an account."),
    EDIT_PROFILE_SUCCESSFULLY("✅ Name updated successfully!"),
    EDIT_PROFILE_INVALID_DATA("❌ Please enter a valid name."),
    EDIT_PROFILE_INVALID_AMOUNT("❌ Please enter a valid amount."),
    TRANSFER_AMOUNT_MIN_REQUIRED("❌ Error: Transfer amount must be at least 0.01"),
    TRANSFER_SUCCESSFUL("✅ Successfully transferred $%s to account %s!"),
    TRANSFER_INVALID_INSUFFICIENT_FUNDS("❌ Error: Invalid transfer: insufficient funds or invalid accounts");



    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}