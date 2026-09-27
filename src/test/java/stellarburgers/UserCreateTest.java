package stellarburgers;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import stellarburgers.client.UserClient;
import stellarburgers.data.UserGenerator;
import stellarburgers.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Создание пользователя: POST /api/auth/register")
class UserCreateTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Можно создать уникального пользователя")
    void createUniqueUserReturnsSuccess() {
        User user = UserGenerator.randomUser();

        ValidatableResponse response = userClient.register(user);
        accessToken = response.extract().path("accessToken");

        response.statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать пользователя, который уже зарегистрирован")
    void createAlreadyRegisteredUserReturnsError() {
        User user = UserGenerator.randomUser();
        accessToken = userClient.register(user).extract().path("accessToken");

        ValidatableResponse response = userClient.register(user);

        response.statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @ParameterizedTest(name = "email = {0}, password = {1}, name = {2}")
    @CsvSource(value = {
            "null, password123, Elena",
            "new_user@yandex.ru, null, Elena",
            "new_user@yandex.ru, password123, null"
    }, nullValues = "null")
    @DisplayName("Нельзя создать пользователя без обязательного поля")
    void createUserWithoutRequiredFieldReturnsError(String email, String password, String name) {
        User user = new User(email, password, name);

        ValidatableResponse response = userClient.register(user);
        accessToken = response.extract().path("accessToken");

        response.statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
