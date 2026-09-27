package stellarburgers.data;

import stellarburgers.model.User;

import java.util.UUID;

public class UserGenerator {

    private UserGenerator() {
    }

    public static User randomUser() {
        return new User(randomEmail(), randomPassword(), randomName());
    }

    public static String randomEmail() {
        return "user_" + randomSuffix() + "@yandex.ru";
    }

    public static String randomPassword() {
        return "pass_" + randomSuffix();
    }

    public static String randomName() {
        return "name_" + randomSuffix();
    }

    private static String randomSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
