package requests.steps;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.AddUserDepositRequest;
import models.CreateUserRequest;
import models.LoginUserRequest;
import models.comparison.ModelAssertions;
import models.modelTransferUserDeposit.TransferUserDepositRequest;
import models.modelTransferUserDeposit.TransferUserDepositResponse;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileRequest;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileResponse;
import org.hamcrest.Matchers;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static void createAccounts(CreateUserRequest userRequest) {
        new CrudRequester(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
                .header(AUTHORIZATION_HEADER, Matchers.notNullValue());
    }

    public static Integer createAccountsAndGetAccountsId(CreateUserRequest userRequest) {
        return new CrudRequester(authSpec(userRequest),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .response().getBody().jsonPath().getInt("id");
    }

    public static void successDepositToUserAccount(CreateUserRequest userRequest, Integer userId, double balance) {
        new CrudRequester(authSpec(userRequest),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(userId)
                        .balance(balance)
                        .build());
    }

    public static void failDepositToUserAccount(CreateUserRequest userRequest,
                                                Integer accountIdUser,
                                                double balance,
                                                ResponseSpecification responseSpecs) {
        new CrudRequester(authSpec(userRequest),
                Endpoint.DEPOSIT,
                responseSpecs)
                .post(AddUserDepositRequest.builder()
                        .id(accountIdUser)
                        .balance(balance)
                        .build());
    }

    public static void successTransferMoneyAmongAccountsId(CreateUserRequest userRequestSenderUser,
                                                           Integer accountIdSenderUser,
                                                           Integer accountIdReceiverUser,
                                                           Double amount) {
        RequestSpecification authSpec = authSpec(userRequestSenderUser);
        TransferUserDepositRequest transferUserDepositRequest =
                buildTransferUserDeposit(accountIdSenderUser, accountIdReceiverUser, amount);

        TransferUserDepositResponse transferUserDepositResponse =
                new ValidatedCrudRequester<TransferUserDepositResponse>(authSpec,
                        Endpoint.TRANSFER,
                        ResponseSpecs.requestReturnsOK())
                        .post(transferUserDepositRequest);

        ModelAssertions.assertThatModels(transferUserDepositRequest, transferUserDepositResponse).match();
    }

    public static void failTransferMoneyAmongAccountsId(CreateUserRequest userRequestSenderUser,
                                                        Integer accountIdSenderUser,
                                                        Integer accountIdReceiverUser,
                                                        Double amount,
                                                        ResponseSpecification responseSpecs) {
        RequestSpecification authSpec = authSpec(userRequestSenderUser);
        TransferUserDepositRequest transferUserDepositRequest =
                buildTransferUserDeposit(accountIdSenderUser, accountIdReceiverUser, amount);

        new CrudRequester(authSpec,
                Endpoint.TRANSFER,
                responseSpecs)
                .post(transferUserDepositRequest);
    }

    public static void failUpdateCustomerProfile(CreateUserRequest userRequest,
                                                 String name,
                                                 ResponseSpecification responseSpecs) {

        RequestSpecification authSpec = authSpec(userRequest);
        UpdateCustomerProfileRequest updateCustomerProfileRequest = buildUpdateProfileRequest(name);

        new CrudRequester(authSpec,
                Endpoint.CUSTOMER_PROFILE,
                responseSpecs)
                .put(updateCustomerProfileRequest);
    }

    public static void successUpdateCustomerProfile(CreateUserRequest userRequest, String name) {
        RequestSpecification authSpec = authSpec(userRequest);
        UpdateCustomerProfileRequest updateCustomerProfileRequest = buildUpdateProfileRequest(name);

        UpdateCustomerProfileResponse updateCustomerProfileResponse =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        authSpec,
                        Endpoint.CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .put(updateCustomerProfileRequest);

        ModelAssertions.assertThatModels(updateCustomerProfileRequest, updateCustomerProfileResponse).match();
    }

    private static UpdateCustomerProfileRequest buildUpdateProfileRequest(String name) {
        return UpdateCustomerProfileRequest.builder()
                .name(name)
                .build();
    }

    private static TransferUserDepositRequest buildTransferUserDeposit(Integer accountIdSenderUser,
                                                                       Integer accountIdReceiverUser,
                                                                       Double amount) {
        return TransferUserDepositRequest.builder()
                .senderAccountId(accountIdSenderUser)
                .receiverAccountId(accountIdReceiverUser)
                .amount(amount)
                .build();
    }

    private static RequestSpecification authSpec(CreateUserRequest userRequest) {
        return RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword());
    }
}
