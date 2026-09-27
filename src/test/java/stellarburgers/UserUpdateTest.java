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

@DisplayName("Изменение данных пользователя: PATCH /api/auth/user")
class UserUpdateTest {

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
    @DisplayName("Авторизованный пользователь может изменить email")
    void updateEmailWithAuthReturnsSuccess() {
        String newEmail = UserGenerator.randomEmail();
        User changes = new User(newEmail, null, null);

        ValidatableResponse response = userClient.update(accessToken, changes);

        response.statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить имя")
    void updateNameWithAuthReturnsSuccess() {
        String newName = UserGenerator.randomName();
        User changes = new User(null, null, newName);

        ValidatableResponse response = userClient.update(accessToken, changes);

        response.statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить пароль")
    void updatePasswordWithAuthReturnsSuccess() {
        String newPassword = UserGenerator.randomPassword();
        User changes = new User(null, newPassword, null);

        ValidatableResponse response = userClient.update(accessToken, changes);

        response.statusCode(200)
                .body("success", is(true));
        userClient.login(new Credentials(user.getEmail(), newPassword))
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить email")
    void updateEmailWithoutAuthReturnsError() {
        User changes = new User(UserGenerator.randomEmail(), null, null);

        ValidatableResponse response = userClient.updateWithoutAuth(changes);

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить имя")
    void updateNameWithoutAuthReturnsError() {
        User changes = new User(null, null, UserGenerator.randomName());

        ValidatableResponse response = userClient.updateWithoutAuth(changes);

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Без авторизации нельзя изменить пароль")
    void updatePasswordWithoutAuthReturnsError() {
        User changes = new User(null, UserGenerator.randomPassword(), null);

        ValidatableResponse response = userClient.updateWithoutAuth(changes);

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
