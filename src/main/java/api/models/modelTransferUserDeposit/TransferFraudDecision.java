package api.models.modelTransferUserDeposit;

import lombok.Getter;

@Getter
public enum TransferFraudDecision {
    APPROVED("APPROVED"),
    BLOCKED("BLOCKED"),
    REVIEW_REQUIRED("REVIEW_REQUIRED"),
    VERIFICATION_REQUIRED("VERIFICATION_REQUIRED");

    private final String fraudDecision;

    TransferFraudDecision(String fraudDecision) {
        this.fraudDecision = fraudDecision;
    }
}
