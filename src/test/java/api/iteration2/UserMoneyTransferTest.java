package api.iteration2;

import api.BaseTest;
import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.AddUserDepositRequest;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import api.models.modelTransferUserDeposit.TransferUserDepositRequest;
import api.models.modelTransferUserDeposit.TransferUserDepositResponse;
import api.models.modelUpdateCustomerProfile.Account;
import api.models.modelUpdateCustomerProfile.GetCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.DataBaseSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import specs.BankAPIAlert;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMoneyTransferTest extends BaseTest {

    public static Stream<Arguments> transferInvalidData() {
        double maxAllowBalance = 5000.0;
        return Stream.of(
                Arguments.of(-100.0, maxAllowBalance, BankAPIAlert.TRANSFER_AMOUNT_MIN_REQUIRED.getMessage()),
                Arguments.of(0.0, maxAllowBalance, BankAPIAlert.TRANSFER_AMOUNT_MIN_REQUIRED.getMessage()),
                Arguments.of(10001.0, maxAllowBalance, BankAPIAlert.TRANSFER_AMOUNT_MAX_EXCEEDED.getMessage())
        );
    }

    @MethodSource("transferInvalidData")
    @ParameterizedTest(name = "invalidBalance={0}, maxAllowBalance={1}")
    public void userMoneyTransferWithInvalidData(double invalidBalance, double maxAllowBalance, String errorValue) {
        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountIdSender = Integer.parseInt(String.valueOf(createAccountSenderResponse.getId()));
        new CrudRequester(requestSpecificationSender,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountIdSender)
                        .balance(maxAllowBalance)
                        .build());

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountIdReceiver = Integer.parseInt(String.valueOf(createAccountReceiverResponse.getId()));
        new CrudRequester(requestSpecificationSender,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue))
                .post(TransferUserDepositRequest.builder()
                        .senderAccountId(accountIdSender)
                        .receiverAccountId(accountIdReceiver)
                        .amount(invalidBalance)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecificationSender,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals((long) accountIdSender))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountIdSender + " не найден"));

        assertEquals(maxAllowBalance, account.getBalance(), "Баланс аккаунта " + accountIdSender + " не должен измениться");

        AccountDao accountDao = DataBaseSteps.getAccountById(account.getId());
        DaoAndModelAssertions.assertThat(account, accountDao).match();
    }

    public static Stream<Arguments> transferCorrectData() {
        double maxAllowBalance = 5000.0;
        return Stream.of(
                Arguments.of(0.01, maxAllowBalance),
                Arguments.of(5000.0, maxAllowBalance),
                Arguments.of(10000.0, maxAllowBalance)
        );
    }

    @MethodSource("transferCorrectData")
    @ParameterizedTest(name = "invalidBalance={0}, maxAllowBalance={1}")
    public void userMoneyTransferWithCorrectData(double correctBalance, double maxAllowBalance) {
        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountIdSender = Integer.parseInt(String.valueOf(createAccountSenderResponse.getId()));

        TestUtils.repeat(2, () ->
                new CrudRequester(requestSpecificationSender,
                        Endpoint.DEPOSIT,
                        ResponseSpecs.requestReturnsOK())
                        .post(AddUserDepositRequest.builder()
                                .id(accountIdSender)
                                .balance(maxAllowBalance)
                                .build()));

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        int accountIdReceiver = Integer.parseInt(String.valueOf(createAccountReceiverResponse.getId()));
        TransferUserDepositRequest transferUserDepositRequest = TransferUserDepositRequest.builder()
                .senderAccountId(accountIdSender)
                .receiverAccountId(accountIdReceiver)
                .amount(correctBalance)
                .build();

        TransferUserDepositResponse transferUserDepositResponse =
                new ValidatedCrudRequester<TransferUserDepositResponse>(requestSpecificationSender,
                        Endpoint.TRANSFER,
                        ResponseSpecs.requestReturnsOK())
                        .post(transferUserDepositRequest);

        ModelAssertions.assertThatModels(transferUserDepositRequest, transferUserDepositResponse).match();

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecificationReceiver,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals((long) accountIdReceiver))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountIdReceiver + " не найден"));

        assertEquals(correctBalance, account.getBalance(), "Баланс аккаунта " + accountIdReceiver + " должен измениться");

        AccountDao accountDao = DataBaseSteps.getAccountById(account.getId());
        DaoAndModelAssertions.assertThat(account, accountDao).match();
    }

    @Test
    public void userTransferMoneyMoreDepositAmount() {
        double balance = RandomData.getRandomPositiveDecimalDeposit();
        double doubleBalance = balance * 2;

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountIdSender = Integer.parseInt(String.valueOf(createAccountSenderResponse.getId()));
        new CrudRequester(requestSpecificationSender,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountIdSender)
                        .balance(balance)
                        .build());

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post();

        Integer accountIdReceiver = Integer.parseInt(String.valueOf(createAccountReceiverResponse.getId()));
        new CrudRequester(requestSpecificationSender,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage()))
                .post(TransferUserDepositRequest.builder()
                        .senderAccountId(accountIdSender)
                        .receiverAccountId(accountIdReceiver)
                        .amount(doubleBalance)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecificationSender,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(Long.valueOf(accountIdSender)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountIdSender + " не найден"));

        assertEquals(balance, account.getBalance(), "Баланс аккаунта " + accountIdSender + " не должен измениться");

        AccountDao accountDao = DataBaseSteps.getAccountById(account.getId());
        DaoAndModelAssertions.assertThat(account, accountDao).match();
    }

    @Test
    public void userTransferMoneyYourAccounts() {
        double balance = RandomData.getRandomPositiveDecimalDeposit();
        List<Integer> accountSenderResponseList = new ArrayList<>();

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());

        TestUtils.repeat(2, () -> {
            CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                    requestSpecificationSender,
                    Endpoint.ACCOUNTS,
                    ResponseSpecs.entityWasCreated())
                    .post();
            accountSenderResponseList.add(Integer.parseInt(String.valueOf(createAccountSenderResponse.getId())));
        });

        Integer accountIdSender = accountSenderResponseList.get(0);
        Integer accountIdReceiver = accountSenderResponseList.get(1);

        new CrudRequester(requestSpecificationSender,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountIdSender)
                        .balance(balance)
                        .build());

        TransferUserDepositRequest transferUserDepositRequest = TransferUserDepositRequest.builder()
                .senderAccountId(accountIdSender)
                .receiverAccountId(accountIdReceiver)
                .amount(balance)
                .build();

        TransferUserDepositResponse transferUserDepositResponse =
                new ValidatedCrudRequester<TransferUserDepositResponse>(requestSpecificationSender,
                        Endpoint.TRANSFER,
                        ResponseSpecs.requestReturnsOK())
                        .post(transferUserDepositRequest);

        ModelAssertions.assertThatModels(transferUserDepositRequest, transferUserDepositResponse).match();

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecificationSender,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        Account account = getCustomerProfileResponse.getAccounts().stream()
                .filter(acc -> acc.getId().equals(Long.valueOf(accountIdReceiver)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт " + accountIdReceiver + " не найден"));

        assertEquals(balance, account.getBalance(), "Баланс аккаунта " + accountIdReceiver + " должен измениться");

        AccountDao accountDao = DataBaseSteps.getAccountById(account.getId());
        DaoAndModelAssertions.assertThat(account, accountDao).match();
    }
}