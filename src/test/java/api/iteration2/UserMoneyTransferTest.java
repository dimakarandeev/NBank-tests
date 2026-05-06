package api.iteration2;

import api.BaseTest;
import models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class UserMoneyTransferTest extends BaseTest {

    public static Stream<Arguments> transferInvalidData () {
        return Stream.of(
                Arguments.of(-100.0, "Transfer amount must be at least 0.01"),
                Arguments.of(0.0, "Transfer amount must be at least 0.01"),
                Arguments.of(10001.0, "Transfer amount cannot exceed 10000")
        );
    }

    @MethodSource("transferInvalidData")
    @ParameterizedTest
    public void userMoneyTransferWithInvalidData (double invalidBalance, String errorValue) {
        double maxAllowBalance = 5000.0;

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        Integer senderAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestSender);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.failTransferMoneyAmongAccountsId(userRequestSender, senderAccountIdUser,
                receiverAccountIdUser, invalidBalance,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue));
    }

    public static Stream<Arguments> transferCorrectData () {
        return Stream.of(
                Arguments.of(0.01),
                Arguments.of(5000.0),
                Arguments.of(10000.0)
        );
    }

    @MethodSource("transferCorrectData")
    @ParameterizedTest
    public void userMoneyTransferWithCorrectData (double correctBalance) {
        double maxAllowBalance = 5000.0;

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        Integer senderAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestSender);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.successTransferMoneyAmongAccountsId(userRequestSender, senderAccountIdUser,
                receiverAccountIdUser, correctBalance);
    }

    @Test
    public void userTransferMoneyMoreDepositAmount () {
        double balance = 2500.0;
        double doubleBalance = balance * 2;

        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        Integer accountIdSenderUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);
        UserSteps.successDepositToUserAccount(userRequestSenderUser, accountIdSenderUser, balance);

        CreateUserRequest userRequestReceiverUser = AdminSteps.createUser();
        Integer accountIdReceiverUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiverUser);

        UserSteps.failTransferMoneyAmongAccountsId(userRequestSenderUser, accountIdSenderUser,
                accountIdReceiverUser, doubleBalance,
                ResponseSpecs.requestReturnsBadRequestWithText(
                        "Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    public void userTransferMoneyYourAccounts () {
        double balance = 1500.0;

        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        Integer accountIdSenderUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);
        Integer accountIdReceiverUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);

        UserSteps.successDepositToUserAccount(userRequestSenderUser, accountIdSenderUser, balance);

        UserSteps.successTransferMoneyAmongAccountsId(userRequestSenderUser, accountIdSenderUser,
                accountIdReceiverUser, balance);
    }
}