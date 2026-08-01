package api.iteration2;

import api.BaseTest;
import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.AddUserDepositRequest;
import api.models.AddUserDepositResponse;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.modelUpdateCustomerProfile.Account;
import api.models.modelUpdateCustomerProfile.GetCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.DataBaseSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.qameta.allure.Issue;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import api.specs.BankAPIAlert;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Long accountId = createAccountResponse.getId();

        new ValidatedCrudRequester<AddUserDepositResponse>
                (RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.DEPOSIT,
                        ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountId)
                        .balance(balance)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountId + " не найден"));

        assertEquals(balance, account.getBalance(), "Баланс аккаунта " + accountId + " должен измениться");

//        AccountDao accountDao = DataBaseSteps.getAccountById(addUserDepositResponse.getId());
//        DaoAndModelAssertions.assertThat(addUserDepositResponse, accountDao).match();
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(-100.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage()),
                Arguments.of(0.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage())
                //Arguments.of(5010.0, BankAPIAlert.DEPOSIT_AMOUNT_MAX_EXCEEDED.getMessage())
        );
    }


    @Issue("#2342342")
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
                .post();

        Long accountId = createAccountResponse.getId();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.DEPOSIT,
                        ResponseSpecs.requestReturnsSCBADREQUESTWithText(errorValue))
                .post(AddUserDepositRequest.builder()
                        .id(accountId)
                        .balance(balance)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountId + " не найден"));

        assertEquals(0, account.getBalance(), "Баланс аккаунта " + accountId + " не должен измениться");

//        AccountDao accountDao = DataBaseSteps.getAccountById(addUserDepositResponse.getId());
//        DaoAndModelAssertions.assertThat(addUserDepositResponse, accountDao).match();
    }

    @Test
    public void userAddDepositOtherUser() {
        double deposit = RandomData.getRandomPositiveDecimalDeposit();

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Long accountIdSender = createAccountSenderResponse.getId();

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Long accountIdReceiver = createAccountReceiverResponse.getId();

         new CrudRequester(RequestSpecs.authAsUser(userRequestSender.getUsername(), userRequestSender.getPassword()),
                        Endpoint.DEPOSIT,
                        ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()))
                .post(AddUserDepositRequest.builder()
                        .id(accountIdReceiver)
                        .balance(deposit)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecificationSender,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountIdSender))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountIdSender + " не найден"));

        assertEquals(0, account.getBalance(), "Баланс аккаунта " + accountIdSender + " не должен измениться");

//        AccountDao accountDao = DataBaseSteps.getAccountById(addUserDepositResponse.getId());
//        DaoAndModelAssertions.assertThat(addUserDepositResponse, accountDao).match();
    }

    @Test
    public void userAddDepositNotExistUser() {
        double deposit = RandomData.getRandomPositiveDecimalDeposit();

        CreateUserRequest userRequest = AdminSteps.createUser();
        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequest.getUsername(), userRequest.getPassword());

        CreateAccountResponse createAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Long accountId = createAccountResponse.getId();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.DEPOSIT,
                        ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()))
                .post(AddUserDepositRequest.builder()
                        .id(-Math.abs(accountId))
                        .balance(deposit)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountId + " не найден"));

        assertEquals(0, account.getBalance(), "Баланс аккаунта " + accountId + " не должен измениться");

//        AccountDao accountDao = DataBaseSteps.getAccountById(addUserDepositResponse.getId());
//        DaoAndModelAssertions.assertThat(addUserDepositResponse, accountDao).match();
    }
}