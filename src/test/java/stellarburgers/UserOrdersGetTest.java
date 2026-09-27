package stellarburgers;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.data.UserGenerator;
import stellarburgers.model.Order;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DisplayName("Получение заказов пользователя: GET /api/orders")
class UserOrdersGetTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final IngredientClient ingredientClient = new IngredientClient();

    private String accessToken;

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизованный пользователь получает свои заказы")
    void getOrdersWithAuthReturnsUserOrders() {
        accessToken = userClient.register(UserGenerator.randomUser()).extract().path("accessToken");
        List<String> ingredients = ingredientClient.getIngredientIds().subList(0, 2);
        int orderNumber = orderClient.create(accessToken, new Order(ingredients)).extract().path("order.number");

        ValidatableResponse response = orderClient.getUserOrders(accessToken);

        response.statusCode(200)
                .body("success", is(true))
                .body("orders", hasSize(1))
                .body("orders[0].number", equalTo(orderNumber));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить заказы")
    void getOrdersWithoutAuthReturnsError() {
        ValidatableResponse response = orderClient.getUserOrdersWithoutAuth();

        response.statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
