package models.modelUpdateCustomerProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCustomerProfileResponse extends BaseModel {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<Account> accounts;
}