package requests.steps;

import models.CreateUserRequest;
import models.LoginUserRequest;
import org.hamcrest.Matchers;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {

    public static void createAccounts(CreateUserRequest userRequest) {
        new CrudRequester(RequestSpecs.unauthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
                .header("Authorization", Matchers.notNullValue());
    }

    public static void depositMoney() {

    }
}
