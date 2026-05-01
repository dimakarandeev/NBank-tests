package models.modelUpdateCustomerProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BaseModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerProfileResponse extends BaseModel {
    private Customer customer;
    private String message;
}