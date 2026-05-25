package api.iteration2;

import api.BaseTest;
import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import models.AddUserDepositRequest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.comparison.ModelAssertions;
import models.modelTransferUserDeposit.TransferUserDepositRequest;
import models.modelTransferUserDeposit.TransferUserDepositResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserMoneyTransferTest extends BaseTest {

    public static Stream<Arguments> transferInvalidData() {
        double maxAllowBalance = 5000.0;
        return Stream.of(
                Arguments.of(-100.0, maxAllowBalance, BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage()),
                Arguments.of(0.0, maxAllowBalance, BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage()),
                Arguments.of(10001.0, maxAllowBalance, BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage())
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
                .post(null);

        Integer accountIdSender = createAccountSenderResponse.getId();
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
                .post(null);

        Integer accountIdReceiver = createAccountReceiverResponse.getId();
        new CrudRequester(requestSpecificationSender,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue))
                .post(TransferUserDepositRequest.builder()
                        .senderAccountId(accountIdSender)
                        .receiverAccountId(accountIdReceiver)
                        .amount(invalidBalance)
                        .build());
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
                .post(null);

        Integer accountIdSender = createAccountSenderResponse.getId();
        for (int i = 0; i < 2; i++) {
            new CrudRequester(requestSpecificationSender,
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOK())
                    .post(AddUserDepositRequest.builder()
                            .id(accountIdSender)
                            .balance(maxAllowBalance)
                            .build());
        }

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        RequestSpecification requestSpecificationReceiver = RequestSpecs.authAsUser(
                userRequestReceiver.getUsername(), userRequestReceiver.getPassword());
        CreateAccountResponse createAccountReceiverResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationReceiver,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        Integer accountIdReceiver = createAccountReceiverResponse.getId();
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
    }

    @Test
    public void userTransferMoneyMoreDepositAmount() {
        double balance = RandomData.getRandomRandomDecimalDeposit();
        double doubleBalance = balance * 2;

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());
        CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecificationSender,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        Integer accountIdSender = createAccountSenderResponse.getId();
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
                .post(null);

        Integer accountIdReceiver = createAccountReceiverResponse.getId();
        new CrudRequester(requestSpecificationSender,
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage()))
                .post(TransferUserDepositRequest.builder()
                        .senderAccountId(accountIdSender)
                        .receiverAccountId(accountIdReceiver)
                        .amount(doubleBalance)
                        .build());
    }

    @Test
    public void userTransferMoneyYourAccounts() {
        double balance = RandomData.getRandomRandomDecimalDeposit();
        List<Integer> accountSenderResponseList = new ArrayList<>();

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        RequestSpecification requestSpecificationSender = RequestSpecs.authAsUser(
                userRequestSender.getUsername(), userRequestSender.getPassword());

        for (int i = 0; i < 2; i++) {
            CreateAccountResponse createAccountSenderResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                    requestSpecificationSender,
                    Endpoint.ACCOUNTS,
                    ResponseSpecs.entityWasCreated())
                    .post(null);
            accountSenderResponseList.add(createAccountSenderResponse.getId());
        }

        new CrudRequester(requestSpecificationSender,
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(accountSenderResponseList.get(0))
                        .balance(balance)
                        .build());

        TransferUserDepositRequest transferUserDepositRequest = TransferUserDepositRequest.builder()
                .senderAccountId(accountSenderResponseList.get(0))
                .receiverAccountId(accountSenderResponseList.get(1))
                .amount(balance)
                .build();

        TransferUserDepositResponse transferUserDepositResponse =
                new ValidatedCrudRequester<TransferUserDepositResponse>(requestSpecificationSender,
                        Endpoint.TRANSFER,
                        ResponseSpecs.requestReturnsOK())
                        .post(transferUserDepositRequest);

        ModelAssertions.assertThatModels(transferUserDepositRequest, transferUserDepositResponse).match();
    }
}