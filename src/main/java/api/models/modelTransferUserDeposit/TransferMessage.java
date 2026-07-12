package api.models.modelTransferUserDeposit;

import lombok.Getter;

@Getter
public enum TransferMessage {
    APPROVED("Transfer approved and processed immediately"),
    MANUAL_REVIEW("Transfer requires manual review"),
    ADDITIONAL_VERIFICATION("Additional verification required");

    private final String msg;

    TransferMessage(String msg) {
        this.msg = msg;
    }
}