package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.model.Credentials;
import stellarburgers.model.User;

public class UserClient extends BaseClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    @Step("Зарегистрировать пользователя")
    public ValidatableResponse register(User user) {
        return spec()
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Авторизоваться под пользователем")
    public ValidatableResponse login(Credentials credentials) {
        return spec()
                .body(credentials)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Изменить данные пользователя с авторизацией")
    public ValidatableResponse update(String accessToken, User changes) {
        return spec()
                .header("Authorization", accessToken)
                .body(changes)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Изменить данные пользователя без авторизации")
    public ValidatableResponse updateWithoutAuth(User changes) {
        return spec()
                .body(changes)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Удалить пользователя")
    public ValidatableResponse delete(String accessToken) {
        return spec()
                .header("Authorization", accessToken)
                .when()
                .delete(USER_PATH)
                .then();
    }
}
