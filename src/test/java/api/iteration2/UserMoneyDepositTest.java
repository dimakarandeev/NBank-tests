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
                Arguments.of(-100.0, "Deposit amount must be at least 0.01"),
                Arguments.of(0.0, "Deposit amount must be at least 0.01"),
                Arguments.of(5001.0, "Deposit amount cannot exceed 5000")
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
        double deposit = 100.0;
        CreateUserRequest userRequestSender = AdminSteps.createUser();
        UserSteps.createAccountsAndGetAccountsId(userRequestSender);

        CreateUserRequest userRequestReceiver = AdminSteps.createUser();
        Integer receiverAccountIdUser = UserSteps.createAccountsAndGetAccountsId(userRequestReceiver);

        UserSteps.failDepositToUserAccount(userRequestSender,
                receiverAccountIdUser,
                deposit,
                ResponseSpecs.requestReturnForbidden("Unauthorized access to account"));
    }

    // Необходимо уточнить у разработчика, должен возвращаться JSON в ответе или String
    @Test
    public void userAddDepositNotExistUser() {
        Integer idNotExistUser = -1;
        double deposit = 100.0;

        CreateUserRequest userRequest = AdminSteps.createUser();
        UserSteps.createAccountsAndGetAccountsId(userRequest);

        UserSteps.failDepositToUserAccount(userRequest,
                idNotExistUser,
                deposit,
                ResponseSpecs.requestReturnForbidden("Unauthorized access to account"));
    }
}