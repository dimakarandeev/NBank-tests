package api.models.modelTransferUserDeposit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferUserDepositRequest extends BaseModel {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
}