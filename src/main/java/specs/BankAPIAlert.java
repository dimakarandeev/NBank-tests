package specs;

import lombok.Getter;

@Getter
public enum BankAPIAlert {

    UNAUTHORIZED_ACCESS_TO_ACCOUNT("Unauthorized access to account"),
    PERSON_NAME_VALIDATION_ERROR("Name must contain two words with letters only"),
    DEPOSIT_INVALID_ACCOUNT_AMOUNT("Invalid account or amount"),
    TRANSFER_VALIDATION_ERROR("Invalid transfer: insufficient funds or invalid accounts");

    private final String message;

    BankAPIAlert(String message) {
        this.message = message;
    }
}