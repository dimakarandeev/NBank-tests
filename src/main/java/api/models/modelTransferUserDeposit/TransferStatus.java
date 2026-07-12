package api.models.modelTransferUserDeposit;

import lombok.Getter;

@Getter
public enum TransferStatus {
    SUCCESS("SUCCESS"),
    APPROVED("APPROVED"),
    BLOCKED("BLOCKED"),
    REVIEW_REQUIRED("REVIEW_REQUIRED"),
    MANUAL_REVIEW_REQUIRED("MANUAL_REVIEW_REQUIRED"),
    VERIFICATION_REQUIRED("VERIFICATION_REQUIRED");

    private final String status;

    TransferStatus(String status) {
        this.status = status;
    }
}