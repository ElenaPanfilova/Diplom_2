package stellarburgers.client;

import io.qameta.allure.Step;

import java.util.List;

public class IngredientClient extends BaseClient {

    private static final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Получить идентификаторы ингредиентов")
    public List<String> getIngredientIds() {
        return spec()
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }
}
