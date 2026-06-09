package api.iteration2;

import api.BaseTest;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.comparison.ModelAssertions;
import api.models.modelUpdateCustomerProfile.GetCustomerProfileResponse;
import api.models.modelUpdateCustomerProfile.UpdateCustomerProfileRequest;
import api.models.modelUpdateCustomerProfile.UpdateCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import specs.BankAPIAlert;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .put(updateCustomerProfileRequest);

        ModelAssertions.assertThatModels(updateCustomerProfileRequest, updateCustomerProfileResponse).match();

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        assertEquals(updateProfileName, getCustomerProfileResponse.getName());
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
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequestWithText(BankAPIAlert.PERSON_NAME_VALIDATION_ERROR.getMessage()))
                .put(UpdateCustomerProfileRequest.builder()
                        .name(invalidName)
                        .build());

        GetCustomerProfileResponse getCustomerProfileResponse =
                new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        requestSpecification,
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK())
                        .get();

        assertNotEquals(invalidName, getCustomerProfileResponse.getName());
    }
}