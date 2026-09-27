package stellarburgers;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.data.UserGenerator;
import stellarburgers.model.Order;
import stellarburgers.model.User;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Создание заказа: POST /api/orders")
class OrderCreateTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final IngredientClient ingredientClient = new IngredientClient();

    private User user;
    private String accessToken;
    private List<String> ingredients;

    @BeforeEach
    void setUp() {
        user = UserGenerator.randomUser();
        accessToken = userClient.register(user).extract().path("accessToken");
        ingredients = ingredientClient.getIngredientIds().subList(0, 2);
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизованный пользователь может создать заказ с ингредиентами")
    void createOrderWithAuthAndIngredientsReturnsSuccess() {
        ValidatableResponse response = orderClient.create(accessToken, new Order(ingredients));

        response.statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.owner.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Неавторизованный пользователь может создать заказ с ингредиентами")
    void createOrderWithoutAuthWithIngredientsReturnsSuccess() {
        ValidatableResponse response = orderClient.createWithoutAuth(new Order(ingredients));

        response.statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать заказ без ингредиентов")
    void createOrderWithoutIngredientsReturnsError() {
        ValidatableResponse response = orderClient.create(accessToken, new Order(List.of()));

        response.statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Нельзя создать заказ с неверным хешем ингредиента")
    void createOrderWithInvalidIngredientHashReturnsError() {
        ValidatableResponse response = orderClient.create(accessToken, new Order(List.of("invalid-ingredient-hash")));

        response.statusCode(500)
                .body(containsString("Internal Server Error"));
    }
}
