package api.iteration2;

import api.BaseTest;
import io.restassured.specification.RequestSpecification;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.comparison.ModelAssertions;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileRequest;
import models.modelUpdateCustomerProfile.UpdateCustomerProfileResponse;
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

public class UserUpdateCustomProfileTest extends BaseTest {

    @Test
    public void successChangeUserName() {
        String updateProfileName = "John Smith";

        CreateUserRequest userRequestSenderUser = AdminSteps.createUser();
        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequestSenderUser.getUsername(), userRequestSenderUser.getPassword());

        new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        UpdateCustomerProfileRequest updateCustomerProfileRequest =
                UpdateCustomerProfileRequest.builder()
                        .name(updateProfileName)
                        .build();

        UpdateCustomerProfileResponse updateCustomerProfileResponse =
                new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .put(updateCustomerProfileRequest);

        ModelAssertions.assertThatModels(updateCustomerProfileRequest, updateCustomerProfileResponse).match();
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

        RequestSpecification requestSpecification = RequestSpecs.authAsUser(
                userRequestSenderUser.getUsername(), userRequestSenderUser.getPassword());
        new ValidatedCrudRequester<CreateAccountResponse>(
                requestSpecification,
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        new CrudRequester(requestSpecification,
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.PERSON_NAME_VALIDATION_ERROR.getMessage()))
                .put(UpdateCustomerProfileRequest.builder()
                        .name(invalidName)
                        .build());
    }
}