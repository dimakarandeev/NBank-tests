package api.requests.skelethon;

import api.models.*;
import api.models.modelTransferUserDeposit.TransferUserDepositRequest;
import api.models.modelTransferUserDeposit.TransferUserDepositResponse;
import api.models.modelUpdateCustomerProfile.GetCustomerProfileResponse;
import api.models.modelUpdateCustomerProfile.UpdateCustomerProfileRequest;
import api.models.modelUpdateCustomerProfile.UpdateCustomerProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    UPDATE_CUSTOMER_PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    ),

    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    GET_CUSTOMER_PROFILE(
            "/customer/profile",
            BaseModel.class,
            GetCustomerProfileResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}