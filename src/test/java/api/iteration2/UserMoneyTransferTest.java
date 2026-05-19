package api.iteration2;

import api.BaseTest;
import generators.RandomData;
import models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.BankAPIAlert;
import specs.ResponseSpecs;

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
        Integer senderAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestSender);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.failTransferMoneyAmongAccountsId(userRequestSender, senderAccountIdUser,
                receiverAccountIdUser, invalidBalance,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue));
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
        Integer senderAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestSender);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);
        UserSteps.successDepositToUserAccount(userRequestSender, senderAccountIdUser, maxAllowBalance);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.successTransferMoneyAmongAccountsId(userRequestSender, senderAccountIdUser,
                receiverAccountIdUser, correctBalance);
    }

    @Test
    public void userTransferMoneyMoreDepositAmount() {
        double balance = RandomData.getRandomRandomDecimalDeposit();
        double doubleBalance = balance * 2;

        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        Integer accountIdSenderUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);
        UserSteps.successDepositToUserAccount(userRequestSenderUser, accountIdSenderUser, balance);

        CreateUserRequest userRequestReceiverUser = AdminSteps.createUser();
        Integer accountIdReceiverUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiverUser);

        UserSteps.failTransferMoneyAmongAccountsId(userRequestSenderUser, accountIdSenderUser,
                accountIdReceiverUser, doubleBalance,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.TRANSFER_VALIDATION_ERROR.getMessage()));
    }

    @Test
    public void userTransferMoneyYourAccounts() {
        double balance = RandomData.getRandomRandomDecimalDeposit();

        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        Integer accountIdSenderUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);
        Integer accountIdReceiverUser = UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);

        UserSteps.successDepositToUserAccount(userRequestSenderUser, accountIdSenderUser, balance);

        UserSteps.successTransferMoneyAmongAccountsId(userRequestSenderUser, accountIdSenderUser,
                accountIdReceiverUser, balance);
    }
}