package stellarburgers;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.client.UserClient;
import stellarburgers.data.UserGenerator;
import stellarburgers.model.Credentials;
import stellarburgers.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Логин пользователя: POST /api/auth/login")
class UserLoginTest {

    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @BeforeEach
    void setUp() {
        user = UserGenerator.randomUser();
        accessToken = userClient.register(user).extract().path("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Можно залогиниться под существующим пользователем")
    void loginExistingUserReturnsSuccess() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());

        ValidatableResponse response = userClient.login(credentials);

        response.statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя залогиниться с неверным логином")
    void loginWithWrongEmailReturnsError() {
        Credentials credentials = new Credentials(UserGenerator.randomEmail(), user.getPassword());

        ValidatableResponse response = userClient.login(credentials);

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Нельзя залогиниться с неверным паролем")
    void loginWithWrongPasswordReturnsError() {
        Credentials credentials = new Credentials(user.getEmail(), UserGenerator.randomPassword());

        ValidatableResponse response = userClient.login(credentials);

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
