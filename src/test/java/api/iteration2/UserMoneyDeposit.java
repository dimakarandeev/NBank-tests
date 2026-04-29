package api.iteration2;

import api.BaseTest;
import models.AddUserDepositRequest;
import models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class UserMoneyDeposit extends BaseTest {

    public static Stream<Arguments> depositCorrectData() {
        return Stream.of(
                // username field validation
                Arguments.of(0.01),
                Arguments.of(5000.0),
                Arguments.of(1000.0)
        );
    }

    @MethodSource("depositCorrectData")
    @ParameterizedTest
    public void userAddDepositWithCorrectData(double balance) {
        CreateUserRequest userRequest = AdminSteps.createUser();

        Integer userId = new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .response().getBody().jsonPath().getInt("id");

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(AddUserDepositRequest.builder()
                        .id(userId)
                        .balance(balance)
                        .build());
    }

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                // username field validation
                Arguments.of(-100.0, "username", "Deposit amount must be at least 0.01"),
                Arguments.of(0.0, "username", "Deposit amount must be at least 0.01"),
                Arguments.of(5001.0, "username", "Deposit amount cannot exceed 5000")
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    public void userAddDepositWithInvalidData(double balance, String errorKey, String errorValue) {
        CreateUserRequest userRequest = AdminSteps.createUser();

        Integer userId = new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null)
                .extract()
                .response().getBody().jsonPath().getInt("id");

        AddUserDepositRequest addUserDepositRequest = AddUserDepositRequest.builder()
                .id(userId)
                .balance(balance)
                .build();


        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(addUserDepositRequest);

    }

    @Test
    public void userAddDepositOtherUser() {
        // на чужой аккаунт
        // не существующий аккаунт
    }
}