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
        Integer accountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequest);

        UserSteps.successDepositToUserAccount(userRequest, accountIdUser, balance);
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(-100.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage()),
                Arguments.of(0.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage()),
                Arguments.of(5001.0, BankAPIAlert.DEPOSIT_INVALID_ACCOUNT_AMOUNT.getMessage())
        );
    }

    // Необходимо уточнить у разработчика, должен возвращаться JSON в ответе или String
    @MethodSource("depositInvalidData")
    @ParameterizedTest
    public void userAddDepositWithInvalidData(double balance, String errorValue) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        Integer accountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequest);

        UserSteps.failDepositToUserAccount(userRequest, accountIdUser, balance,
                ResponseSpecs.requestReturnsBadRequestWithText(errorValue));
    }

    // Необходимо уточнить у разработчика, должен возвращаться JSON в ответе или String
    @Test
    public void userAddDepositOtherUser() {
        double deposit = RandomData.getRandomRandomDecimalDeposit();

        CreateUserRequest userRequestSender = AdminSteps.createUser();
        UserSteps.createAccountsAndGetAccountsId(userRequestSender);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.failDepositToUserAccount(userRequestSender,
                receiverAccountIdUser,
                deposit,
                ResponseSpecs.requestReturnForbidden(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()));
    }

    // Необходимо уточнить у разработчика, должен возвращаться JSON в ответе или String
    @Test
    public void userAddDepositNotExistUser() {
        double deposit = RandomData.getRandomRandomDecimalDeposit();

        CreateUserRequest userRequest = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequest);

        UserSteps.failDepositToUserAccount(userRequest,
                -Math.abs(receiverAccountIdUser),
                deposit,
                ResponseSpecs.requestReturnForbidden(BankAPIAlert.UNAUTHORIZED_ACCESS_TO_ACCOUNT.getMessage()));
    }
}