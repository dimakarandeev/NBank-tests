package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddUserDepositResponse extends BaseModel {
    private Integer id;
    private String accountNumber;
    private Integer balance;
    private List<String> transactions;
}