package specs;

import lombok.Getter;

@Getter
public enum BankAPIAlert {

    UNAUTHORIZED_ACCESS_TO_ACCOUNT("Unauthorized access to account"),
    PERSON_NAME_VALIDATION_ERROR("Name must contain two words with letters only"),
    DEPOSIT_AMOUNT_MIN_REQUIRED("Deposit amount must be at least 0.01"),
    DEPOSIT_AMOUNT_MAX_EXCEEDED("Deposit amount exceeds the 5000 limit"),
    DEPOSIT_INVALID_ACCOUNT_AMOUNT("Invalid account or amount"),
    TRANSFER_AMOUNT_MIN_REQUIRED("Transfer amount must be at least 0.01"),
    TRANSFER_AMOUNT_MAX_EXCEEDED("Transfer amount cannot exceed 10000"),
    TRANSFER_VALIDATION_ERROR("Invalid transfer: insufficient funds or invalid accounts");

    private final String message;

    BankAPIAlert(String message) {
        this.message = message;
    }
}