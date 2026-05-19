package specs;

import lombok.Getter;

@Getter
public enum BankAPIAlert {

    UNAUTHORIZED_ACCESS_TO_ACCOUNT("Unauthorized access to account"),
    PERSON_NAME_VALIDATION_ERROR("Name must contain two words with letters only"),
    AMOUNT_TOO_SMALL("Deposit amount must be at least 0.01"),
    AMOUNT_TOO_LARGE("Deposit amount cannot exceed 5000");

    private final String message;

    BankAPIAlert(String message) {
        this.message = message;
    }
}