package models.modelUpdateCustomerProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    private int id;
    private double amount;
    private String type;
    private String timestamp; // строка вида "Fri May 01 12:54:39 UTC 2026"
    private int relatedAccountId;
}