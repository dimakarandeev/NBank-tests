package api.iteration2;

import api.BaseTest;
import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import models.AddUserDepositRequest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.BankAPIAlert;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class UserMoneyDepositTest extends BaseTest {

    public static Stream<Arguments> depositCorrectData() {
        return Stream.of(
                Arguments.of(0.01),
                Arguments.of(5000.0),
                Arguments.of(1000.0)
        );
    }

    @MethodSource("depositCorrectData")
    @ParameterizedTest
    public void userAddDepositWithCorrectData(double balance) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequest.getUsername(), userRequest.getPassword());

        CreateAccountResponse createAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountId = createAccountResponse.getId();
        new CrudRequester(requestSpecification,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountId)
                        .balance(balance)
                        .build());
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(-100.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage()),
                Arguments.of(0.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage()),
                Arguments.of(5001.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage())
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    public void userAddDepositWithInvalidData(double balance, String errorValue) {
        CreateUserRequest userRequest = AdminSteps.createUser();

        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequest.getUsername(), userRequest.getPassword());

        CreateAccountResponse createAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        Integer accountId = createAccountResponse.getId();

        new CrudRequester(requestSpecification,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue))
                .post(AddUserDepositRequest.builder()
                        .id(accountId)
                        .balance(balance)
                        .build());


    }

    @Test
    public void userAddDepositOtherUser() {
        double deposit = RandomData.getRandomRandomDecimalDeposit();

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        Integer accountIdReceiver = createAccountReceiverResponse.getId();
        new CrudRequester(requestSpecificationSender,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnForbidden(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()))
                .post(AddUserDepositRequest.builder()
                        .id(accountIdReceiver)
                        .balance(deposit)
                        .build());
    }

    @Test
    public void userAddDepositNotExistUser() {
        double deposit = RandomData.getRandomRandomDecimalDeposit();

        CreateUserRequest userRequest = AdminSteps.createUser();
        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequest.getUsername(), userRequest.getPassword());

        CreateAccountResponse createAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        Integer accountId = createAccountResponse.getId();

        new CrudRequester(requestSpecification,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnForbidden(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()))
                .post(AddUserDepositRequest.builder()
                        .id(-Math.abs(accountId))
                        .balance(deposit)
                        .build());
    }
}