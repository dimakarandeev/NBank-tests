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
public class UpdateCustomerProfileRequest extends BaseModel {
    private String name;
}
