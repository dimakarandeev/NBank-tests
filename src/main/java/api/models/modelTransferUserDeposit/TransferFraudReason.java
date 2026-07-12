package api.models.modelTransferUserDeposit;

import lombok.Getter;

@Getter
public enum TransferFraudReason {
    LOW_RISK("Low risk transaction"),
    BLOCKED_BY_POLICY("Transaction blocked by fraud policy"),
    UNUSUAL_PATTERN("Unusual transaction pattern"),
    MANUAL_REVIEW_REQUIRED("Manual review required"),
    ADDITIONAL_VERIFICATION_NEEDED("Additional verification needed"),
    VERIFICATION_REQUIRED("Verification required");

    private final String fraudReason;

    TransferFraudReason(String fraudReason) {
        this.fraudReason = fraudReason;
    }
}