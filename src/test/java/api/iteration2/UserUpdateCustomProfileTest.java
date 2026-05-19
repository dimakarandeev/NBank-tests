package api.iteration2;

import api.BaseTest;
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

public class UserUpdateCustomProfileTest extends BaseTest {

    @Test
    public void successChangeUserName() {
        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);

        UserSteps.successUpdateCustomerProfile(userRequestSenderUser, "John Smith");
    }

    public static Stream<Arguments> nameInvalidData() {
        return Stream.of(
                Arguments.of("JohnSmith"),
                Arguments.of("Иван Иванов"),
                Arguments.of("John Smith Jr"),
                Arguments.of("John! Smith"),
                Arguments.of("John Smit@h"),
                Arguments.of("John. Smith"),
                Arguments.of(" John Smith "),
                Arguments.of("John Smit3h")
        );
    }

    @MethodSource("nameInvalidData")
    @ParameterizedTest
    public void changeUserNameWithInvalidData(String invalidName) {
        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        UserSteps.createAccountsAndGetAccountsId(userRequestSenderUser);

        UserSteps.failUpdateCustomerProfile(userRequestSenderUser, invalidName,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.PERSON_NAME_VALIDATION_ERROR.toString()));
    }
}