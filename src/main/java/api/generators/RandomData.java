package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {

    private RandomData() {}

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                RandomStringUtils.randomNumeric(3) + "$";
    }

    public static Double getRandomPositiveDecimalDeposit() {
        double value = ThreadLocalRandom.current().nextDouble(0.01, 5000.00);
        return Math.round(value * 100.0) / 100.0;
    }

    public static String getRandomUserUpdateProfile() {
        String firstName = RandomStringUtils.randomAlphabetic(8);
        String lastName = RandomStringUtils.randomAlphabetic(10);
        return firstName + " " + lastName;
    }
    
    public static Double getRandomNegativeDecimalDeposit() {
        double value = ThreadLocalRandom.current().nextDouble(-5000.00, -0.01);
        return Math.round(value * 100.0) / 100.0;
    }
}