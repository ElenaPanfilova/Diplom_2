package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.model.Order;

public class OrderClient extends BaseClient {

    private static final String ORDERS_PATH = "/api/orders";

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse create(String accessToken, Order order) {
        return spec()
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse createWithoutAuth(Order order) {
        return spec()
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Получить заказы пользователя с авторизацией")
    public ValidatableResponse getUserOrders(String accessToken) {
        return spec()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Получить заказы пользователя без авторизации")
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return spec()
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}
