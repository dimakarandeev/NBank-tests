package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;
import models.modelTransferUserDeposit.TransferUserDepositRequest;
import models.modelTransferUserDeposit.TransferUserDepositResponse;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileRequest;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileResponse;

@Getter
@AllArgsConstructor
public enum Endpoint {

    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            AddUserDepositRequest.class,
            AddUserDepositResponse.class
    ),

    TRANSFER(
            "/accounts/transfer",
            TransferUserDepositRequest.class,
            TransferUserDepositResponse.class
    ),

    CUSTOMER_PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}